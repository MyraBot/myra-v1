package com.myra.dev.marian.commands.leveling;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Graphic;
import com.myra.dev.marian.utilities.MessageReaction;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

@CommandSubscribe(
        name = "edit rank"
)
public class Background implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("edit rank", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "edit rank <url>`", "\uD83D\uDDBC │ Set a custom rank background", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        // Get database
        Database db = new Database(ctx.getGuild());
        // Check if you have enough money
        if (db.getMembers().getMember(ctx.getMember()).getBalance() < 10000) {
            utilities.error(ctx.getChannel(), "edit rank", "\uD83D\uDDBC", "Not enough money", "You need 10 000" + db.getNested("economy").getString("currency"), ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // Check if argument is an image
        try {
            ImageIO.read(new URL(ctx.getArguments()[0]));
        } catch (Exception e) {
            utilities.error(ctx.getChannel(), "edit rank", "❓", "Invalid image", e.getMessage(), ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // Get image from Url
        BufferedImage background = ImageIO.read(new URL(ctx.getArguments()[0]));
        // Resize image
        background = Graphic.getInstance().resizeImage(background, 350, 100);
        // Parse to InputStream
        InputStream backgroundFile = Graphic.getInstance().toInputStream(background);
        // Success
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("edit rank", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .addField("\uD83C\uDFC1 │ New rank background", "Do you want to buy this background for 10000" + db.getNested("economy").getString("currency"), false)
                .setImage("attachment://background.png");
        Message message = ctx.getChannel().sendFile(backgroundFile, "background.png").embed(success.build()).complete();
        // Add reactions to message
        message.addReaction("\u2705").queue(); // Checkmark
        message.addReaction("\uD83D\uDEAB").queue(); // Barrier

        MessageReaction.add(ctx.getGuild(), "edit rank", message, ctx.getAuthor(),true, "\u2705", "\uD83D\uDEAB");
    }


    public void confirm(GuildMessageReactionAddEvent event) {
        // Check for right reaction
        if (!MessageReaction.check(event, "edit rank", true)) return;

        // Reaction emoji: "Checkmark"
        if (event.getReactionEmote().getEmoji().equals("\u2705")) {
            // Get database
            Database db = new Database(event.getGuild());
            // Update balance
            db.getMembers().getMember(event.getMember()).setBalance(db.getMembers().getMember(event.getMember()).getBalance() - 10000);
            // Send success
            Utilities.getUtils().success(event.getChannel(),
                    "edit rank", "\uD83D\uDDBC",
                    "New rank background",
                    "You bought a new rank background:",
                    event.getUser().getEffectiveAvatarUrl(), false, event.getChannel().retrieveMessageById(event.getMessageId()).complete().getEmbeds().get(0).getImage().getUrl()
            );
            // Save new image in database
            db.getMembers().getMember(event.getMember()).setString("rankBackground", event.getChannel().retrieveMessageById(event.getMessageId()).complete().getEmbeds().get(0).getImage().getUrl());
        }
        // Reaction emoji: "Barrier"
        else if (event.getReactionEmote().getEmoji().equals("\uD83D\uDEAB")) {
            // Send success
            EmbedBuilder success = new EmbedBuilder()
                    .setAuthor("edit rank")
                    .setColor(Utilities.getUtils().blue)
                    .setDescription("Your purchase has been canceled");
            event.getChannel().sendMessage(success.build()).queue();
        }
    }
}
