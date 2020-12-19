package com.myra.dev.marian.commands.music.commands;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.TrackScheduler;
import com.myra.dev.marian.utilities.MessageReaction;
import com.myra.dev.marian.utilities.Utilities;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

@CommandSubscribe(
        name = "music controller"
)
public class MusicController implements Command {

    private static HashMap<Message, Boolean> cancelTimer = new HashMap<>();

    @Override
    public void execute(CommandContext ctx) throws Exception {
        final AudioPlayer player = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer;
        // No audio track is playing
        if (player.getPlayingTrack() == null) {
            Utilities.getUtils().error(ctx.getChannel(), "music controller", "\uD83C\uDF9A", "The player isn't playing any song", "Use `" + ctx.getPrefix() + "play <song>` to play a song", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        //music controller
        EmbedBuilder musicController = new EmbedBuilder()
                .setAuthor("music", null, ctx.getEvent().getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .addField("current playing track", PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack().getInfo().title, false)
                .setFooter(displayPosition(player));
        Message message = ctx.getChannel().sendMessage(musicController.build()).complete();
        //updating embed
        Timer update = new Timer();
        Timer cancel = new Timer();
        Timer removeHashMap = new Timer();
        // Add to HashMap
        cancelTimer.put(message, false);

        update.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                EmbedBuilder update = new EmbedBuilder();
                update.setAuthor("music", null, ctx.getEvent().getJDA().getSelfUser().getEffectiveAvatarUrl());
                //if no song is playing
                if (player.getPlayingTrack() == null) {
                    update.addField("\u23F9 │ player stopped", "no song playing", false);
                }
                //song is paused
                else if (player.isPaused()) {
                    update.addField("\u23F8 │ player paused", player.getPlayingTrack().getInfo().title, false);
                } else {
                    update
                            .setColor(Utilities.getUtils().blue)
                            .addField("\u25B6 │ current playing track", PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack().getInfo().title, false)
                            .setFooter(displayPosition(player));
                }

                message.editMessage(update.build()).queue();
            }
        }, 2 * 1000, 2 * 1000);
        //add reactions
        message.addReaction("\u23EF\uFE0F").queue();
        message.addReaction("\u23ED\uFE0F").queue();
        message.addReaction("\u23F9\uFE0F").queue();
        //add message id to HashMap
        MessageReaction.add(ctx.getGuild(), "musicController", message, ctx.getAuthor(),false, "\u23EF\uFE0F", "\u23ED\uFE0F", "\u23F9\uFE0F");

        //cancel timer
        cancel.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
                    // Save boolean value
                    cancelTimer.put(message, true);

                    removeHashMap.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.getPlayingTrack() == null) {
                                MessageReaction.remove("musicController", message);
                                update.cancel();
                                cancel.cancel();
                                removeHashMap.cancel();
                                cancelTimer.remove(message);
                                System.out.println("removing command from hashmap");
                            }
                        }
                    }, 5 * 1000);
                }
                //cancel timers
                if (cancelTimer.get(message)) {
                    update.cancel();
                    cancel.cancel();
                    // Remove from HashMap
                    cancelTimer.remove(message);
                }
            }
        }, 5 * 1000, 5 * 1000);
    }

    //reactions
    public void guildMessageReactionAddEvent(GuildMessageReactionAddEvent event) {
        //if reaction was added on the wrong message return
        if (!MessageReaction.check(event, "musicController", true)) return;

        AudioPlayer player = PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer;
        TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler;

        //play and pause
        if (event.getReactionEmote().getEmoji().equals("\u23EF\uFE0F") && !event.getMember().getUser().isBot()) {
            player.setPaused(!player.isPaused());
            event.getReaction().removeReaction(event.getMember().getUser()).queue();
        }
        //skip
        if (event.getReactionEmote().getEmoji().equals("\u23ED\uFE0F") && !event.getMember().getUser().isBot()) {
            scheduler.nextTrack();
            event.getReaction().removeReaction(event.getMember().getUser()).queue();
        }
        //stop
        if (event.getReactionEmote().getEmoji().equals("\u23F9\uFE0F") && !event.getMember().getUser().isBot()) {
            player.stopTrack();
            player.setPaused(false);
            event.getReaction().removeReaction(event.getMember().getUser()).queue();
        }
    }

    private String displayPosition(AudioPlayer player) {
        //split song duration in 15 parts
        Long sections = player.getPlayingTrack().getDuration() / 15;
        //get the part the song is in
        Long atSection = player.getPlayingTrack().getPosition() / sections;

        StringBuilder positionRaw = new StringBuilder("000000000000000")
                .insert(Math.toIntExact(atSection), '1');

        return positionRaw.toString().replaceAll("0", "▬").replace("1", "\uD83D\uDD18");
    }
}