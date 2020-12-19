package com.myra.dev.marian.commands.leveling.administrator;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.Manager;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
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

        // Remove leveling channel
        if (channelId.equals(channel.getId())) {
            db.getNested("leveling").set("channel", "not set", Manager.type.STRING); // Set leveling channel to `not set`
            // Send success message
            utils.success(ctx.getChannel(), "leveling channel", "\uD83E\uDDFE", "Leveling channel changed", "Leveling messages are now sent in the channel the user leveled up", ctx.getAuthor().getEffectiveAvatarUrl(), false, null);
        } else {
            db.getNested("leveling").set("channel", channel.getId(), Manager.type.STRING); // Set leveling channel to the new channel
            // Send success message
            utils.success(ctx.getChannel(), "leveling channel", "\uD83E\uDDFE", "Leveling channel changed", "Leveling messages are now sent in " + channel.getAsMention(), ctx.getAuthor().getEffectiveAvatarUrl(), false, null);
            // Send success message in new channel
            utils.success(channel, "leveling channel", "\uD83E\uDDFE", "Leveling channel changed", "Leveling messages are now sent in here", ctx.getAuthor().getEffectiveAvatarUrl(), false, null);
        }
    }
}
