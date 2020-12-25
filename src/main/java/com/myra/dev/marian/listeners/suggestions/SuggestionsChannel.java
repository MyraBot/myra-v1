package com.myra.dev.marian.listeners.suggestions;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

@CommandSubscribe(
        name = "suggestions channel",
        requires = Permissions.ADMINISTRATOR
)
public class SuggestionsChannel implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 1) {
            // Usage
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("suggestions", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "suggestions channel <channel>`", "\uD83D\uDCC1 │ Set the channel in which the suggestions should go", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }

        //connect to database
        Database db = new Database(ctx.getGuild());
        // Get given channel
        TextChannel channel = utilities.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "suggestions", "\uD83D\uDDF3");
        if (channel == null) return;
        // Success
        EmbedMessage.Success success = new EmbedMessage.Success()
                .setCommand("suggestions")
                .setEmoji("\uD83D\uDDF3")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
        //remove suggestions channel
        if (db.getString("suggestionsChannel").equals(channel.getId())) {
            db.set("suggestionsChannel", "not set"); // Update database
            success.setMessage("Suggestions are no longer sent in " + ctx.getGuild().getTextChannelById(db.getString("suggestionsChannel")).getAsMention()).send(ctx.getChannel()); // Success
        } else {
            db.set("suggestionsChannel", channel.getId()); // Update database
            // Success messages
            success.setMessage("Suggestions are now sent in " + channel.getAsMention()).send(ctx.getChannel());
            success.setMessage("Suggestions are now send in here").send(channel);
        }
    }
}
