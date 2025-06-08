import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandBase {
    public final static List<CommandBase> Commands = new ArrayList<>();

    /// Add the command if it's not already there
    public CommandBase(){
        if (!Commands.contains(this))
            Commands.add(this);
    }

    /// Should follow PascalCase (ex: "RemoveTrade")
    public abstract String CommandName();

    /// Command aliases
    public String[] CommandAliases() {return new String[]{};}

    /// Required for slash command arguments
    /// Optional if your command doesn't have any arguments
    public OptionData[] Options() {return new OptionData[0];}

    /// Has no function, this is cosmetic
    public String Syntax() {
        String syntax = "";
        for (OptionData option : Options()) syntax += "<" + option.getName() + "> $ ";
        return syntax.endsWith(" $ ") ? syntax.substring(0, syntax.length() -3) : syntax;
    }

    /// NOTE:
    /// the first line of the description may not be over 100 characters in size
    /// cannot be empty
    public abstract String Description();

    public abstract void onMessageReceived(MessageReceivedEvent event);
    public abstract void onSlashCommandInteraction(SlashCommandInteractionEvent event);

    public String CompiledSyntax() {return "Syntax: " + Main.prefix + CommandName() + " " + Syntax();}

    /// Updates bot commands, to be called after all commands are initialized
    public static void UpdateCommands(JDA api){
        CommandListUpdateAction action = api.updateCommands();
        Commands.forEach(meow -> action.addCommands(net.dv8tion.jda.api.interactions.commands.build.Commands.slash(meow.CommandName().toLowerCase(), meow.Description().split("\n")[0]).addOptions(meow.Options()))); // .substring(0, Math.min(meow.Description().length(), 100))
        action.queue();
    }
}