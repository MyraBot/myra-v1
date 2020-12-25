package com.myra.dev.marian.commands.administrator;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "prefix",
        requires = Permissions.ADMINISTRATOR
)
public class Prefix implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        //command usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("prefix", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "prefix <prefix>`", "\uD83D\uDCCC │ Change the prefix of the bot", false);
            ctx.getChannel().sendMessage(embed.build()).queue();
            return;
        }
// Change the prefix
        Database db = new Database(ctx.getGuild()); // Get database
        db.set("prefix", ctx.getArguments()[0]); // Change prefix
        // Success information
        EmbedMessage.Success success = new EmbedMessage.Success()
                .setCommand( "prefix")
                .setEmoji("\uD83D\uDCCC")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage("Prefix changed to `" + ctx.getArguments()[0] + "`");
        success.send(ctx.getChannel());
    }
}
