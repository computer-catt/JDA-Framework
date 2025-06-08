import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class Help extends CommandBase{
    public String CommandName() {return "Help";}
    public String[] CommandAliases() {return new String[]{"h"};}
    public String Description() {return "Returns this list.";}

    /// limitations as listed in the [discord js embeds guide](https://discordjs.guide/popular-topics/embeds.html#editing-the-embedded-message-content)
    public MessageEmbed[] getEmbed(){
        int embedsAmount = (Commands.size() / 25) + 1;
        System.out.println(embedsAmount);
        MessageEmbed[] embeds = new MessageEmbed[embedsAmount];

            for (int i = 0; i < embeds.length; i++) {
            EmbedBuilder builder = new EmbedBuilder();
            if (i == 0) builder.setTitle("Help").setDescription("Welcome to the help command! your guide for anything around this bot.");
            for (int j = 0, k; j < 25; j++) {
                if (Commands.size() == (k = i*25 + j)) break;
                CommandBase command = CommandBase.Commands.get(k);
                builder.addField("__" + command.CommandName() + "__",
                        command.Description() + "\n" +
                                "-# Aliases: `" + Arrays.toString(command.CommandAliases()) + "`\n" +
                                "-# Syntax: `" + Main.prefix + command.CommandName() + " " + command.Syntax() + "`\n\u200B", false);
            }
            embeds[i] = builder.build();
        }

        return embeds;
    }

    public void onMessageReceived(MessageReceivedEvent event) {event.getChannel().sendMessageEmbeds(Arrays.asList(getEmbed())).queue();}
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {event.replyEmbeds(Arrays.asList(getEmbed())).queue();}
}