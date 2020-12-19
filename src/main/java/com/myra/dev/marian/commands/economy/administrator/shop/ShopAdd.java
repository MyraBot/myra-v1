package com.myra.dev.marian.commands.economy.administrator.shop;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

@CommandSubscribe(
        name = "shop add",
        requires = Permissions.ADMINISTRATOR
)
public class ShopAdd implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length != 2) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("shop add", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "shop add <role> <price>`", "\u26FD │ Add roles to the shop", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        // Get role
        if (Utilities.getUtils().getRole(ctx.getEvent(), ctx.getArguments()[0], "shop add", "\u26FD") == null)
            return; // Check for role
        final Role role = Utilities.getUtils().getRole(ctx.getEvent(), ctx.getArguments()[0], "shop add", "\u26FD"); // Store role
        // Price isn't a number
        if (!ctx.getArguments()[1].matches("\\d+")) {
            Utilities.getUtils().error(ctx.getChannel(), "shop add", "\u26FD", "Invalid number", "Please provide a valid number", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // Price is more than the maximum amount of money
        if (Integer.parseInt(ctx.getArguments()[1]) > Config.ECONOMY_MAX) {
            Utilities.getUtils().error(ctx.getChannel(), "shop add", "\u26FD", "Invalid number", "You can't set a price higher than the maximum", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // Add new Role
        ShopRolesManager.getInstance().addRole(ctx.getGuild(), role.getId(), Integer.valueOf(ctx.getArguments()[1]));
        // Send success message
        EmbedBuilder roleAdd = new EmbedBuilder()
                .setAuthor("shop add", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Added " + role.getAsMention() + " to the shop for " + ctx.getArguments()[1] + " " + new Database(ctx.getGuild()).getNested("economy").getString("currency"));
        ctx.getChannel().sendMessage(roleAdd.build()).queue();
    }
}
