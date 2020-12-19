package com.myra.dev.marian.listeners.notifications;

import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.managers.NotificationsTwitchManager;
import com.myra.dev.marian.utilities.APIs.Twitch;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.bson.Document;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TwitchNotification {

    public void jdaReady(ReadyEvent event) throws Exception {
        final int start = 5 - LocalDateTime.now().getMinute() % 5;

        Utilities.TIMER.scheduleAtFixedRate(() -> {   // Loop

            try {
                final Iterator<Guild> guilds = event.getJDA().getGuilds().iterator(); // Create an iterator for the guilds
                while (guilds.hasNext()) { // Loop through every guild
                    final Guild guild = guilds.next(); // Get next guild
                    Database db = new Database(guild); // Get database

                    // Get variables
                    List<String> streamers = NotificationsTwitchManager.getInstance().getStreamers(guild); // Get all streamers
                    String channelRaw = db.getNested("notifications").getString("channel");

                    if (streamers.isEmpty()) continue;  // No streamers are set

                    // If no notifications channel is set
                    if (channelRaw.equals("not set")) {
                        Utilities.getUtils().error(guild.getDefaultChannel(), "notifications", "\uD83D\uDD14", "No notifications channel specified", "To set a notifications channel type in `" + db.getString("prefix") + "notification channel <channel>`", guild.getIconUrl());
                        continue;
                    }
                    TextChannel channel = guild.getTextChannelById(channelRaw); // Get notifications channel

                    // For each streamer
                    for (String streamer : streamers) {
                        JSONObject stream = new Twitch().getStream(streamer); // Get stream information

                        // If no stream is found
                        if (stream == null) {
                            Utilities.getUtils().error(channel, "notifications twitch", "\uD83D\uDD14", "No channel found", "**" + streamer + "** doesn't exist anymore", guild.getIconUrl());
                            continue;
                        }

                        // Streamer is offline
                        if (!stream.getBoolean("is_live")) continue;

                        //get stream start
                        final ZonedDateTime date = ZonedDateTime.parse(stream.getString("started_at"));
                        long publishedAtInMillis = date.toInstant().toEpochMilli(); // Get stream start in milliseconds

                        // Last twitch check was already made when the video came out
                        if (publishedAtInMillis < MongoDb.getInstance().getCollection("config").find().first().getLong("twitch refresh"))
                            continue;

                        // Get all values
                        final String id = stream.getString("id");
                        final String name = stream.getString("display_name"); // Get user name of streamer
                        final String title = stream.getString("title"); // Get stream title
                        final String thumbnail = stream.getString("thumbnail_url"); // Get profile picture
                        final String preview = "https://static-cdn.jtvnw.net/previews-ttv/live_user_" + name + "-440x248.jpg";
                        // Create embed
                        EmbedBuilder notification = new EmbedBuilder()
                                .setAuthor(name, "https://www.twitch.tv/" + name, thumbnail)
                                .setColor(Utilities.getUtils().blue)
                                .setDescription(Utilities.getUtils().hyperlink(title, "https://www.twitch.tv/" + name) + "\n")
                                .setThumbnail(thumbnail)
                                .setImage(preview)
                                .setTimestamp(date.toInstant());
                        // If streamer set a game
                        if (!stream.getString("game_id").equals("0")) {
                            notification.appendDescription(new Twitch().getGame(stream.getString("game_id"))); // Add game to notification
                        }
                        assert channel != null;
                        channel.sendMessage(notification.build()).queue(); // Send stream notification
                    }
                }

                // Update last refresh in database
                final Document updatedDocument = MongoDb.getInstance().getCollection("config").find().first(); // Get config document
                updatedDocument.replace("twitch refresh", System.currentTimeMillis()); // Update last check
                MongoDb.getInstance().getCollection("config").findOneAndReplace(MongoDb.getInstance().getCollection("config").find().first(), updatedDocument); // Update document

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, start, 5, TimeUnit.MINUTES);
    }
}
