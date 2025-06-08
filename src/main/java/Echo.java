import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/// A SAMPLE COMMAND TO SHOW ARGUMENTS
///
/// should probably not include this in your bot as people could say weird stuff and report your bot's messages (you'd be responsible).
public class Echo extends CommandBase{
    public String CommandName() {
        return "Echo";
    }
    public OptionData[] Options() {return new OptionData[]{
            new OptionData(OptionType.STRING, "text", "the text you want to echo", true)};}
    public String Description() {return "produces an ECHO- echo- echo- echo- echo-";}

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentRaw();
        messageContent = messageContent.substring(messageContent.indexOf(' '));
        event.getChannel().sendMessage(messageContent).queue();
    }

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        event.reply(event.getOption("text").getAsString()).queue();}
}