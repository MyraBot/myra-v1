package com.myra.dev.marian.commands.music.commands;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.Manager;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.PlayerManager;
import com.myra.dev.marian.utilities.APIs.LavaPlayer.TrackScheduler;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MusicVoteListener {
    public void onMusicCommand(Message message) {
        final Emote emote = Utilities.getUtils().getEmote("greenTick");
        message.addReaction(emote).queue();
    }

    private final MusicSkip musicSkip = new MusicSkip();
    private final MusicClearQueue musicClearQueue = new MusicClearQueue();

    public void onVoteAdd(GuildMessageReactionAddEvent event) {
        if (!event.getReaction().getReactionEmote().isEmote()) return;
        final Emote emote = Utilities.getUtils().getEmote("greenTick"); // Get vote reaction emote
        if (!event.getReaction().getReactionEmote().getEmote().equals(emote)) return;

        event.retrieveMessage().queue(
                message -> { // Retrieve message
                    System.out.println("retrieved message");
                    event.retrieveMember().queue(member -> {
                        if (member.getVoiceState() == null) return; // Message author isn't in a vc anymore
                        if (!member.getVoiceState().inVoiceChannel()) return; // Reaction author isn't in a vc

                        final VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel(); // Get voice channel
                        if (voiceChannel.getMembers().isEmpty()) return; // Everyone left the voice call
                        final List<Member> members = voiceChannel.getMembers(); // Get members in a voice channel
                        final int size = (int) members.stream().filter(vcaller -> !vcaller.getUser().isBot()).count();

                        AtomicInteger votes = new AtomicInteger();
                        message.getReactions().forEach(reaction -> {
                            // Right emote
                            if (reaction.getReactionEmote().getEmote().equals(emote)) {
                                reaction.retrieveUsers().forEachAsync(user -> {
                                    // Add 1 vote
                                    if (members.stream().anyMatch(reactedMember -> reactedMember.getUser() == user) && !user.isBot())
                                        votes.addAndGet(1);

                                    return true; // Iterates over all entities until the provided action returns false
                                }).whenComplete((input, exception) -> { // Run after all entities are iterated
                                    if (exception != null) {
                                        exception.printStackTrace();
                                    } else {
                                        System.out.println(votes.get());
                                        if (votes.get() < size / 2) return; // Not enough votes

                                        final String prefix = new Database(event.getGuild()).getString("prefix"); // Get prefix
                                        final TrackScheduler scheduler = PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler;  // Get track scheduler
                                        final List<String> skipExecutors = Manager.COMMAND_SERVICE.getCommandExecutors(MusicSkip.class); /** Get all executors from the {@link MusicSkip} class*/
                                        final List<String> clearQueueExecutors = Manager.COMMAND_SERVICE.getCommandExecutors(MusicClearQueue.class); /** Get all executors from the {@link MusicClearQueue} class*/

                                        // Skip song
                                        if (skipExecutors.stream().anyMatch(executor -> message.getContentRaw().equalsIgnoreCase(prefix + executor))) {
                                            musicSkip.skip(event.getChannel(), message.getAuthor());
                                        }
                                        // Clear queue
                                        if (clearQueueExecutors.stream().anyMatch(executor -> message.getContentRaw().equalsIgnoreCase(prefix + executor))) {
                                            musicClearQueue.clearQueue(scheduler, event.getChannel(), message.getAuthor());
                                        }
                                    }
                                });
                            }
                        });
                    });
                },
                error -> {

                });
    }
}
