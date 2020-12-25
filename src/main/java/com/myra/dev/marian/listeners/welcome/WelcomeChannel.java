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
import net.dv8tion.jda.api.entities.TextChannel;

@CommandSubscribe(
        name = "welcome channel",
        requires = Permissions.ADMINISTRATOR
)
public class WelcomeChannel implements Command {


    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder welcomeChannelUsage = new EmbedBuilder()
                    .setAuthor("welcome channel", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "welcome channel <channel>`", "\uD83D\uDCC1 │ Set the channel, the welcome message will go", true);
            ctx.getChannel().sendMessage(welcomeChannelUsage.build()).queue();
            return;
        }
        /**
         * Change welcome channel
         */
        //get channel
        TextChannel channel = utilities.getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "welcome channel", "\uD83D\uDCC1");
        if (channel == null) return;
        // Get database
        Database db = new Database(ctx.getGuild());
        // Get current welcome channel
        String currentChannelId = db.getNested("welcome").getString("welcomeChannel");
        // Success
        EmbedMessage.Success success = new EmbedMessage.Success()
                .setCommand("welcome channel")
                .setEmoji("\uD83D\uDCC1")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
        //remove welcome channel
        if (currentChannelId.equals(channel.getId())) {
            db.getNested("welcome").set("welcomeChannel", "not set", Manager.type.STRING); // Remove channel id
            success.setMessage("Welcome are no longer send in " + channel.getAsMention()).send(ctx.getChannel()); // Success message
        } else {
            db.getNested("welcome").setString("welcomeChannel", channel.getId()); // Update database
            // Success message
            success.setMessage("Welcome messages are now send in " + channel.getAsMention()).send(ctx.getChannel());
            success.setMessage("Welcome actions are now send in here").send(channel);
        }
    }
}
