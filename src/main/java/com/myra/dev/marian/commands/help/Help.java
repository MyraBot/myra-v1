package com.myra.dev.marian.commands.help;

import com.myra.dev.marian.Bot;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.CommandEmbeds;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.MessageReaction;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

@CommandSubscribe(
        name = "help",
        aliases = {"help me"}
)
public class Help implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        //check for no arguments
        if (ctx.getArguments().length != 0) return;
        Utilities utilities = Utilities.getUtils();
        //embed
        EmbedBuilder help = new EmbedBuilder()
                .setAuthor("help", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setThumbnail(ctx.getEvent().getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setDescription(ctx.getEvent().getJDA().getSelfUser().getName() + " is a multi-purpose bot featuring moderation, music, welcoming and much more!\n" +
                        "If you found a bug please report it in " + utilities.hyperlink("my Discord server", "https://discord.gg/nG4uKuB") + " or write me (" + ctx.getEvent().getJDA().getUserById(Config.marian).getAsTag() + ") a direct message. For suggestions join the server as well!\n" +
                        "A moderator role must have `View Audit Log` permission to use the moderation commands. To see all available commands type in `" + ctx.getPrefix() + "commands`")
                .addField("**\u2709\uFE0F │ invite**", utilities.hyperlink("Invite ", "https://discord.gg/nG4uKuB") + ctx.getEvent().getJDA().getSelfUser().getName() + " to your server", true)
                .addField("**\u26A0\uFE0F │ support**", utilities.hyperlink("Report ", "https://discord.gg/nG4uKuB") + " bugs and get " + utilities.hyperlink("help ", "https://discord.gg/nG4uKuB"), true);
        ctx.getChannel().sendMessage(help.build()).queue(message -> {
            // Add reactions
            message.addReaction("\u2709\uFE0F").queue();
            message.addReaction("\u26A0\uFE0F").queue();

            MessageReaction.add(ctx.getGuild(), "help", message, ctx.getAuthor(), true, "\u2709\uFE0F", "\u26A0\uFE0F");
        });
    }

    //reactions
    public void guildMessageReactionAddEvent(GuildMessageReactionAddEvent event) throws Exception {
        // If reaction was added on the wrong message return
        if (!MessageReaction.check(event, "help", true)) return;

        CommandEmbeds embed = new CommandEmbeds(event.getGuild(), event.getJDA(), event.getUser(), new Database(event.getGuild()).getString("prefix"));
        //invite bot
        if (event.getReactionEmote().getEmoji().equals("\u2709\uFE0F") && !event.getMember().getUser().isBot()) {
            event.retrieveMessage().queue(message -> {
                message.editMessage(embed.inviteJda().build()).queue(); // Edit message
                message.clearReactions().queue(); // Clear reactions
            });
        }
        //support server
        if (event.getReactionEmote().getEmoji().equals("\u26A0\uFE0F") && !event.getMember().getUser().isBot()) {
            event.retrieveMessage().queue(message -> {
                message.editMessage(embed.supportServer().build()).queue(); // Edit message
                message.clearReactions().queue(); // Clear reactions
            });
        }
    }
}

