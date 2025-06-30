import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/// A SAMPLE COMMAND TO SHOW ARGUMENTS
/// should probably not include this in your bot as people could say weird stuff and report your bot's messages (you'd be responsible).
public class EchoRole extends CommandBase {
	public String Description() {return "produces an ECHO- echo- echo- echo- echo- of a role";}

	@CommandArgument(description = "The role you want to echo")
	public Role role;

	@CommandArgument(description = "How much times do you wanna do that?", required = false)
	public int times = -1;

	public void onMessageReceived(MessageReceivedEvent event) {
		if (role == null){
			event.getChannel().sendMessage("is null stupid").queue();
			return;
		}
		event.getChannel().sendMessage(role.getName()).queue();
		for (int i = 0; i < times-1; i++)
			event.getChannel().sendMessage(role.getName()).queue();
	}

	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		event.reply(role.getName()).queue();
		for (int i = 0; i < times-1; i++)
			event.getChannel().sendMessage(role.getName()).queue();
	}
}