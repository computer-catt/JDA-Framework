import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/// A SAMPLE COMMAND TO SHOW ARGUMENTS
/// should probably not include this in your bot as people could say weird stuff and report your bot's messages (you'd be responsible).
public class Echo extends CommandBase {
	public String Description() {return "produces an ECHO- echo- echo- echo- echo-";}

	@CommandArgument(description = "The text you want to echo")
	public String text;

	@CommandArgument(description = "How much times do you wanna do that?", required = false)
	public int times = -1;

	public void onMessageReceived(MessageReceivedEvent event) {
		event.getChannel().sendMessage(text).queue();
		for (int i = 0; i < times-1; i++)
			event.getChannel().sendMessage(text).queue();
	}

	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		event.reply(text).queue();
		for (int i = 0; i < times-1; i++)
			event.getChannel().sendMessage(text).queue();
	}
}