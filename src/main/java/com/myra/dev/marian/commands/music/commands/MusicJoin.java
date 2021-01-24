package com.myra.dev.marian.commands.music.commands;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("ConstantConditions")
@CommandSubscribe(
        command = "join",
        aliases = {"connect"},
        name = "join"
)
public class MusicJoin implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
// ERRORS
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Already connected to a voice channel
        if (ctx.getGuild().getAudioManager().isConnected()) {
            final VoiceChannel vc = ctx.getGuild().getAudioManager().getConnectedChannel(); // Get current voice call
            // Create invite
            vc.createInvite().timeout(10, TimeUnit.MINUTES).queue(invite -> {
                new Error(ctx.getEvent())
                        .setCommand("join")
                        .setEmoji("\uD83D\uDCE5")
                        .setMessage(String.format("I can only be in one channel at a time, right now I'm in `%s`", Utilities.getUtils().hyperlink(vc.getName(), invite.getUrl())))
                        .send();
            });
            return;
        }
        // Member didn't joined voice call yet
        if (!ctx.getMember().getVoiceState().inVoiceChannel()) {
            new Error(ctx.getEvent())
                    .setCommand("join")
                    .setEmoji("\uD83D\uDCE5")
                    .setMessage("Please join a voice channel first")
                    .send();
            return;
        }
        // Missing permissions to connect
        if (!ctx.getGuild().getSelfMember().hasPermission(ctx.getMember().getVoiceState().getChannel(), Permission.VOICE_CONNECT)) {
            new Error(ctx.getEvent())
                    .setCommand("join")
                    .setEmoji("\uD83D\uDCE5")
                    .setMessage("I'm missing permissions to join your voice channel")
                    .setFooter("I need the permission `Connect` under the `VOICE PERMISSIONS` category")
                    .send();
            return;
        }
// JOIN VOICE CHANNEL
        // Open audio connection
        ctx.getGuild().getAudioManager().openAudioConnection(ctx.getMember().getVoiceState().getChannel());
        // Send success message
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("join", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .setDescription("Joined voice channel: **" + ctx.getMember().getVoiceState().getChannel().getName() + "**");
        ctx.getChannel().sendMessage(success.build()).queue();
    }
}
