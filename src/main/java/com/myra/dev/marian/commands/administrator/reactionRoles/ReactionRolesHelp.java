package com.myra.dev.marian.commands.administrator.reactionRoles;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "reaction roles",
        aliases = {"reaction role", "rr"},
        requires = Permissions.ADMINISTRATOR
)
public class ReactionRolesHelp implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("reaction roles", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "reaction roles add <role>`", "\uD83D\uDD17 │ Bind a role to a reaction", false)
                    .addField("`" + ctx.getPrefix() + "reaction roles remove`", "\uD83D\uDDD1 │ Remove a reaction role", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
        }
    }
}
