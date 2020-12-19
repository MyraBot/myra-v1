package com.myra.dev.marian.management;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.myra.dev.marian.Bot;
import com.myra.dev.marian.commands.Leaderboard;
import com.myra.dev.marian.commands.administrator.notifications.NotificationsList;
import com.myra.dev.marian.commands.administrator.reactionRoles.ReactionRolesAdd;
import com.myra.dev.marian.commands.economy.blackjack.BlackJack;
import com.myra.dev.marian.commands.fun.TextFormatter;
import com.myra.dev.marian.commands.general.Reminder;
import com.myra.dev.marian.commands.general.information.InformationServer;
import com.myra.dev.marian.commands.help.InviteThanks;
import com.myra.dev.marian.commands.leveling.Background;
import com.myra.dev.marian.commands.moderation.ban.Tempban;
import com.myra.dev.marian.commands.moderation.mute.MutePermissions;
import com.myra.dev.marian.commands.moderation.mute.Tempmute;
import com.myra.dev.marian.commands.music.commands.MusicController;
import com.myra.dev.marian.commands.music.commands.MusicPlay;
import com.myra.dev.marian.database.MongoDbUpdate;
import com.myra.dev.marian.listeners.ReactionRoles;
import com.myra.dev.marian.listeners.autorole.AutoroleAssign;
import com.myra.dev.marian.listeners.leveling.VoiceCall;
import com.myra.dev.marian.listeners.notifications.TwitchNotification;
import com.myra.dev.marian.listeners.notifications.YouTubeNotification;
import com.myra.dev.marian.listeners.welcome.WelcomeListener;
import com.myra.dev.marian.management.commands.CommandService;
import com.myra.dev.marian.management.listeners.Listener;
import com.myra.dev.marian.management.listeners.ListenerService;
import com.myra.dev.marian.marian.Roles;
import com.myra.dev.marian.marian.ServerTracking;
import com.myra.dev.marian.utilities.APIs.Twitch;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Listeners extends ListenerAdapter {
    private final CommandService commandService = Manager.COMMAND_SERVICE;
    private final ListenerService listenerService = Manager.LISTENER_SERVICE;
    private final EventWaiter waiter;
    public static boolean ready = false;

    public Listeners(final EventWaiter waiter) {
        this.waiter = waiter;

        new Manager().commandRegistry(waiter); // Load all commands
    }


    private final static Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    private final static String onlineInfo = "Bot online!";

    //JDA Events
    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        try {
            new MongoDbUpdate().updateDatabase(event); // Update database

            while (!ready) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // restore interrupted status
                }
            }

            new Reminder().onReady(event); // Load reminders

            new Tempban().loadUnbans(event); // Load bans
            new Tempmute().onReady(event); // Load mutes

            new Twitch().jdaReady(event); // Get access token for twitch
            // Start notifications
            new YouTubeNotification().start(event);// Start twitch notifications
            new TwitchNotification().jdaReady(event); // Start youtube notifications

            // Marian's Discord role
            new Roles().jdaReady(event);

            online(); // Change profile picture and activity
            LOGGER.info(onlineInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void online() {
        final int start = 60 - LocalDateTime.now().getMinute() % 60; // Get time to start changing the profile picutre
        // Set her status to online
        Bot.shardManager.getShards().forEach(bot -> {
            bot.getPresence().setActivity(Activity.listening("~help │ " + bot.getGuilds().size() + " servers")); // Change status
        });
        // Get a random one
        Utilities.TIMER.scheduleAtFixedRate(() -> {
            final InputStream profilePicture = null; // Create variable for new profile picture
            while (profilePicture == null) { // If profile picture is still null
                this.getClass().getClassLoader().getResourceAsStream("profilePicture" + new Random().nextInt(9) + ".png");
            }
            // Change profile
            Bot.shardManager.getShards().forEach(bot -> {
                try {
                    bot.getPresence().setActivity(Activity.listening("~help │ " + bot.getGuilds().size() + " servers")); // Change status
                    bot.getSelfUser().getManager().setAvatar(Icon.from(profilePicture)).queue(); // Change profile picture
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }, start, 60, TimeUnit.MINUTES);
    }
    // Errors
    private final String missingPermsMESSAGE_WRITE = "Cannot perform action due to a lack of Permission. Missing permission: MESSAGE_WRITE";
    private final String missingPermsVIEW_CHANNEL = "Cannot perform action due to a lack of Permission. Missing permission: VIEW_CHANNEL";
    //  Run actions
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (!ready) return;
        try {
            if (event.getMessage().getFlags().contains(Message.MessageFlag.IS_CROSSPOST))
                return; // Message is a server announcement
            if (event.getMessage().isWebhookMessage()) return; // Message is a WebHook
            if (event.getAuthor().isBot()) return; // Message is from another bot

            commandService.processCommandExecution(event, waiter);
            listenerService.processCommandExecution(event);
        } catch (Exception exception) {
            final String error = exception.getMessage(); // Get error
            if (exception.getMessage() == null) {
                exception.printStackTrace();
                return;
            }
            // Missing permissions: MESSAGE_WRITE
            if (error.startsWith(missingPermsMESSAGE_WRITE)) {
                return;
            }
            // Missing permissions: VIEW_CHANNEL
            else if (error.equals(missingPermsVIEW_CHANNEL)) {
                error(event, "I'm not able to see the channel."); // Send error}
            }
            // Other error
            else {
                error(event, "An error accrued, please contact " + Utilities.getUtils().hyperlink("my developer", Utilities.getUtils().marianUrl()));
                exception.printStackTrace();
            }
        }
    }

    private void error(GuildMessageReceivedEvent event, String error) {
        final Utilities utils = Utilities.getUtils(); // Get utilities

        event.getChannel().sendMessage(new EmbedBuilder()
                .setAuthor("error", "https://discord.gg/nG4uKuB", event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setColor(utils.red)
                .setDescription(error + "\n" + utils.hyperlink("If you need more help please join the support server", "https://discord.gg/nG4uKuB"))
                .build()
        ).queue();
    }

    /**
     * reactions
     */
    private final NotificationsList notificationsList = new NotificationsList();
    private final ReactionRoles reactionRoles = new ReactionRoles();
    private final InformationServer informationServer = new InformationServer();
    private final TextFormatter textFormatter = new TextFormatter();
    private final Background background = new Background();
    private final BlackJack blackJack = new BlackJack();
    private final Leaderboard leaderboard = new Leaderboard();

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        try {
            if (!ready) return;
            if (event.getUser().isBot()) return; // Don't react to bots

            // Administrator
            notificationsList.switchList(event); // List notification
            reactionRoles.reactionRoleAssign(event); // Reaction roles
            // Commands
            informationServer.guildMessageReactionAddEvent(event);
            // Fun
            textFormatter.guildMessageReactionAddEvent(event); // Text formatter
            // Leveling
            leaderboard.switchLeaderboard(event); // Switch what leaderboard shows
            // Economy
            background.confirm(event);
            blackJack.reaction(event); // Blackjack
            // Music
            new MusicPlay().guildMessageReactionAddEvent(event);
            new MusicController().guildMessageReactionAddEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        try {
            if (!ready) return;
            reactionRoles.reactionRoleRemove(event); // Reaction roles remove listener
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildUpdateName(GuildUpdateNameEvent event) {
        try {
            if (!ready) return;
            new MongoDbUpdate().guildNameUpdated(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTextChannelCreate(TextChannelCreateEvent event) {
        try {
            if (!ready) return;
            // Set permissions for mute role
            new MutePermissions().textChannelCreateEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        try {
            if (!ready) return;
            // Welcome
            new WelcomeListener().welcome(event);

            // Autorole
            new AutoroleAssign().onGuildMemberJoin(event);

            // Exclusive role
            new Roles().exclusive(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Guild Voice Events
    private final VoiceCall voiceCall = new VoiceCall();

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        try {
            if (!ready) return;
            voiceCall.updateXpGain(event.getChannelJoined()); // Start xp gian
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        try {
            if (!ready) return;
            voiceCall.updateXpGain(event.getChannelLeft()); // Update xp for users, who are still in old voice call
            voiceCall.updateXpGain(event.getChannelJoined()); // Update xp for users in new voice call
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        try {
            if (!ready) return;
            voiceCall.stopXpGain(event.getMember()); // Stop xp gain
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildVoiceMute(GuildVoiceMuteEvent event) {
        try {
            if (!ready) return;
            voiceCall.updateXpGain(event); // Update xp gain
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        try {
            if (!ready) return;
            // Add guild document to database
            new MongoDbUpdate().guildJoinEvent(event);
            // Server tracking message
            new ServerTracking().guildJoinEvent(event);
            // Thank message to server owner
            new InviteThanks().guildJoinEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        try {
            if (!ready) return;
            //delete guild document
            new MongoDbUpdate().guildLeaveEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
