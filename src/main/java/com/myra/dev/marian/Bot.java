package com.myra.dev.marian;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.myra.dev.marian.management.Listeners;
import com.myra.dev.marian.utilities.ConsoleColours;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Bot {
    public static ShardManager shardManager;

    private final static String TOKEN = "NzE4NDQ0NzA5NDQ1NjMyMTIy.Xto9xg.JcLjYVCFcXMjvJmvvnBKuqUljao";
    private final static String LOADING_STATUS = "loading bars fill";
    private final static String OFFLINE_INFO = ConsoleColours.RED + "Bot offline" + ConsoleColours.RESET;

    private final EventWaiter waiter = new EventWaiter();

    private Bot() throws LoginException, RateLimitedException {
        DefaultShardManagerBuilder jda = DefaultShardManagerBuilder.create(
                TOKEN,
                // Enabled events
                GatewayIntent.GUILD_MEMBERS,// Enabling events with members (Member join, leave, ...)
                GatewayIntent.GUILD_MESSAGES, // Enabling message events (send, edit, delete, ...)
                GatewayIntent.GUILD_MESSAGE_REACTIONS, // Reaction add remove bla bla
                GatewayIntent.GUILD_VOICE_STATES,

                GatewayIntent.GUILD_EMOJIS // Emote add/update/delete events. Also is needed for the CacheFlag
        )
                .enableCache(
                        CacheFlag.EMOTE,
                        CacheFlag.VOICE_STATE
                )
                .disableCache(
                        CacheFlag.ACTIVITY,
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.MEMBER_OVERRIDES,
                        CacheFlag.ROLE_TAGS
                )
                .setStatus(OnlineStatus.IDLE)
                .setActivity(Activity.watching(LOADING_STATUS))

                .addEventListeners(waiter, new Listeners(waiter));
        // Build JDA
        shardManager = jda.build();
        consoleListener(); // Add console listener
    }

    // Main method
    public static void main(String[] args) throws LoginException, RateLimitedException {
        new Bot();
    }


    private void consoleListener() {
        String line;
        // Create a Buffered reader, which reads the lines of the console
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while ((line = reader.readLine()) != null) {
                // Shutdown command
                if (line.equalsIgnoreCase("shutdown")) {
                    if (shardManager != null) {
                        // Set status to "offline"
                        shardManager.setStatus(OnlineStatus.OFFLINE);
                        // Stop shard manager
                        shardManager.shutdown();
                        System.out.println(OFFLINE_INFO);
                        // Stop jar file from running
                        System.exit(0);
                    }
                }
                // Help command
                else {
                    System.out.println("Use " + ConsoleColours.RED + "shutdown" + ConsoleColours.RESET + " to shutdown the program");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}