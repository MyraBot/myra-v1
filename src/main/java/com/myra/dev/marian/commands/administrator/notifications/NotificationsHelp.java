package com.myra.dev.marian.commands.administrator.notifications;


import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "notifications",
        aliases = {"notification"},
        requires = Permissions.ADMINISTRATOR
)
public class NotificationsHelp implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Send message
        new CommandUsage(ctx.getEvent())
                .setCommand("notifications")
                .addUsages(
                        new Usage()
                                .setUsage("notifications twitch <streamer>")
                                .setDescription("Add and remove auto notifications for a twitch streamer")
                                .setEmoji("\uD83D\uDCE1"),
                        new Usage()
                                .setUsage("notifications youtube <youtube channel>")
                                .setDescription("Add and remove auto notifications for YouTube")
                                .setEmoji("\uD83D\uDCFA"),
                        new Usage()
                                .setUsage("notifications list")
                                .setDescription("Displays all users you get notification from")
                                .setEmoji("\uD83D\uDD14"),
                        new Usage()
                                .setUsage("notifications channel <channel>")
                                .setDescription("Set the channel, the notifications will go")
                                .setEmoji("\uD83D\uDCC1"),
                        new Usage()
                                .setUsage("notifications message")
                                .setDescription("Set a notification message, which is send once a notifications is send")
                                .setEmoji("\uD83D\uDCEF")
                )
                .send();
    }
}
