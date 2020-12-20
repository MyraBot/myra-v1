package com.myra.dev.marian.management;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.myra.dev.marian.commands.Leaderboard;
import com.myra.dev.marian.commands.administrator.*;
import com.myra.dev.marian.commands.administrator.notifications.*;
import com.myra.dev.marian.commands.administrator.reactionRoles.ReactionRolesAdd;
import com.myra.dev.marian.commands.economy.*;
import com.myra.dev.marian.commands.economy.administrator.Currency;
import com.myra.dev.marian.commands.economy.administrator.EconomySet;
import com.myra.dev.marian.commands.economy.administrator.shop.ShopAdd;
import com.myra.dev.marian.commands.economy.administrator.shop.ShopHelp;
import com.myra.dev.marian.commands.economy.administrator.shop.ShopRemove;
import com.myra.dev.marian.commands.economy.blackjack.BlackJack;
import com.myra.dev.marian.commands.fun.Meme;
import com.myra.dev.marian.commands.fun.TextFormatter;
import com.myra.dev.marian.commands.fun.WouldYouRather;
import com.myra.dev.marian.commands.general.Avatar;
import com.myra.dev.marian.commands.general.Calculate;
import com.myra.dev.marian.commands.general.Reminder;
import com.myra.dev.marian.commands.general.information.InformationBot;
import com.myra.dev.marian.commands.general.information.InformationHelp;
import com.myra.dev.marian.commands.general.information.InformationServer;
import com.myra.dev.marian.commands.general.information.InformationUser;
import com.myra.dev.marian.commands.help.*;
import com.myra.dev.marian.commands.leveling.Background;
import com.myra.dev.marian.commands.leveling.Rank;
import com.myra.dev.marian.commands.leveling.administrator.LevelingChannel;
import com.myra.dev.marian.commands.leveling.administrator.LevelingHelp;
import com.myra.dev.marian.commands.leveling.administrator.LevelingSet;
import com.myra.dev.marian.commands.leveling.administrator.levelingRoles.LevelingRolesAdd;
import com.myra.dev.marian.commands.leveling.administrator.levelingRoles.LevelingRolesHelp;
import com.myra.dev.marian.commands.leveling.administrator.levelingRoles.LevelingRolesList;
import com.myra.dev.marian.commands.leveling.administrator.levelingRoles.LevelingRolesRemove;
import com.myra.dev.marian.commands.moderation.Clear;
import com.myra.dev.marian.commands.moderation.Kick;
import com.myra.dev.marian.commands.moderation.ModerationHelp;
import com.myra.dev.marian.commands.moderation.Nick;
import com.myra.dev.marian.commands.moderation.ban.Ban;
import com.myra.dev.marian.commands.moderation.ban.Tempban;
import com.myra.dev.marian.commands.moderation.ban.Unban;
import com.myra.dev.marian.commands.moderation.mute.Mute;
import com.myra.dev.marian.commands.moderation.mute.MuteRole;
import com.myra.dev.marian.commands.moderation.mute.Tempmute;
import com.myra.dev.marian.commands.moderation.mute.Unmute;
import com.myra.dev.marian.listeners.GlobalChat;
import com.myra.dev.marian.listeners.Someone;
import com.myra.dev.marian.listeners.autorole.AutoRoleSet;
import com.myra.dev.marian.listeners.leveling.Leveling;
import com.myra.dev.marian.listeners.leveling.LevelingListener;
import com.myra.dev.marian.listeners.suggestions.SubmitSuggestion;
import com.myra.dev.marian.listeners.suggestions.SuggestionsChannel;
import com.myra.dev.marian.listeners.suggestions.SuggestionsHelp;
import com.myra.dev.marian.listeners.suggestions.SuggestionsToggle;
import com.myra.dev.marian.listeners.welcome.WelcomeChannel;
import com.myra.dev.marian.listeners.welcome.WelcomeColour;
import com.myra.dev.marian.listeners.welcome.WelcomeHelp;
import com.myra.dev.marian.listeners.welcome.WelcomeImage.WelcomeImageBackground;
import com.myra.dev.marian.listeners.welcome.WelcomeImage.WelcomeImageFont;
import com.myra.dev.marian.listeners.welcome.WelcomeImage.WelcomeImageHelp;
import com.myra.dev.marian.listeners.welcome.WelcomeImage.WelcomeImageToggle;
import com.myra.dev.marian.listeners.welcome.WelcomePreview;
import com.myra.dev.marian.listeners.welcome.welcomeDirectMessage.WelcomeDirectMessageHelp;
import com.myra.dev.marian.listeners.welcome.welcomeDirectMessage.WelcomeDirectMessageMessage;
import com.myra.dev.marian.listeners.welcome.welcomeDirectMessage.WelcomeDirectMessageToggle;
import com.myra.dev.marian.listeners.welcome.welcomeEmbed.WelcomeEmbedHelp;
import com.myra.dev.marian.listeners.welcome.welcomeEmbed.WelcomeEmbedMessage;
import com.myra.dev.marian.listeners.welcome.welcomeEmbed.WelcomeEmbedToggle;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandService;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.management.commands.DefaultCommandService;
import com.myra.dev.marian.management.listeners.DefaultListenerService;
import com.myra.dev.marian.management.listeners.ListenerService;
import com.myra.dev.marian.marian.*;

