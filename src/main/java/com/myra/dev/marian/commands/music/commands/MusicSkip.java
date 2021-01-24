package com.myra.dev.marian.commands.music.commands;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

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


        final int size = (int) ctx.getMember().getVoiceState().getChannel().getMembers().stream().filter(member -> !member.getUser().isBot()).count();

        // Skip song
        if (size <= 4 || !new Database(ctx.getGuild()).getBoolean("musicVoting")) {
            skip(ctx.getChannel(), ctx.getAuthor());
        }

        // Only start voting if more than 4 members are in the voice call and music voting is enabled
        else new MusicVoteListener().onMusicCommand(ctx.getEvent().getMessage()); // Start voting
    }

    public void skip(TextChannel channel, User author) {
        final AudioTrack track = PlayerManager.getInstance().getMusicManager(channel.getGuild()).audioPlayer.getPlayingTrack(); // Get audio player
        // Send success message
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("skip", track.getInfo().uri, author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Skipped track: " + Utilities.getUtils().hyperlink(track.getInfo().title, track.getInfo().uri));
        channel.sendMessage(success.build()).queue();
        // Skip track
        PlayerManager.getInstance().getMusicManager(channel.getGuild()).scheduler.nextTrack();
    }
}
