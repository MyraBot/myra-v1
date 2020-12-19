package com.myra.dev.marian.commands.moderation.mute;


import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.time.Instant;

@CommandSubscribe(
        name = "mute",
        requires = Permissions.MODERATOR
)
public class Mute implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments()[0].equalsIgnoreCase("role")) return; // Mute role command was used

        // Command usage
        if (ctx.getArguments().length < 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("mute", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "mute <user> <reason>`", "\uD83D\uDD07 │ mute a specific user", false)
                    .setFooter("you don't have to give a reason.");
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Mute
        final Member member = Utilities.getUtils().getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], "mute", "\uD83D\uDD07"); // Get member
        if (member == null) return;

        final String muteRoleId = new Database(ctx.getGuild()).getString("muteRole"); // Get mute role id
        // No mute role set
        if (muteRoleId.equals("not set")) {
            Utilities.getUtils().error(ctx.getChannel(), "mute", "\uD83D\uDD07 ", "You didn't specify a mute role", "To indicate a mute role, type in `" + ctx.getPrefix() + "mute role <role>`", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // User is already muted
        if (member.getRoles().contains(ctx.getGuild().getRoleById(muteRoleId))) {
            Utilities.getUtils().error(ctx.getChannel(), "mute", "\uD83D\uDD07", "This user is already muted", "Use `" + ctx.getPrefix() + "unmute <user>` to unmute a user", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }

        final User user = member.getUser(); // Get member as user
        // Guild message
        EmbedBuilder guildMessage = new EmbedBuilder()
                .setAuthor(user.getAsTag() + " got muted", null, user.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().red)
                .setDescription("\uD83D\uDD07 │ " + user.getAsMention() + " got muted on " + ctx.getGuild().getName())
                .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        // Direct message
        EmbedBuilder directMessage = new EmbedBuilder()
                .setAuthor("You got muted", null, user.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().red)
                .setDescription("\uD83D\uDD07 │ You got muted on " + ctx.getGuild().getName())
                .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        // No reason given
        if (ctx.getArguments().length == 1) {
            guildMessage.addField("\uD83D\uDCC4 │ no reason", "there was no reason given", false); // Set reason to none
            directMessage.addField("\uD83D\uDCC4 │ no reason", "there was no reason given", false); // Set reason to none
        }
        //mute with reason
        else {
            final String reason = ctx.getArgumentsRaw().split("\\s+", 2)[1]; // Get arguments
            guildMessage.addField("\uD83D\uDCC4 │ reason:", reason, false); // Add reason
            directMessage.addField("\uD83D\uDCC4 │ reason:", reason, false); // Add reason
        }

        // Send messages
        ctx.getChannel().sendMessage(guildMessage.build()).queue(); // Send message in guild
        user.openPrivateChannel().queue((channel) -> { // Send direct message
            channel.sendMessage(directMessage.build()).queue();
        });
        // Mute member
        ctx.getGuild().addRoleToMember(ctx.getGuild().getMember(user), ctx.getGuild().getRoleById(muteRoleId)).queue();
    }
}
