package com.myra.dev.marian.commands.music.commands;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@CommandSubscribe(
        command = "queue",
        name = "queue",
        aliases = {"songs", "tracks"}
)
public class MusicQueue implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Get utilities
        Utilities utilities = Utilities.getUtils();
// Errors
        // Bot isn't connected to a voice channel
        if (!ctx.getGuild().getAudioManager().isConnected()) {
            utilities.error(ctx.getChannel(), "shuffle queue", "\uD83D\uDCE4", "I'm not connected to a voice channel", "Use `" + ctx.getPrefix() + "join` to connect me to your voice channel", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // No audio track is playing
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
            utilities.error(ctx.getChannel(), "shuffle queue", "\uD83C\uDFB2", "The player isn't playing any song", "Use `" + ctx.getPrefix() + "play <song>` to play a song", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
// Send Queue
        // Get queue
        BlockingQueue<AudioTrack> queue = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).scheduler.getQueue();
        // Get the first 15 audio tracks
        int trackCount = Math.min(queue.size(), 15);
        List<AudioTrack> tracks = new ArrayList<>(queue);
        String songs = "";
        for (int i = 0; i < trackCount; i++) {
            songs += ("\n• " + tracks.get(i).getInfo().title);
        }
        // If there are no songs queued
        if (songs.equals("")) {
            songs = "none \uD83D\uDE14";
        }
        // Get audio player
        AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer;
        // Get current playing Song
        String currentPlaying = utilities.hyperlink(audioPlayer.getPlayingTrack().getInfo().title, audioPlayer.getPlayingTrack().getInfo().uri);

        EmbedBuilder queuedSongs = new EmbedBuilder()
                .setAuthor("queue", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .addField("\uD83D\uDCC3 │ queued songs", songs, false)
                .addField("\uD83D\uDCDA │ total songs", Integer.toString(queue.size()), false)
                .addField("\uD83D\uDCBF │ current playing", currentPlaying, false);
        ctx.getChannel().sendMessage(queuedSongs.build()).queue();
    }
}