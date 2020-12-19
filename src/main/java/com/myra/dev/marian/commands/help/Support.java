package com.myra.dev.marian.commands.help;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.CommandEmbeds;

@CommandSubscribe(
        name = "support",
        aliases = {"support server", "bugs"}
)
public class Support implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        //check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Send message
        ctx.getChannel().sendMessage(new CommandEmbeds(ctx.getGuild(), ctx.getEvent().getJDA(), ctx.getAuthor(), ctx.getPrefix()).supportServer().build()).queue();
    }
}
