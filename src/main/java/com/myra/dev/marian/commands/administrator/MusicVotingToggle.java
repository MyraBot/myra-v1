package com.myra.dev.marian.commands.administrator;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Permissions;

@CommandSubscribe(
        name = "music voting",
        aliases = {"music vote"},
        requires = Permissions.ADMINISTRATOR
)
public class MusicVotingToggle implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return;

        final Database db = new Database(ctx.getGuild()); // Get database
        final boolean value = !db.getBoolean("musicVoting"); // Get new value
        db.setBoolean("musicVoting", value); // Update database

        Success success = new Success(ctx.getEvent())
                .setCommand("music voting toggle")
                .setEmoji("\uD83D\uDDF3")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
        if (value) success.setMessage("Music voting is now turned `on`");
        else success.setMessage("Music voting is now turned `off`");
        success.send(); // Send Message
    }
}
