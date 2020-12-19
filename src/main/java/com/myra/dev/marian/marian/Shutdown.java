package com.myra.dev.marian.marian;

import com.myra.dev.marian.Bot;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.MessageReaction;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

@CommandSubscribe(
        name = "shutdown",
        requires = Permissions.MARIAN
)
public class Shutdown implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Access only for Marian
        if (!ctx.getAuthor().getId().equals(Config.marian)) return;
        EmbedBuilder shutdown = new EmbedBuilder()
                .setAuthor("shutdown", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Wait what!? You want me to take a break? Are you sure?");
        Message message = ctx.getChannel().sendMessage(shutdown.build()).complete();
        // Add reaction
        message.addReaction("\u2705").queue();
        // Add the message
        MessageReaction.add(ctx.getGuild(), "shutdown", message, ctx.getAuthor(),true, "\u2705");
    }

    public void exitProgram(GuildMessageReactionAddEvent event) {
        // Check for right message
        if (!MessageReaction.check(event, "shutdown", true)) return;
        // Clear all reactions
        event.getChannel().retrieveMessageById(event.getMessageId()).complete().clearReactions().queue();
        // Shutdown JDA
        event.getJDA().shutdown();
        // Shutdown whole program
        System.exit(0);
    }
}
