package com.myra.dev.marian.commands.economy;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "economy",
        requires = Permissions.ADMINISTRATOR
)
public class EconomyHelp implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Usage
        EmbedBuilder usage = new EmbedBuilder()
                .setAuthor("economy", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + ctx.getPrefix() + "economy set <user> <balance>`", "\uD83D\uDC5B │ Change a users balance", false)
                .addField("`" + ctx.getPrefix() + "economy currency <currency>`", new Database(ctx.getGuild()).getNested("economy").getString("currency") + " │ Set a custom currency", false);
        ctx.getChannel().sendMessage(usage.build()).queue();
        return;
    }
}
