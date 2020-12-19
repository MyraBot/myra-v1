package com.myra.dev.marian.listeners.welcome.WelcomeImage;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import net.dv8tion.jda.api.Permission;

@CommandSubscribe(
        name = "welcome image toggle"
)
public class WelcomeImageToggle implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        Database db = new Database(ctx.getGuild());
        //missing permissions
        if (!ctx.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        //toggle
        db.getListenerManager().toggle("welcomeImage", "\uD83D\uDDBC", ctx.getEvent());
    }
}