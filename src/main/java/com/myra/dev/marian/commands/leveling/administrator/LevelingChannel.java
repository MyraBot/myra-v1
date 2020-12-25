package com.myra.dev.marian.commands.leveling.administrator;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.Manager;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

@CommandSubscribe(
        name = "leveling channel",
        requires = Permissions.ADMINISTRATOR
)
public class LevelingChannel implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        final Utilities utils = new Utilities(); // Get utilities
// Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("leveling channel", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utils.gray)
                    .addField("`" + ctx.getPrefix() + "leveling channel <channel>`", "\uD83E\uDDFE │ Change the channel where level-up messages are sent", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Change leveling channel
        Database db = new Database(ctx.getGuild());

        if (utils.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "leveling channel", "\uD83E\uDDFE") == null)
            return;
        final TextChannel channel = utils.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "leveling channel", "\uD83E\uDDFE");

        final String channelId = db.getNested("leveling").getString("channel"); // Get leveling channel id

        EmbedMessage.Success success = new EmbedMessage.Success()
                .setCommand("report")
                .setEmoji("\uD83D\uDC1B")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());

        // Remove leveling channel
        if (channelId.equals(channel.getId())) {
            db.getNested("leveling").set("channel", "not set", Manager.type.STRING); // Set leveling channel to `not set`
            success.setMessage("Leveling messages are now sent in the channel the user levels up").send(ctx.getChannel()); // Send success message
        } else {
            db.getNested("leveling").set("channel", channel.getId(), Manager.type.STRING); // Set leveling channel to the new channel
            success.setMessage("Leveling messages are now sent in " + channel.getAsMention()).send(ctx.getChannel()); // Send success message
            success.setMessage("Leveling messages are now sent in here").send(channel); // Send success message in new channel
        }
    }
}
