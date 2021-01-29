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
        name = "notifications message twitch",
        aliases = {"notification message twitch"},
        requires = Permissions.ADMINISTRATOR
)
public class NotificationsMessageTwitch implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("notifications message twitch")
                    .addUsages(
                            new Usage()
                                    .setUsage("notifications message twitch <message>")
                                    .setDescription("Add a notification message, which is send once your streamer is live")
                                    .setEmoji("\uD83D\uDCE1"))
                    .addInformation(String.format("Use variables to customize your message%n" +
                            "%n{streamer} - Name of streamer" +
                            "%n{title} - Title of stream&n" +
                            "%n{game} - Game which is played on stream"))
                    .send();
            return;
        }

        // Update database
        new Database(ctx.getGuild()).getNested("notifications").setString("twitchMessage", ctx.getArgumentsRaw());
        // Set success message
        new Success(ctx.getEvent())
                .setCommand("notifications message twitch")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setEmoji("\uD83D\uDCE1")
                .setMessage(String.format("You changed the twitch notifications message to:%n%s", ctx.getArgumentsRaw()))
                .send();
    }
}