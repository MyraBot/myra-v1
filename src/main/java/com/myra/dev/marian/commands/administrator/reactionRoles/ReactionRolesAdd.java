package com.myra.dev.marian.commands.administrator.reactionRoles;

import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

@CommandSubscribe(
        name = "reaction roles add",
        aliases = {"rr add"},
        requires = Permissions.ADMINISTRATOR
)
public class ReactionRolesAdd implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (!MongoDb.getInstance().getCollection("guilds").find(eq("guildId", ctx.getGuild().getId())).first().getBoolean("premium")) return;

        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("reaction roles", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .addField("`" + ctx.getPrefix() + "reaction roles add <role>`", "", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Add reaction roles
        final Role role = Utilities.getUtils().getRole(ctx.getEvent(), ctx.getArguments()[0], "reaction roles add", ""); // Get given role
        if (role == null) return;

        if (!ReactionRolesManager.reactionRoles.containsKey(ctx.getGuild())) { // Guild isn't in the HashMap yet
            ReactionRolesManager.reactionRoles.put(ctx.getGuild().getId(), new HashMap<>()); // Add guild to hashmap
        }
        if (ReactionRolesManager.reactionRoles.get(ctx.getGuild().getId()).containsKey(ctx.getAuthor())) {
            Utilities.getUtils().error(ctx.getChannel(), "reaction roles add", "", "You already started adding a reaction role", "Canceling adding the reaction role", ctx.getAuthor().getEffectiveAvatarUrl()); // Send error
            ReactionRolesManager.reactionRoles.get(ctx.getGuild().getId()).remove(ctx.getAuthor().getId()); // Remove user from reaction roles setup
            return;
        }

        // Create reaction roles document
        Document reactionRolesInfo = new Document()
                .append("role", role.getId()) // Store role id
                .append("message", null) // Add message id key
                .append("emoji", null) // Add emoji key
                .append("type", null); // Add type id
        ReactionRolesManager.reactionRoles.get(ctx.getGuild().getId()).put(ctx.getAuthor().getId(), reactionRolesInfo); // Add setup document

        EmbedBuilder type = new EmbedBuilder()
                .setAuthor("reaction roles add", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Choose a reaction role type:")
                .addField("normal", "\uD83C\uDF81 │ Be able to get unlimited reaction roles. If you remove your reaction your role will get removed", true)
                .addField("unique", "\uD83E\uDD84 │ You can only get 1 role from a message at the same time", true)
                .addField("verify", "\u2705 │ Once you reacted to the message and get you're role, you're not able to remove the role", true);
        ctx.getChannel().sendMessage(type.build()).queue(message -> {
            // Add reactions
            message.addReaction("\uD83C\uDF81").queue(); // 🎁
            message.addReaction("\uD83E\uDD84").queue(); // 🦄
            message.addReaction("\u2705").queue(); // ✅

            com.myra.dev.marian.utilities.MessageReaction.add(ctx.getGuild(), "reaction roles add", message, ctx.getAuthor(), false, "\uD83C\uDF81", "\uD83E\uDD84", "\u2705");

            Utilities.TIMER.schedule(() -> {
                if (com.myra.dev.marian.utilities.MessageReaction.reactions.get(ctx.getGuild().getId()).get("reaction roles add").get(message.getId()) != null) {
                    com.myra.dev.marian.utilities.MessageReaction.remove("reaction roles add", message); // Remove reaction
                    Utilities.getUtils().error(ctx.getChannel(), "reaction roles add", "", "You took too long", "Canceled adding the reaction role", ctx.getAuthor().getEffectiveAvatarUrl());
                }
            }, 1, TimeUnit.MINUTES);
        });
    }

    public void typeSelection(GuildMessageReactionAddEvent event) {
        if (!com.myra.dev.marian.utilities.MessageReaction.check(event, "reaction roles add", true)) return;

        if (!ReactionRolesManager.reactionRoles.containsKey(event.getGuild().getId()))
            return; // Guild isn't setting up a reaction role
        if (!ReactionRolesManager.reactionRoles.get(event.getGuild().getId()).containsKey(event.getUserId()))
            return; // User isn't setting up a reaction role
        if (ReactionRolesManager.reactionRoles.get(event.getGuild().getId()).get(event.getUserId()).getString("type") != null)
            return; // Type is already chosen

        event.retrieveMessage().queue(message -> { // Retrieve message
            // Choose reaction role type
            String type = null;
            if (event.getReactionEmote().getEmoji().equals("\uD83C\uDF81")) type = "normal"; // 🎁
            if (event.getReactionEmote().getEmoji().equals("\uD83E\uDD84")) type = "unique"; // 🦄
            if (event.getReactionEmote().getEmoji().equals("\u2705")) type = "verify"; // ✅

            message.clearReactions().queue(); // Clear reactions

            final Document reactionRolesInfo = ReactionRolesManager.reactionRoles.get(event.getGuild().getId()).get(event.getUserId()); // Get reaction roles information document
            reactionRolesInfo.replace("type", type); // Set reaction roles type

            EmbedBuilder messageAndEmojiSelection = new EmbedBuilder()
                    .setAuthor("reaction roles add", null, event.getUser().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setDescription("Now react to the message you want the reaction role to be");
            message.editMessage(messageAndEmojiSelection.build()).queue(); // Edit message to select the reaction roles message and emoji
        });
    }


    public void messageSelection(GuildMessageReactionAddEvent event) {
        if (!ReactionRolesManager.reactionRoles.containsKey(event.getGuild().getId()))
            return; // Guild isn't setting up a reaction role
        if (!ReactionRolesManager.reactionRoles.get(event.getGuild().getId()).containsKey(event.getUserId()))
            return; // User isn't setting up a reaction role
        if (ReactionRolesManager.reactionRoles.get(event.getGuild().getId()).get(event.getUserId()).getString("type") == null)
            return; // A type isn't chosen yet

        final MessageReaction.ReactionEmote emote = event.getReactionEmote(); // Get reaction emote as a variable

        String emoji = null; // Store emoji
        if (emote.isEmoji()) emoji = emote.getEmoji(); // Save emoji
        else if (emote.isEmoji()) emoji = emote.getEmote().getId(); // Save emote

        final Document reactionRolesInfo = ReactionRolesManager.reactionRoles.get(event.getGuild().getId()).get(event.getUserId()); // Get reaction roles information document
        reactionRolesInfo.replace("message", event.getMessageId()); // Add message id
        reactionRolesInfo.replace("emoji", emoji); // Emoji

        EmbedBuilder messageAndEmojiSelection = new EmbedBuilder()
                .setAuthor("reaction roles add", null, event.getUser().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Successfully added");
        event.getChannel().sendMessage(messageAndEmojiSelection.build()).queue(); // Send success message

        event.getChannel().retrieveMessageById(reactionRolesInfo.getString("message")).queue(message -> { // Get reaction roles message
            message.addReaction(reactionRolesInfo.getString("emoji")).queue(); // Add reaction
        });

        final Document guildDocument = MongoDb.getInstance().getCollection("guilds").find(eq("guildId", event.getGuild().getId())).first(); // Get guild document
        List<Document> reactionRoles = guildDocument.getList("reactionRoles", Document.class); // Get reaction roles list
        reactionRoles.add(reactionRolesInfo); // Add reaction roles info

        MongoDb.getInstance().getCollection("guilds").findOneAndReplace(eq("guildId", event.getGuild().getId()), guildDocument); // Update guild document
        ReactionRolesManager.reactionRoles.get(event.getGuild().getId()).remove(event.getUser().getId()); // Remove user from reaction roles setup state
    }
}
