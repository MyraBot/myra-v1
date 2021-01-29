package com.myra.dev.marian.commands.administrator.notifications;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Permissions;

@CommandSubscribe(
        name = "notifications message youtube",
        aliases = {"notification message youtube"},
        requires = Permissions.ADMINISTRATOR
)
public class NotificationsMessageYoutube implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("notifications message youtube")
                    .addUsages(
                            new Usage()
                                    .setUsage("notifications message twitch <message>")
                                    .setDescription("Add a notification message, which is send once your streamer is live")
                                    .setEmoji("\\\uD83D\uDCFA"))
                    .addInformation(String.format("Use variables to customize your message%n" +
                            "%n{youtuber} - Name of youtuber" +
                            "%n{title} - Name of the video"))
                    .send();
            return;
        }

        // Update database
        new Database(ctx.getGuild()).getNested("notifications").setString("twitchMessage", ctx.getArgumentsRaw());
        // Set success message
        new Success(ctx.getEvent())
                .setCommand("notifications message youtube")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setEmoji("\\\uD83D\uDCFA")
                .setMessage(String.format("You changed the twitch notifications message to:%n%s", ctx.getArgumentsRaw()))
                .send();
    }
}
