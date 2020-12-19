package com.myra.dev.marian.commands.music.commands;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.GuildMusicManager;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "repeat",
        aliases = {"repeat queue", "queue repeat", "loop", "loop queue", "queue loop"}
)
public class MusicRepeat implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Get utilities
        Utilities utilities = Utilities.getUtils();
// Errors
        // Bot isn't connected to a voice channel
        if (!ctx.getGuild().getAudioManager().isConnected()) {
            utilities.error(ctx.getChannel(), "repeat", "\uD83D\uDD01", "I'm not connected to a voice channel", "Use `" + ctx.getPrefix() + "join` to connect me to your voice channel", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // No audio track is playing
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
            utilities.error(ctx.getChannel(), "repeat", "\uD83D\uDD01", "The player isn`t playing any song", "Use `" + ctx.getPrefix() + "play <song>` to play a song", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
// Toggle repeat
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
        final boolean newRepeating = !musicManager.scheduler.repeating;
        // Update repeat value
        musicManager.scheduler.repeating = newRepeating;
        // Send success message
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("repeat", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue);
        if (newRepeating) success.setDescription("The queue will repeat");
        else success.setDescription("The queue won't repeat");

        ctx.getChannel().sendMessage(success.build()).queue();
    }
}
