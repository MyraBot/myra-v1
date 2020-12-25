package com.myra.dev.marian.listeners.welcome.WelcomeImage;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.concurrent.TimeUnit;

@CommandSubscribe(
        name = "welcome image font",
        requires = Permissions.ADMINISTRATOR
)
public class WelcomeImageFont implements Command {
    private final String[] emojis = {
            "1\uFE0F\u20E3", // 1️⃣
            "2\uFE0F\u20E3", // 2️⃣
            "3\uFE0F\u20E3" // 3️⃣
    };

    @Override
    public void execute(CommandContext ctx) throws Exception {
        Utilities utilities = Utilities.getUtils(); // Get utilities
        //change font
        EmbedBuilder fontSelection = new EmbedBuilder()
                .setAuthor("welcome image font", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .addField("fonts",
                        "1\uFE0F\u20E3 default" +
                                "\n2\uFE0F\u20E3 modern" +
                                "\n3\uFE0F\u20E3 handwritten",
                        false);
        ctx.getChannel().sendMessage(fontSelection.build()).queue(message -> { // Send message
            //add reactions
            message.addReaction(emojis[0]).queue();
            message.addReaction(emojis[1]).queue();
            message.addReaction(emojis[2]).queue();

            // Event waiter
            ctx.waiter().waitForEvent(
                    GuildMessageReactionAddEvent.class, // Event to wait for
                    e -> // Condition
                            !e.getUser().isBot()
                                    && e.getUser() == ctx.getAuthor()
                                    && e.getMessageId().equals(message.getId()),
                    e -> { // Run on event
                        final Database db = new Database(e.getGuild()); // Get database
                        final String reaction = e.getReactionEmote().getEmoji(); // Get reacted emoji

                        EmbedMessage.Success success = new EmbedMessage.Success()
                                .setCommand("welcome image font")
                                .setEmoji("\uD83D\uDDDB")
                                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
                        // Fonts
                        if (reaction.equals(emojis[0])) {
                            db.getNested("welcome").setString("welcomeImageFont", "default"); // Update database
                            success.setMessage("You have changed the font to `default`").send(ctx.getChannel());
                        }
                        if (reaction.equals(emojis[1])) {
                            db.getNested("welcome").setString("welcomeImageFont", "modern"); // Update database
                            success.setMessage("You have changed the font to `modern`").send(ctx.getChannel());
                        }
                        if (reaction.equals(emojis[2])) {
                            db.getNested("welcome").setString("welcomeImageFont", "handwritten"); // Update database
                            success.setMessage("You have changed the font to `handwritten`").send(ctx.getChannel());
                        }
                    },
                    30, TimeUnit.SECONDS,
                    () -> { // Run on timeout
                        message.clearReactions().queue(); // Clear reactions
                        utilities.error(ctx.getChannel(), "welcome image font", "\uD83D\uDDDB", "Too slow", "You took too long to react", ctx.getAuthor().getEffectiveAvatarUrl()); // Error
                    }
            );
        });
    }
}
