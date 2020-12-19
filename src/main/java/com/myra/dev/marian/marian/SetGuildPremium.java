package com.myra.dev.marian.marian;

import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

@CommandSubscribe(
        name = "set premium",
        requires = Permissions.MARIAN
)

public class SetGuildPremium implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        final String guildId = ctx.getArguments()[0]; // Get guild
        // No server found
        if (ctx.getEvent().getJDA().getGuildById(guildId) == null) {
            Utilities.getUtils().error(ctx.getChannel(), "set premium", "", "Couldn't find server", "Make sure you used the right id", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        final Guild guild = ctx.getEvent().getJDA().getGuildById(guildId); // Get guild

        final Document document = MongoDb.getInstance().getCollection("guilds").find(eq("guildId", guildId)).first(); // Get guild document
        final boolean newValue = !document.getBoolean("premium"); // Get opposite value of the current value
        document.replace("premium", newValue); // Update premium status

        MongoDb.getInstance().getCollection("guilds").findOneAndReplace(eq("guildId", guildId), document); // Update guild document
        Utilities.getUtils().success(ctx.getChannel(), "set premium", "", guild.getName() + " has now premium", "Now you're one of the cool guys", guild.getIconUrl(), false, null);
    }
}
