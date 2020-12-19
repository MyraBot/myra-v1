package com.myra.dev.marian.commands.music.commands;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

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
            utilities.error(ctx.getChannel(), "join", "\uD83D\uDCE5", "I can only be in one channel at a time", "I'm already connected to **" + ctx.getGuild().getAudioManager().getConnectedChannel().getName() + "**", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // Member didn't joined voice call yet
        if (!ctx.getMember().getVoiceState().inVoiceChannel()) {
            utilities.error(ctx.getChannel(), "join", "\uD83D\uDCE5", "Please join a voice channel first", "In order for me to join a voice channel, you must already be connected to a voice channel", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // Missing permissions to connect
        if (!ctx.getGuild().getSelfMember().hasPermission(ctx.getMember().getVoiceState().getChannel(), Permission.VOICE_CONNECT)) {
            utilities.error(ctx.getChannel(), "join", "\uD83D\uDCE5", "I'm missing permissions to join your voice channel", "please give me the permission `Connect` under the `VOICE PERMISSIONS` category", ctx.getAuthor().getEffectiveAvatarUrl());
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
