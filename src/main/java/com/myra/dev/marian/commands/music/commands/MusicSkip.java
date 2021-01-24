package com.myra.dev.marian.commands.music.commands;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        command = "skip",
        name = "skip",
        aliases = {"next"}
)
public class MusicSkip implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        // Errors
        if (!ctx.getGuild().getAudioManager().isConnected()) { // Bot isn't connected to a voice channel
            new Error(ctx.getEvent())
                    .setCommand("skip")
                    .setEmoji("\u23ED\uFE0F")
                    .setMessage("I'm not connected to a voice channel")
                    .send();
            return;
        }
        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) { // No audio track is playing
            new Error(ctx.getEvent())
                    .setCommand("skip")
                    .setEmoji("\u23ED\uFE0F")
                    .setMessage("The player isn't playing any song")
                    .send();
            return;
        }
// Skip current playing track
        // Get audio player
        AudioTrack track = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack();
        // Send success message
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("skip", track.getInfo().uri, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .setDescription("Skipped track: " + utilities.hyperlink(track.getInfo().title, track.getInfo().uri));
        ctx.getChannel().sendMessage(success.build()).queue();
        // Skip track
        PlayerManager.getInstance().getMusicManager(ctx.getGuild()).scheduler.nextTrack();
    }
}
