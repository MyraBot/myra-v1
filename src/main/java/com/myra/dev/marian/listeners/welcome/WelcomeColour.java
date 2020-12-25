package com.myra.dev.marian.listeners.welcome;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.Manager;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

@CommandSubscribe(
        name = "welcome colour",
        aliases = {"welcome color"},
        requires = Permissions.ADMINISTRATOR
)
public class WelcomeColour implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder welcomeChannelUsage = new EmbedBuilder()
                    .setAuthor("welcome colour", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "welcome colour <hex colour>`", "\uD83C\uDFA8 │ Set the colour of the embeds", false);
            ctx.getChannel().sendMessage(welcomeChannelUsage.build()).queue();
            return;
        }
        String hex = null;
        //remove #
        if (ctx.getArguments()[0].startsWith("#")) {
            StringBuilder raw = new StringBuilder(ctx.getArguments()[0]);
            raw.deleteCharAt(0);
            hex = "0x" + raw.toString();
        }
        //add 0x
        else {
            hex = "0x" + ctx.getArguments()[0];
        }
        //if colour doesn't exist
        try {
            Color.decode(hex);
        } catch (Exception e) {
            utilities.error(ctx.getChannel(), "welcome embed colour", "\uD83C\uDFA8", "Invalid colour", "The given colour doesn't exist", ctx.getAuthor().getEffectiveAvatarUrl())
            ;
            return;
        }
        //save in database
        new Database(ctx.getGuild()).getNested("welcome").set("welcomeColour", hex, Manager.type.INTEGER);
        //success
        EmbedMessage.Success success = new EmbedMessage.Success()
                .setCommand("welcome embed colour")
                .setEmoji("\uD83C\uDFA8")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage("Colour changed to `" + hex.replace("0x", "#") + "`");
        success.send(ctx.getChannel());
    }
}