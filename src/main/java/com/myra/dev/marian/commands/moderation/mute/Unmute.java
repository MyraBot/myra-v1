package com.myra.dev.marian.commands.moderation.mute;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.time.Instant;

@CommandSubscribe(
        name = "unmute",
        requires = Permissions.MODERATOR
)
public class Unmute implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        final Utilities utilities = Utilities.getUtils(); // Get utilities
        // Command usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("unmute", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "unmute <user>`", "\uD83D\uDD08 │ Unmute a specific user", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Unmute
        final Member member = utilities.getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], "Unmute", "\uD83D\uDD08"); // Get member
        if (member == null) return;

        final String muteRoleId = new Database(ctx.getGuild()).getString("muteRole"); // Get mute role id
        // No mute role set
        if (muteRoleId.equals("not set")) {
            new Error(ctx.getEvent())
                    .setCommand("unmute")
                    .setEmoji("\uD83D\uDD08")
                    .setMessage("You didn't specify a mute role")
                    .send();
            return;
        }
        // User is already muted
        if (!member.getRoles().contains(ctx.getGuild().getRoleById(muteRoleId))) {
            new Error(ctx.getEvent())
                    .setCommand("unmute")
                    .setEmoji("\uD83D\uDD08")
                    .setMessage("This user isn't muted")
                    .send();
            return;
        }

        final User user = member.getUser(); // Get member as user
        // Guild message
        EmbedBuilder guildMessage = new EmbedBuilder()
                .setColor(utilities.green)
                .setAuthor(user.getAsTag() + " got unmuted", null, user.getEffectiveAvatarUrl())
                .setDescription("\uD83D\uDD08 │ " + user.getAsMention() + " got unmuted on " + ctx.getGuild().getName())
                .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        //direct message
        EmbedBuilder directMessage = new EmbedBuilder()
                .setAuthor("You got unmuted", null, user.getEffectiveAvatarUrl())
                .setColor(utilities.green)
                .setDescription("\uD83D\uDD08 │ You got unmuted on " + ctx.getGuild().getName())
                .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());

        // Send messages
        ctx.getChannel().sendMessage(guildMessage.build()).queue(); // Guild message
        user.openPrivateChannel().queue((channel) -> { // Direct message
            channel.sendMessage(directMessage.build()).queue();
        });

        ctx.getGuild().removeRoleFromMember(member, ctx.getGuild().getRoleById(muteRoleId)).queue(); // Unmute
    }
}