import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Ping extends CommandBase{
    public String CommandName() {
        return "Ping";
    }
    public String[] CommandAliases() {
        return new String[]{"p"};
    }
    public String Description() {return "Totally pings the bot :3\nTry it";}

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        event.getChannel().sendMessage(event.getJDA().getGatewayPing() + " ms").queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        event.reply(event.getJDA().getGatewayPing() + " ms").queue();
    }
}