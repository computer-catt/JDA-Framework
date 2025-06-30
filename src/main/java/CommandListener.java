import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {
    public void onReady(ReadyEvent event) {
        CommandBase.UpdateCommands(event.getJDA());
        Main.logger.info("Initialized and updated commands");
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        if (message.isEmpty()) return;
        if (message.charAt(0) != Main.prefix) return;
        Main.logger.info(message);

        String prefixLess = message.substring(1).toLowerCase().split(" ")[0];

        for (CommandBase command : CommandBase.Commands) {
            if (command.CommandName().toLowerCase().equals(prefixLess)) {
                command.MessageCommand(event);
                return;
            } else
                for (String commandStr : command.CommandAliases())
                    if (commandStr.toLowerCase().equals(prefixLess)) {
                        command.MessageCommand(event);
                        return;
                    }
        }
        event.getChannel().sendMessage("Command not found!").queue();
    }

    /// goes through the commands and checks if the command context matches
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        for (CommandBase command : CommandBase.Commands)
            if (command.CommandName().equalsIgnoreCase(event.getName())) {
                command.SlashCommand(event);
                break;
            }
    }
}