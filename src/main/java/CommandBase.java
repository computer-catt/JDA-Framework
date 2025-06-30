import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.Nullable;

import javax.management.AttributeNotFoundException;
import javax.naming.CannotProceedException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

import static net.dv8tion.jda.api.interactions.commands.build.Commands.slash;

public abstract class CommandBase {
	public final static List<CommandBase> Commands = new ArrayList<>();

	public static class Group<T, T2, T3, T4> {
		public T t1;
		public T2 t2;
		public T3 t3;
		public T4 t4;

		public Group(T t1, T2 t2, T3 t3, T4 t4){
			this.t1 = t1;
			this.t2 = t2;
			this.t3 = t3;
			this.t4 = t4;
		}
	}


	/// Add the command if it's not already there
	public CommandBase(){
		Main.logger.info("Initializing " + CommandName());
		if (!Commands.contains(this))
			Commands.add(this);
	}

	/// DO NOT USE
	private final List<Group<String, VarHandle, CommandArgument, Object>> arguments = new ArrayList<>();
	/// do not run in constructor
	public List<Group<String, VarHandle, CommandArgument, Object>> getArguments() {
		if (!arguments.isEmpty()) return arguments;
		boolean optional = false;
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (!field.isAnnotationPresent(CommandArgument.class)) continue;
			field.setAccessible(true);
			CommandArgument annotation = field.getAnnotation(CommandArgument.class);

			if (optional && annotation.required())
				Main.logger.error("DISORDERED ARGUMENTS\nMake sure all optional arguments are last.");
			optional = !annotation.required();

			MethodHandles.Lookup lookup = MethodHandles.lookup().in(this.getClass());
			VarHandle handle = null;

			try {
				handle = lookup.unreflectVarHandle(field);
			} catch (IllegalAccessException e) {e.printStackTrace();}

			Object defaultValue = null;
			if (handle != null) defaultValue = handle.get(this);
			arguments.add(new Group<>(field.getName(), handle, annotation, defaultValue));
		}
		return arguments;
	}

	private String CommandName = null;
	public String CommandName(){
		if (CommandName != null) return CommandName;
		CommandName = this.getClass().getName();
		return CommandName;
	}

	public String CommandDelimiter() {return "\\|";}

	/// Command aliases
	public String[] CommandAliases() {return new String[]{};}

	private List<OptionData> options = null;
	/// Required for slash command arguments
	/// Optional if your command doesn't have any arguments
	public List<OptionData> Options() {
		if (options != null) return options;
		List<OptionData> options = new ArrayList<>();
		for (Group<String, VarHandle, CommandArgument, Object> argument : getArguments())
			options.add(new OptionData(
					argument.t3.OPTION_TYPE() == OptionType.UNKNOWN ? parseOptionType(argument.t2.varType()) : argument.t3.OPTION_TYPE(),
					argument.t1,
					argument.t3.description(),
					argument.t3.required()));

		this.options = options;
		return options;
	}

	/// Has no function, this is cosmetic
	public String Syntax() {
		String syntax = "";
		for (OptionData option : Options()) syntax += "<" + option.getName() + "> " + CommandDelimiter() + " ";
		return syntax.endsWith(" " + CommandDelimiter() + " ") ? syntax.substring(0, syntax.length() -3) : syntax;
	}

	/// NOTE:
	/// the first line of the description may not be over 100 characters in size
	/// cannot be empty
	public abstract String Description();

	public abstract void onMessageReceived(MessageReceivedEvent event);
	public abstract void onSlashCommandInteraction(SlashCommandInteractionEvent event);

	public void MessageCommand(MessageReceivedEvent event){
		resetArguments();
		if (!Options().isEmpty()){
			String prefixlessMessageContent = event.getMessage().getContentRaw();
			if (prefixlessMessageContent.indexOf(' ') != -1) prefixlessMessageContent = prefixlessMessageContent.substring(prefixlessMessageContent.indexOf(' ') + 1);
			else prefixlessMessageContent = "";

			String[] textArgs = !prefixlessMessageContent.isEmpty() ? prefixlessMessageContent.split(CommandDelimiter()) : new String[0];

			int index = 0, attachmentIndex = 0;
			List<Message.Attachment> attachments = event.getMessage().getAttachments();
			for (Group<String, VarHandle, CommandArgument, Object> argument : getArguments()) {
				if (argument.t2.varType() == Message.Attachment.class)
					argument.t2.set(this, attachments.get(attachmentIndex++));

				try {
					if (!argument.t3.required() && textArgs.length <= index) break;
					argument.t2.set(this, remapMessageTextToType(textArgs[index].trim(), argument.t2.varType(), event.getJDA(), event.isFromGuild() ? event.getGuild() : null));
					index++;
				} catch (IndexOutOfBoundsException e) {
					event.getChannel().sendMessage("Missing required argument: " + argument.t1 + "\n" + argument.t3.description() + "\n\n" + CompiledSyntax()).queue();
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		onMessageReceived(event);
	}

	public void resetArguments(){
		for (Group<String, VarHandle, CommandArgument, Object> arg : getArguments()) arg.t2.set(this, arg.t4);
	}

	public void SlashCommand(SlashCommandInteractionEvent event){
		long startTime = System.nanoTime();
		resetArguments();
		for (Group<String, VarHandle, CommandArgument, Object> argument : getArguments())
			try {
				argument.t2.set(this, getOptionTypeValue(event.getOption(argument.t1), argument.t2.varType()));}
			catch (AttributeNotFoundException _){}
			catch (Exception e){ e.printStackTrace();}

		long duration = (System.nanoTime() - startTime); // in nanoseconds
		onSlashCommandInteraction(event);
		System.out.println(duration);
	}

	public String CompiledSyntax() {return "Syntax: " + Main.prefix + CommandName() + " " + Syntax();}

	/// Updates bot commands, to be called after all commands are initialized
	public static void UpdateCommands(JDA api){
		CommandListUpdateAction action = api.updateCommands();
		Commands.forEach(meow -> action.addCommands(slash(meow.CommandName().toLowerCase(), meow.Description().split("\n")[0]).addOptions(meow.Options()))); // .substring(0, Math.min(meow.Description().length(), 100))
		action.queue();
	}

	OptionType parseOptionType(Type type){
		if (type == String.class) return OptionType.STRING;
		if (type == Integer.class || type == int.class) return OptionType.INTEGER;
		if (type == Boolean.class || type == boolean.class) return OptionType.BOOLEAN;
		if (type == User.class) return OptionType.USER;
		if (type == GuildChannel.class) return OptionType.CHANNEL;
		if (type == Role.class) return OptionType.ROLE;
		if (type == Long.class ||type == long.class ||
				type == Double.class || type == double.class) return OptionType.NUMBER;
		if (type == IMentionable.class) return OptionType.MENTIONABLE;
		if (type == Message.Attachment.class) return OptionType.ATTACHMENT;
		return OptionType.UNKNOWN;
	}

	Object getOptionTypeValue(OptionMapping mapping, Type type) throws AttributeNotFoundException, CannotProceedException {
		if (mapping == null) throw new AttributeNotFoundException("Argument not found");
		if (type == String.class) return mapping.getAsString();
		if (type == Integer.class || type == int.class) return mapping.getAsInt();
		if (type == Boolean.class || type == boolean.class) return mapping.getAsBoolean();
		if (type == User.class) return mapping.getAsUser();
		if (type == GuildChannel.class) return mapping.getAsChannel();
		if (type == Role.class) return mapping.getAsRole();
		if (type == Long.class || type == long.class) return mapping.getAsLong();
		if (type == Double.class || type == double.class) return mapping.getAsDouble();
		if (type == IMentionable.class) return mapping.getAsMentionable();
		if (type == Message.Attachment.class) return mapping.getAsAttachment();
		throw new CannotProceedException("Cannot map");
	}

	Object remapMessageTextToType(String string, Type type, JDA api, @Nullable Guild guild) throws CannotProceedException {
		if (type == String.class) return string;
		if (type == Integer.class || type == int.class) return Integer.parseInt(string);
		if (type == Boolean.class || type == boolean.class) return remapStringToBoolean(string);
		if (type == User.class) return remapStringToUser(string, api, guild);
		if (type == GuildChannel.class){
			if (guild == null) return null;
			String s1 = string;
			if (s1.startsWith("<#") && s1.endsWith(">"))
				s1 = s1.substring(2, s1.length() -1);

			List<GuildChannel> options;
			for (GuildChannel a : options = guild.getChannels()){
				if (a.getId().equals(s1)) return a;
				if (a.getName().equals(string)) return a;
				if (a.getName().equalsIgnoreCase(string)) return a;
				if (a.getName().contains(string)) return a;
				if (a.getName().toLowerCase().contains(string.toLowerCase())) return a;
			}

			return options.stream()
					.min(Comparator.comparingInt(o -> levenshtein(string, o.getName())))
					.orElse(null);
		}
		if (type == Role.class) {
			if (guild == null) return null;
			String s1 = string;
			if (s1.startsWith("<#") && s1.endsWith(">"))
				s1 = s1.substring(2, s1.length() -1);

			List<Role> options;
			for (Role a : options = guild.getRoles()){
				if (a.getId().equals(s1)) return a;
				if (a.getName().equals(string)) return a;
				if (a.getName().equalsIgnoreCase(string)) return a;
				if (a.getName().contains(string)) return a;
				if (a.getName().toLowerCase().contains(string.toLowerCase())) return a;
			}

			return options.stream()
					.min(Comparator.comparingInt(o -> levenshtein(string, o.getName())))
					.orElse(null);
		}
		if (type == Long.class || type == long.class) return Long.parseLong(string);
		if (type == Double.class || type == double.class) return Double.parseDouble(string);
		//if (type == IMentionable.class) {} idk how to do this :sob it seems too generic, id have to just put every imentionable impl checking here iirc
		//if (type == Message.Attachment.class) return attachments.get(); NOT TEXT PARSABLE, IMPLEMENTED ELSEWHERE
		throw new CannotProceedException("Cannot map");
	}

	public boolean remapStringToBoolean(String string){
		return string.equalsIgnoreCase("true") ||
				string.equalsIgnoreCase("yes") ||
				string.equalsIgnoreCase("yah") ||
				string.equalsIgnoreCase("yeah");
	}

	public User remapStringToUser(String string, JDA api, Guild guild){
		boolean verbose = true;
		String s1 = string;
		if (verbose) System.out.println("Id comparison");
		// id comparison, direct mention
		if (string.startsWith("<@") && string.endsWith(">"))
			s1 = string.substring(2).substring(0, string.length() -3);

		User user = null;
		try {
			if (api != null && (user = api.getUserById(s1)) != null) return user;
		} catch (Exception _) {}

		if (verbose) System.out.println("direct username");
		// direct username match
		if (string.length() > 1 && api != null && (user = api.getUserByTag(string.length() < 5 || string.charAt(string.length()-4) != '#' ? string + "#0000" :string )) != null)
			return user;

		if (verbose) System.out.println("contains");
		Optional<Member> member;
		if (guild != null)
			if ((member = guild.getMembers().stream().filter(e -> e.getEffectiveName().contains(string) || e.getUser().getName().contains(string)).findAny()).isPresent())
				return member.get().getUser();

		if (verbose) System.out.println("levenshtien");
		// levenshtien comparisons
		if (guild != null)
			if ((member = guild.getMembers().stream().min(Comparator.comparingInt(o -> Math.min(levenshtein(string, o.getEffectiveName()), levenshtein(string, o.getUser().getName()))))).isPresent())
				return member.get().getUser();

		return user;
	}

	public static int levenshtein(String a, String b) {
		int[][] dp = new int[a.length() + 1][b.length() + 1];
		for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
		for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
		for (int i = 1; i <= a.length(); i++)
			for (int j = 1; j <= b.length(); j++)
				dp[i][j] = Math.min(
						Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
						dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1)
				);
		return dp[a.length()][b.length()];
	}
}