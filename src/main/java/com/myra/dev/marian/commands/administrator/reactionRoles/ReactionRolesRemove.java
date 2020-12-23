package com.myra.dev.marian.commands.administrator.reactionRoles;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import org.bson.Document;

import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandSubscribe(
        name = "reaction roles remove",
        aliases = {"reaction role", "rr remove"},
        requires = Permissions.ADMINISTRATOR
)
public class ReactionRolesRemove implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        EmbedBuilder usage = new EmbedBuilder()
                .setAuthor("reaction roles remove", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Remove the reaction of the reaction role");

        ctx.getChannel().sendMessage(usage.build()).queue(msg -> {
            ctx.waiter().waitForEvent(
                    GuildMessageReactionRemoveEvent.class, // Event to wait for
                    e -> !e.getUser().isBot()
                            && e.getUser() == ctx.getAuthor(),
                    e -> { // Code on event
                        final Database db = new Database(ctx.getGuild()); // Get database
                        final List<Document> reactionRoles = db.getList("reactionRoles", Document.class); // Get reaction roles

                        final String reactionEmoji = e.getReactionEmote().getEmoji(); // Get emoji of removed reaction
                        final String reactionMessage = e.getMessageId(); // Get message id of reaction message

                        // Reacted message isn't a reaction role
                        if (reactionRoles.stream().noneMatch(reactionRole -> reactionMessage.equals(reactionRole.getString("message")) && reactionEmoji.equals(reactionRole.getString("emoji")))) {
                            EmbedMessage.Error error = new EmbedMessage.Error()
                                    .setCommand("reaction roles remove")
                                    .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                                    .setMessage("This is not a reaction role");
                            error.send(ctx.getChannel());
                            return;
                        }

                        for (Document reactionRole : reactionRoles) {
                            // Check every reaction role
                            final String messageId = reactionRole.getString("message"); // Get message id
                            final String emoji = reactionRole.getString("emoji"); // Get reaction emoji

                            // Remove reaction
                            if (reactionMessage.equals(messageId) && reactionEmoji.equals(emoji)) {
                                reactionRoles.remove(reactionRole); // Remove reaction role
                                db.set("reactionRoles", reactionRoles); // Update database
                                e.retrieveMessage().queue(message -> message.removeReaction(emoji, e.getJDA().getSelfUser()).queue()); // Remove reaction from message

                                // Send success message
                                EmbedMessage.Success success = new EmbedMessage.Success()
                                        .setCommand("reaction roles remove")
                                        .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                                        .setMessage("Deleted reaction role");
                                success.send(e.getChannel());
                                break;
                            }
                        }
                    },
                    30L, TimeUnit.SECONDS, // Timeout
                    () -> { // Code on timeout
                        EmbedMessage.Error error = new EmbedMessage.Error()
                                .setCommand("reaction roles remove")
                                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                                .setMessage("You didn't remove a reaction");
                        error.send(ctx.getChannel());
                    }
            );
        });
    }
}