package com.myra.dev.marian.listeners.autorole;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

@CommandSubscribe(
        name = "autorole",
        aliases = {"auto role", "defaultrole", "default role", "joinrole", "join role"},
        requires = Permissions.ADMINISTRATOR
)
public class AutoRoleSet implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        //command usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("auto role", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "autorole <role>`", "\uD83D\uDCDD │ Give a new joined member automatic a certain role", true);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Autorole
        // Get autorole
        Role role = utilities.getRole(ctx.getEvent(), ctx.getArguments()[0], "autorole", "\uD83D\uDCDD");
        if (role == null) return;
        // Get database
        Database db = new Database(ctx.getGuild());
        //remove autorole
        if (db.getString("autoRole").equals(role.getId())) {
            //error
            utilities.success(ctx.getChannel(), "auto role", "\uD83D\uDCDD", "Removed auto role", "New members no longer get the " + ctx.getGuild().getRoleById(db.getString("autoRole")).getAsMention() + " role", ctx.getAuthor().getEffectiveAvatarUrl(), false, null);
            //database
            db.set("autoRole", "not set");
            return;
        }
        //Database
        db.set("autoRole", role.getId());
        //success
        utilities.success(ctx.getChannel(),
                "auto role", "\uD83D\uDCDD",
                "Added auto role",
                "New members get now the " + role.getAsMention() + " role",
                ctx.getAuthor().getEffectiveAvatarUrl(),
                false, null);
    }
}
