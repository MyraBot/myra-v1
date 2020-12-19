package com.myra.dev.marian.commands.help;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.CommandEmbeds;

@CommandSubscribe(
        name = "invite"
)
public class Invite implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        ctx.getChannel().sendMessage(new CommandEmbeds(ctx.getGuild(), ctx.getEvent().getJDA(), ctx.getAuthor(), ctx.getPrefix()).inviteJda().build()).queue();
    }
}
