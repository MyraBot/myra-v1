package com.myra.dev.marian.listeners.welcome.WelcomeImage;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.Manager;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.MessageReaction;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

@CommandSubscribe(
        name = "welcome image font",
        requires = Permissions.ADMINISTRATOR
)
public class WelcomeImageFont implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        //change font
        EmbedBuilder fontSelection = new EmbedBuilder()
                .setAuthor("welcome image font", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .addField("fonts",
                        "1\uFE0F\u20E3 default \n" +
                                "2\uFE0F\u20E3 modern \n" +
                                "3\uFE0F\u20E3 handwritten",
                        false);
        Message message = ctx.getChannel().sendMessage(fontSelection.build()).complete();
        //add reactions
        message.addReaction("1\uFE0F\u20E3").queue();
        message.addReaction("2\uFE0F\u20E3").queue();
        message.addReaction("3\uFE0F\u20E3").queue();

        MessageReaction.add(ctx.getGuild(), "welcomeImageFont", message, ctx.getAuthor(),true, "1\uFE0F\u20E3", "2\uFE0F\u20E3", "3\uFE0F\u20E3");
    }

    //reaction
    public void chooseFont(GuildMessageReactionAddEvent event) {
        //missing permissions
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
        //if reaction was added on the wrong message return
        if (!MessageReaction.check(event, "welcomeImageFont", true)) return;
        // Get database
        Database db = new Database(event.getGuild());
        //fonts
        switch (event.getReaction().getReactionEmote().getEmoji()) {
            case "1\uFE0F\u20E3":
                db.getNested("welcome").set("welcomeImageFont", "default", Manager.type.STRING);
                Utilities.getUtils().success(event.getChannel(), "welcome image font", "\uD83D\uDDDB", "Changed welcome image font", "You have changed the font to `default`", event.getUser().getEffectiveAvatarUrl(), false, null);
                break;
            case "2\uFE0F\u20E3":
                db.getNested("welcome").set("welcomeImageFont", "modern", Manager.type.STRING);
                Utilities.getUtils().success(event.getChannel(), "welcome image font", "\uD83D\uDDDB", "Changed welcome image font", "You have changed the font to `modern`", event.getUser().getEffectiveAvatarUrl(), false, null);
                break;
            case "3\uFE0F\u20E3":
                db.getNested("welcome").set("welcomeImageFont", "handwritten", Manager.type.STRING);
                Utilities.getUtils().success(event.getChannel(), "welcome image font", "\uD83D\uDDDB", "Changed welcome image font", "You have changed the font to `handwritten`", event.getUser().getEffectiveAvatarUrl(), false, null);
                break;
        }
    }
}
