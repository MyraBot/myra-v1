package com.myra.dev.marian.listeners.welcome.WelcomeImage;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.Manager;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

@CommandSubscribe(
        name = "welcome image background",
        aliases = {"welcome image image"},
        requires = Permissions.ADMINISTRATOR
)
public class WelcomeImageBackground implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("welcome image background", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "welcome image background <url>`", "\uD83D\uDDBC │ Change the background of the welcome images", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        /**
         * set background image
         */
        //invalid url
        try {
            ImageIO.read(new URL(ctx.getArguments()[0]));
        } catch (IOException e) {
            utilities.error(ctx.getChannel(),
                    "welcome image background",
                    "\uD83D\uDDBC",
                    "Invalid background URL",
                    "Please try another image",
                    ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        //save in database
        new Database(ctx.getGuild()).getNested("welcome").set("welcomeImageBackground", ctx.getArguments()[0], Manager.type.STRING);
        //success
        EmbedMessage.Success success = new EmbedMessage.Success()
                .setCommand("welcome image background")
                .setEmoji("\uD83D\uDDBC")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage("The background has been changed to:")
                .setImage(ctx.getArguments()[0]);
        success.send(ctx.getChannel());
    }
}