import java.util.Map;

public class Manager {
    public static enum type {STRING, INTEGER, BOOLEAN}

    final static Leveling LEVELING = new Leveling();
    final static CommandService COMMAND_SERVICE = new DefaultCommandService();
    final static ListenerService LISTENER_SERVICE = new DefaultListenerService();

    public static Map<Command, CommandSubscribe> getCommands() {
        return COMMAND_SERVICE.getCommands();
    }

    // Return leveling
    public static Leveling getLeveling() {
        return LEVELING;
    }

    public void commandRegistry(EventWaiter waiter) {
        // Register commands
        COMMAND_SERVICE.register(
                // Marian
                new SetGuildPremium(),
                new MariansDiscordEmbeds(),
                new GetInvite(),
                new Dashboard(),
                new Shutdown(),
                // Administrator
                new Prefix(),
                new Say(),
                new Toggle(),
                new GlobalChatChannel(),

                new ReactionRolesAdd(),
                //
                new LogChannel(),
                // Help
                new Commands(),
                new Help(),
                new Invite(),
                new Ping(),
                new Support(),
                new Feature(),
                new Report(),
                new Vote(),
                // General
                new InformationHelp(),
                new InformationServer(),
                new InformationUser(),
                new InformationBot(),

                new Avatar(),
                new Calculate(),
                new Reminder(),
                // Leveling
                new LevelingHelp(),
                new LevelingSet(),

                new LevelingChannel(),

                new LevelingRolesHelp(),
                new LevelingRolesList(),
                new LevelingRolesAdd(),
                new LevelingRolesRemove(),

                new Rank(),
                new Background(),
                new Leaderboard(),
                // Economy
                new EconomyHelp(),
                new EconomySet(),
                new Currency(),

                new ShopHelp(),
                new ShopAdd(),
                new ShopRemove(),

                new Balance(),
                new Daily(),
                new Streak(),
                new Fish(),
                new BlackJack(),
                new Give(),

                new Buy(),
                // Fun
                new Meme(),
                new TextFormatter(),
                new WouldYouRather(),
                // Suggestions
                new SuggestionsHelp(),
                new SuggestionsChannel(),
                new SuggestionsToggle(),

                new SubmitSuggestion(),
                // Moderation
                new ModerationHelp(),

                new Ban(),
                new Tempban(),
                new Unban(),

                new MuteRole(),
                new Mute(),
                new Tempmute(),
                new Unmute(),

                new Clear(),
                new Kick(),
                new Nick(),
                // Music
                /*new MusicHelp(),
                new MusicJoin(),
                new MusicLeave(),
                new MusicPlay(),
                new MusicStop(),
                new MusicShuffle(),
                new MusicRepeat(),
                new MusicInformation(),
                new MusicQueue(),
                new MusicSkip(),
                new MusicClearQueue(),
                new MusicController(),*/
                // Autorole
                new AutoRoleSet(),
                // Notification
                new NotificationsHelp(),
                new NotificationsChannel(),
                new NotificationsList(),

                new YouTuber(),
                new Streamer(),
                // Welcome
                new WelcomeHelp(),
                new WelcomePreview(),
                new WelcomeChannel(),
                new WelcomeColour(),
                // Welcome Image
                new WelcomeImageHelp(),
                new WelcomeImageToggle(),
                new WelcomeImageBackground(),
                new WelcomeImageFont(),
                // Welcome direct message
                new WelcomeDirectMessageHelp(),
                new WelcomeDirectMessageToggle(),
                new WelcomeDirectMessageMessage(),
                // Welcome embed
                new WelcomeEmbedHelp(),
                new WelcomeEmbedToggle(),
                new WelcomeEmbedMessage()
        );

        // Register listeners
        LISTENER_SERVICE.register(
                // Administrator
                new Someone(),
                new GlobalChat(),

                new LevelingListener()
        );
    }
}
