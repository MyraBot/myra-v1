package com.myra.dev.marian.commands.moderation.ban;


import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.time.Instant;

@CommandSubscribe(
        name = "unban",
        aliases = {"unbean"},
        requires = Permissions.MODERATOR
)
public class Unban implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        Utilities utilities = Utilities.getUtils(); // Get utilities
        // Command usage
        if (ctx.getArguments().length == 1) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("│ unban", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "unban <user>`", "\uD83D\uDD13 │ unban a specific member", false);
            ctx.getChannel().sendMessage(embed.build()).queue();
            return;
        }
// Unban
        User user = utilities.getUser(ctx.getEvent(), ctx.getArguments()[0], "unban", "\uD83D\uDD13"); // Get member
        if (user == null) return;


        ctx.getGuild().retrieveBanList().queue(bans -> {
            // User isn't banned
            if (!bans.stream().anyMatch(ban -> ban.getUser().equals(user))) {
                Utilities.getUtils().error(ctx.getChannel(), "unban", "\uD83D\uDD13", "User isn't banned", "The mentioned user is not banned", ctx.getAuthor().getEffectiveAvatarUrl());
                return;
            }


            // Guild message
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(user.getAsTag() + " got unbanned", null, user.getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setDescription("\uD83D\uDD13 │ " + user.getAsMention() + " got unbanned from " + ctx.getGuild().getName())
                    .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now());
            // Direct message
            EmbedBuilder directMessage = new EmbedBuilder()
                    .setAuthor("You got unbanned", null, ctx.getGuild().getIconUrl())
                    .setColor(Utilities.getUtils().blue)
                    .setDescription("\uD83D\uDD13 │ You got unbanned from `" + ctx.getGuild().getName() + "`")
                    .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now());

            // Send messages
            ctx.getChannel().sendMessage(embed.build()).queue(); // Guild message
            user.openPrivateChannel().queue((channel) -> { // Direct message
                channel.sendMessage(directMessage.build()).queue();
            });

            ctx.getGuild().unban(user).queue(); // unban user
        });
    }
}