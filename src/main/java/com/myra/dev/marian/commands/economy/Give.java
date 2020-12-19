package com.myra.dev.marian.commands.economy;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.allMethods.GetMember;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

@CommandSubscribe(
        name = "give",
        aliases = {"transfer", "pay"}
)
public class Give implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        final Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 2) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("give", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "give <user> <balance>`", "\uD83D\uDCB8 │ Give credits to other users", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Get user
        final Member member = utilities.getMember(ctx.getEvent(), ctx.getArguments()[0], "give", "\uD83D\uDCB8");
        if (member == null) return;
// Errors
        // Amount of money aren't digits
        if (!ctx.getArguments()[1].matches("\\d+")) {
            utilities.error(ctx.getChannel(), "give", "\uD83D\uDCB8", "Invalid number", "Please only use digits", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }

        final GetMember dbMember = new Database(ctx.getGuild()).getMembers().getMember(member); // Get member in database
        final GetMember dbAuthor = new Database(ctx.getGuild()).getMembers().getMember(ctx.getMember()); // Get author in database
        final int amount = Integer.parseInt(ctx.getArguments()[1]); // Money to transfer

        // Don't have enough money
        if (dbAuthor.getInteger("balance") < amount) {
            utilities.error(ctx.getChannel(), "give", "\uD83D\uDCB8", "Nope", "You don't have enough money", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // Balance limit would be reached
        if (dbMember.getInteger("balance") + amount > Config.ECONOMY_MAX) {
            utilities.error(ctx.getChannel(), "give", "\uD83D\uDCB8", "lol", "The user you want to give the money would be too rich...", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // User is bot
        if (member.getUser().isBot()) {
            utilities.error(ctx.getChannel(), "give", "\uD83D\uDCB8", "Invalid user", "You're not allowed to give credits to bots", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
// Transfer money
        // Transfer money
        dbMember.setInteger("balance", dbMember.getInteger("balance") + amount); // Add money to given member
        dbAuthor.setInteger("balance", dbAuthor.getInteger("balance") - amount); // Remove money of author
        // Success message
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("give", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .setDescription(ctx.getAuthor().getAsMention() + " gave you `" + utilities.formatNumber(Integer.parseInt(ctx.getArguments()[1])) + "` " + new Database(ctx.getGuild()).getNested("economy").getString("currency"));
        ctx.getChannel().sendMessage(member.getAsMention()).queue();
        ctx.getChannel().sendMessage(success.build()).queue();
    }
}
