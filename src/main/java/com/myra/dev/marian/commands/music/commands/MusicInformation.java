package com.myra.dev.marian.commands.music.commands;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        command = "music information",
        name = "music information",
        aliases = {"music info", "track information", "track info", "track", "song", "song information", "song info"}
)
public class MusicInformation implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Get audio player
        final AudioPlayer player = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer;
        // Get utilities
        final Utilities utilities = Utilities.getUtils();
        //the bot isn't connected to any voice channel
        if (!ctx.getGuild().getAudioManager().isConnected()) {
            new Error(ctx.getEvent())
                    .setCommand("track information")
                    .setEmoji("\uD83D\uDDD2")
                    .setMessage("I'm not connected to a voice channel")
                    .send();
            return;
        }
        //bot isn't playing any song
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
            new Error(ctx.getEvent())
                    .setCommand("track information")
                    .setEmoji("\uD83D\uDDD2")
                    .setMessage("The player isn't playing any song")
                    .send();
            return;
        }
        EmbedBuilder info = new EmbedBuilder()
                .setAuthor(player.getPlayingTrack().getInfo().title + " by " + player.getPlayingTrack().getInfo().author, player.getPlayingTrack().getInfo().uri, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .setDescription(player.isPaused() ? "\u23F8\uFE0F " : "\u23F8\uFE0F " + utilities.formatTime(player.getPlayingTrack().getPosition()) + " - " + utilities.formatTime(player.getPlayingTrack().getDuration()))
                .setFooter(displayPosition(player));
        ctx.getChannel().sendMessage(info.build()).queue();
    }

    private String displayPosition(AudioPlayer player) {
        //split song duration in 15 parts
        long sections = player.getPlayingTrack().getDuration() / 15;
        //get the part the song is in
        long atSection = player.getPlayingTrack().getPosition() / sections;

        StringBuilder positionRaw = new StringBuilder("000000000000000")
                .insert(Math.toIntExact(atSection), '1');

        return positionRaw.toString().replaceAll("0", "▬").replace("1", "\uD83D\uDD18");
    }
}