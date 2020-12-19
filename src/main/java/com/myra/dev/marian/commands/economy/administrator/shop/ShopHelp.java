package com.myra.dev.marian.commands.economy.administrator.shop;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "shop",
        requires = Permissions.ADMINISTRATOR
)
public class ShopHelp implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("shop", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "shop add <role> <price>`", "\u26FD │ Add roles to the shop", false)
                    .addField("`" + ctx.getPrefix() + "shop remove <role>`", "\u274C │ Remove a role from the shop", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
        }
    }
}
