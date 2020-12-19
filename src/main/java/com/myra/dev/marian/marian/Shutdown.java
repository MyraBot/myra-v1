package com.myra.dev.marian.marian;

import com.myra.dev.marian.Bot;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.concurrent.TimeUnit;

@CommandSubscribe(
        name = "shutdown",
        requires = Permissions.MARIAN
)
public class Shutdown implements Command {
    private final String emoji = "\u2705"; // ✅

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Access only for Marian
        if (!ctx.getAuthor().getId().equals(Config.marian)) return;
        EmbedBuilder shutdown = new EmbedBuilder()
                .setAuthor("shutdown", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Wait what!? You want me to take a break? Are you sure?");
        ctx.getChannel().sendMessage(shutdown.build()).queue(message -> { // Send shutdown request
            message.addReaction(emoji).queue(); // Add reaction

            // Event waiter
            ctx.waiter().waitForEvent(
                    GuildMessageReactionAddEvent.class, // Event to wait for
                    e -> // Condition
                            !e.getUser().isBot()
                                    && e.getUser() == ctx.getAuthor()
                                    && e.getMessageId().equals(message.getId()),
                    e -> { // Code on event
                        message.clearReactions().queue(); // Clear reactions
                        Bot.shardManager.setStatus(OnlineStatus.OFFLINE); // Set status to offline
                        e.getJDA().shutdown(); // Shutdown JDA
                        System.exit(0);  // Shutdown whole program
                    },
                    30, TimeUnit.SECONDS,
                    () -> message.clearReactions().queue()
            );
        });
    }
}
