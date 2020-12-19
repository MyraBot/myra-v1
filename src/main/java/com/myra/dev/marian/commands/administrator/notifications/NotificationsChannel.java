package com.myra.dev.marian.commands.administrator.notifications;

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
        name = "notifications channel",
        aliases = {"notification channel"},
        requires = Permissions.ADMINISTRATOR
)
public class NotificationsChannel implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Missing permissions
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder notificationUsage = new EmbedBuilder()
                    .setAuthor("notification Twitch", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "notification channel <channel>`", "\uD83D\uDCC1 │ Set the channel, the notifications will go", false);
            ctx.getChannel().sendMessage(notificationUsage.build()).queue();
            return;
        }
        /**
         * Change notification channel
         */
        //Get database
        Database db = new Database(ctx.getGuild());
        //get channel
        TextChannel channel = utilities.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "notification channel", "\uD83D\uDD14");
        if (channel == null) return;
        //get current notification channel
        String currentChannelId = db.getNested("notifications").getString("channel");
        //remove notification channel
        if (currentChannelId.equals(channel.getId())) {
            //remove channel id
            db.getNested("notifications").set("channel", "not set", Manager.type.STRING);
            //success
            utilities.success(ctx.getChannel(), "notification channel", "\uD83D\uDD14", "Notification channel removed", "Notifications are no longer send in " + channel.getAsMention(), ctx.getAuthor().getEffectiveAvatarUrl(), false, null);
            return;
        }
        //change notification channel
        db.getNested("notifications").set("channel", channel.getId(), Manager.type.STRING);
        //success
        utilities.success(ctx.getChannel(), "notification channel", "\uD83D\uDD14", "Notification channel changed", "Notifications are now send in " + channel.getAsMention(), ctx.getAuthor().getEffectiveAvatarUrl(), false, null);
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("notification channel", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .addField("\uD83D\uDCC1 │ Notification channel changed", "Media notifications are now send in here", false);
        channel.sendMessage(success.build()).queue();
    }
}
