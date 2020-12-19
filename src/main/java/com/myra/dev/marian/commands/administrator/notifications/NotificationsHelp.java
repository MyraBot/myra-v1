package com.myra.dev.marian.commands.administrator.notifications;


import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "notification",
        aliases = {"notifications"},
        requires = Permissions.ADMINISTRATOR
)
public class NotificationsHelp implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Send message
        EmbedBuilder notificationUsage = new EmbedBuilder()
                .setAuthor("notification", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + ctx.getPrefix() + "notifications twitch <streamer>`", "\uD83D\uDCE1 │ Add and remove auto notifications for a twitch streamer", false)
                .addField("`" + ctx.getPrefix() + "notifications youtube <youtube channel>`", "\uD83D\uDCFA │ Add and remove auto notifications for YouTube", false)
                .addField("`" + ctx.getPrefix() + "notifications list`", "\uD83D\uDD14 │ Displays all users you get notification from", false)
                .addField("`" + ctx.getPrefix() + "notifications channel <channel>`", "\uD83D\uDCC1 │ Set the channel, the notifications will go", false);
        ctx.getChannel().sendMessage(notificationUsage.build()).queue();
    }
}
