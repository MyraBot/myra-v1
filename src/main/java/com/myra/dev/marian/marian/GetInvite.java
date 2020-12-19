package com.myra.dev.marian.marian;

import com.myra.dev.marian.Bot;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.Permissions;

@CommandSubscribe(
        name = "get invite",
        requires = Permissions.MARIAN
)
public class GetInvite implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for marian
        if (!ctx.getAuthor().getId().equals(Config.marian)) return;
        // Get invite link to default channel
        String invite = ctx.getEvent().getJDA().getGuildById(ctx.getArguments()[0]).getDefaultChannel().createInvite().setMaxUses(1).complete().getUrl();
        // Send link
        ctx.getChannel().sendMessage(invite).queue();
    }
}
