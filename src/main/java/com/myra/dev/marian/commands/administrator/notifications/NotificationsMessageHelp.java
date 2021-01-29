package com.myra.dev.marian.commands.administrator.notifications;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Permissions;

@CommandSubscribe(
        name = "notifications message",
        aliases = {"notification message"},
        requires = Permissions.ADMINISTRATOR
)
public class NotificationsMessageHelp implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length == 0) {
            CommandUsage usage = new CommandUsage(ctx.getEvent())
                    .setCommand("notifications message")
                    .addUsages(
                            new Usage()
                                    .setUsage("notifications message twitch <message>")
                                    .setDescription("Add a notification message, which is send once your streamer is live")
                                    .setEmoji("\uD83D\uDCEF"),
                            new Usage()
                                    .setUsage("notifications message youtube <message>")
                                    .setDescription("Add a notification message, which is send once a youtuber uploaded a video")
                                    .setEmoji("\\\uD83D\uDCFA"));
            usage.send();
        }
    }
}
