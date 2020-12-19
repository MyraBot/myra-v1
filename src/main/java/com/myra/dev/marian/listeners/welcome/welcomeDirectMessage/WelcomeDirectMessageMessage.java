package com.myra.dev.marian.listeners.welcome.welcomeDirectMessage;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.Manager;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "welcome direct message message",
        aliases = {"welcome dm message"},
        requires = Permissions.ADMINISTRATOR
)
public class WelcomeDirectMessageMessage implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder welcomeDirectMessageMessage = new EmbedBuilder()
                    .setAuthor("welcome direct message", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .addField("`" + ctx.getPrefix() + "welcome direct message message <message>`", "\uD83D\uDCAC │ change the text of the direct messages", false)
                    .setFooter("{user} = mention the user │ {server} = server name │ {count} = user count");
            ctx.getChannel().sendMessage(welcomeDirectMessageMessage.build()).queue();
            return;
        }
        Database db = new Database(ctx.getGuild());
        // Get message
        String message = "";
        for (int i = 0; i < ctx.getArguments().length; i++) {
            message += ctx.getArguments()[i] + " ";
        }
        //remove last space
        message = message.substring(0, message.length() - 1);
        //change value in database
        db.getNested("welcome").set("welcomeDirectMessage", message, Manager.type.STRING);
        //success
        String welcomeMessage = db.getNested("welcome").getString("welcomeDirectMessage");
        Utilities.getUtils().success(ctx.getChannel(), "welcome direct message", "\u2709\uFE0F", "Welcome message changed",
                welcomeMessage
                        .replace("{user}", ctx.getAuthor().getAsMention())
                        .replace("{server}", ctx.getGuild().getName())
                        .replace("{count}", Integer.toString(ctx.getGuild().getMemberCount())),
                ctx.getAuthor().getEffectiveAvatarUrl(), false, null);
    }
}
