package com.myra.dev.marian.commands.music.commands;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.GuildMusicManager;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "stop"
)
public class MusicStop implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Get utilities
        Utilities utilities = Utilities.getUtils();
// Errors
        // Bot isn't connected to a voice channel
        if (!ctx.getGuild().getAudioManager().isConnected()) {
            utilities.error(ctx.getChannel(), "stop", "\u23F9", "I'm not connected to a voice channel", "Use `" + ctx.getPrefix() + "join` to connect me to your voice channel", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // No audio track is playing
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
            utilities.error(ctx.getChannel(), "stop", "\u23F9", "The player isn`t playing any song", "Use `" + ctx.getPrefix() + "play <song>` to play a song", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
// Skip current playing track
        // Send success message
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("stop", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .setDescription("Stopped music player");
        ctx.getChannel().sendMessage(success.build()).queue();
        // Stop player
        final GuildMusicManager player = PlayerManager.getInstance().getMusicManager(ctx.getGuild()); // Get player manager
        player.scheduler.getQueue().clear(); // Clear queue
        player.audioPlayer.stopTrack(); // Stop track
    }
}
