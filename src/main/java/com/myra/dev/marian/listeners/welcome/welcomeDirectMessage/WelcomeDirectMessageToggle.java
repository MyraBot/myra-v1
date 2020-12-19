package com.myra.dev.marian.listeners.welcome.welcomeDirectMessage;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;

@CommandSubscribe(
        name = "welcome direct message toggle",
        aliases = {"welcome dm toggle"},
        requires = Permissions.ADMINISTRATOR
)
public class WelcomeDirectMessageToggle implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        //toggle feature
        new Database(ctx.getGuild()).getListenerManager().toggle("welcomeDirectMessage", "\u2709\uFE0F", ctx.getEvent());
    }
}
