package com.myra.dev.marian.commands.economy.administrator;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

@CommandSubscribe(
        name = "economy set",
        aliases = {"balance set", "bal set", "money set"},
        requires = Permissions.ADMINISTRATOR
)
public class EconomySet implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 2) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("economy set", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "economy set <user> <balance>`", "\uD83D\uDC5B │ Change a users balance", false)
                    .setFooter("Use: + / -, to add and subtract money");
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Change balance
        final Member member = utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "economy set", "\uD83D\uDC5B");
        if (member == null) return;

        final Database db = new Database(ctx.getGuild()); // Get database
        int updatedBalance = db.getMembers().getMember(member).getBalance(); // Get old balance

        long amount = Long.parseLong(ctx.getArguments()[1]); // Get amount of money to set/add/remove
        if (amount > Config.ECONOMY_MAX || amount < -Config.ECONOMY_MAX) { // Limit would be reached
            utilities.error(ctx.getChannel(), "economy set", "\uD83D\uDC5B", "Invalid amount", "You can have an amount of money between +" + Config.ECONOMY_MAX + " and -" + Config.ECONOMY_MAX, ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }

        // Add balance
        if (ctx.getArguments()[1].matches("[+]\\d+")) { // Amount of money is too much
                    updatedBalance += Integer.parseInt(ctx.getArguments()[1].substring(1)); // Add balance
        }
        // Subtract balance
        else if (ctx.getArguments()[1].matches("[-]\\d+")) {
            updatedBalance -= Integer.parseInt(ctx.getArguments()[1].substring(1)); // Subtract balance
        }
        // Set balance
        else if (ctx.getArguments()[1].matches("\\d+")) {
            updatedBalance = Integer.parseInt(ctx.getArguments()[1]); // Set new balance
        }
        // Error
        else {
            utilities.error(ctx.getChannel(), "economy set", "\uD83D\uDC5B", "Invalid operator", "Please use `+` to add money, `-` to subtract money or leave the operators out to set an exact amount of money", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // Balance limit would be reached
        if (updatedBalance > Config.ECONOMY_MAX) {
            utilities.error(ctx.getChannel(), "economy set", "\uD83D\uDC5B", "lol", "The user you want to give the money would be too rich...", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // Change balance in database
        db.getMembers().getMember(member).setBalance(updatedBalance);
        // Success
        utilities.success(ctx.getChannel(), "economy set", "\uD83D\uDC5B", "Updated balance", member.getAsMention() + "has now `" + utilities.formatNumber(updatedBalance) + "` " + db.getNested("economy").getString("currency"), ctx.getAuthor().getEffectiveAvatarUrl(), false, null);
    }
}
