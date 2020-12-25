package com.myra.dev.marian.commands.administrator;

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
        name = "log channel",
        aliases = {"logging channel", "logs channel"},
        requires = Permissions.ADMINISTRATOR
)
public class LogChannel implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        Utilities utilities = Utilities.getUtils(); // Get utilities
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("log channel", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "log channel <channel>`", "\uD83E\uDDFE │ Set the channel where logging messages go", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }

        // Change log channel
        TextChannel channel = utilities.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "log channel", "\uD83E\uDDFE"); // Get channel
        if (channel == null) return;

        Database db = new Database(ctx.getGuild()); // Get database
        // Remove log channel
        if (channel.getId().equals(db.getString("logChannel"))) {
            // Update database
            db.set("logChannel", "not set");
            // Send success message
            EmbedMessage.Success success = new EmbedMessage.Success()
                    .setCommand("log channel")
                    .setEmoji( "\uD83E\uDDFE")
                    .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                    .setMessage("Logs are no longer send in " + channel.getAsMention());
            success.send(ctx.getChannel());
        }
        // Change log channel
        else {
            // Update database
            db.set("logChannel", channel.getId());
            // Success message
            EmbedBuilder logChannel = new EmbedBuilder()
                    .setAuthor("log channel", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.blue)
                    .addField("\uD83E\uDDFE │ Log channel changed", "Log channel changed to **" + channel.getName() + "**", false);
            ctx.getChannel().sendMessage(logChannel.build()).queue();
            // Success message in the new log channel
            EmbedBuilder logChannelInfo = new EmbedBuilder()
                    .setAuthor("log channel", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.blue)
                    .addField("\uD83E\uDDFE │ Log channel changed", "Logging actions are now send in here", false);
            channel.sendMessage(logChannelInfo.build()).queue();
        }
    }
}