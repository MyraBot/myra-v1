package com.myra.dev.marian.commands.music.commands;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        command = "leave",
        name = "leave",
        aliases = {"disconnect"}
)
public class MusicLeave implements Command {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Get utilities
        Utilities utilities = Utilities.getUtils();
// Errors
        // Not connected to a voice channel
        if (!ctx.getGuild().getAudioManager().isConnected()) {
            utilities.error(ctx.getChannel(),
                    "leave", "\uD83D\uDCE4",
                    "I'm not connected to a voice channel",
                    "Use `" + ctx.getPrefix() + "join` to connect me to your voice channel",
                    ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // If author isn't in a voice channel yet
        if (!ctx.getEvent().getMember().getVoiceState().inVoiceChannel()) {
            utilities.error(ctx.getChannel(), "leave", "\uD83D\uDCE4", "You need to join a voice channel first to use this command", "Use `" + ctx.getPrefix() + "join` to let me join a voice channel", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // Author isn't in the same voice channel as the bot
        if (!ctx.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(ctx.getEvent().getMember())) {
            utilities.error(ctx.getChannel(),
                    "leave", "\uD83D\uDCE4",
                    "You have to be in the same voice channel as me to use this command",
                    "To kick me you need to be in **" + ctx.getGuild().getAudioManager().getConnectedChannel().getName() + "**",
                    ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
// Leave voice channel
        // Leave from current channel
        ctx.getGuild().getAudioManager().closeAudioConnection();
        // Send success message
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("leave", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .setDescription("Left voice channel: **" + ctx.getEvent().getMember().getVoiceState().getChannel().getName() + "**");
        ctx.getChannel().sendMessage(success.build()).queue();
    }
}
