package com.myra.dev.marian.commands.economy.administrator.shop;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

@CommandSubscribe(
        name = "shop remove",
        requires = Permissions.ADMINISTRATOR
)
public class ShopRemove implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("shop remove", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "shop remove <role>`", "\u274C │ Add roles to the shop", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        // Get role
        if (Utilities.getUtils().getRole(ctx.getEvent(), ctx.getArguments()[0], "shop add", "\u26FD") == null)
            return; // Check for role
        final Role role = Utilities.getUtils().getRole(ctx.getEvent(), ctx.getArguments()[0], "shop add", "\u26FD"); // Store role
        // Remove Role
        ShopRolesManager.getInstance().removeRole(ctx.getGuild(), role.getId());
        // Send success message
        EmbedBuilder removedRole = new EmbedBuilder()
                .setAuthor("shop remove", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Removed " + role.getAsMention() + " in the shop");
        ctx.getChannel().sendMessage(removedRole.build()).queue();
    }
}
