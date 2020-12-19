package com.myra.dev.marian.commands;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.allMethods.LeaderboardType;
import com.myra.dev.marian.database.documents.MemberDocument;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.CustomEmote;
import com.myra.dev.marian.utilities.MessageReaction;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.List;

@CommandSubscribe(
        command = "leaderboard",
        name = "leaderboard",
        aliases = {"top"}
)
public class Leaderboard implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        //create embed
        EmbedBuilder leaderboard = new EmbedBuilder()
                .setAuthor(ctx.getGuild().getName() + "'s leaderboard", null, ctx.getGuild().getIconUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription(ctx.getGuild().getName() + "'s level leaderboard \n")
                .appendDescription(levelLeaderboard(ctx.getGuild()));
        //send message
        ctx.getChannel().sendMessage(leaderboard.build()).queue(message -> {
            // Add reactions
            message.addReaction("\uD83C\uDFC6").queue(); // Add level emoji
            message.addReaction(Utilities.getUtils().getEmote(ctx.getEvent().getJDA(), CustomEmote.coin)).queue(); // Add balance emote

            final Emote coin = Utilities.getUtils().getEmote(ctx.getEvent().getJDA(), CustomEmote.coin); // Get coin emote
            MessageReaction.add(ctx.getGuild(), "leaderboard", message, ctx.getAuthor(),true, "\uD83C\uDFC6", coin.getId()); // Add message to reactions
        });
    }

    public void switchLeaderboard(GuildMessageReactionAddEvent event) {
        if (!MessageReaction.check(event, "leaderboard", false)) return;

        // If reaction is emote
        if (event.getReactionEmote().isEmote()) {
            // Reactions is coin
            if (event.getReactionEmote().getEmote().equals(Utilities.getUtils().getEmote(event.getJDA(), CustomEmote.coin))) {
                //create embed
                EmbedBuilder leaderboard = new EmbedBuilder()
                        .setAuthor(event.getGuild().getName() + "'s leaderboard", null, event.getGuild().getIconUrl())
                        .setColor(Utilities.getUtils().blue)
                        .setDescription(event.getGuild().getName() + "'s balance leaderboard \n")
                        .appendDescription(balanceLeaderboard(event.getGuild()));
                //send message
                event.retrieveMessage().queue(message -> {
                    message.editMessage(leaderboard.build()).queue(); // Edit message to show the balance leaderboard
                    event.getReaction().removeReaction(event.getUser()).queue(); // Remove reaction
                });
            }
        }
        else {
            if (event.getReactionEmote().getEmoji().equals("\uD83C\uDFC6")) {
                //create embed
                EmbedBuilder leaderboard = new EmbedBuilder()
                        .setAuthor(event.getGuild().getName() + "'s leaderboard", null, event.getGuild().getIconUrl())
                        .setColor(Utilities.getUtils().blue)
                        .setDescription(event.getGuild().getName() + "'s level leaderboard \n")
                        .appendDescription(levelLeaderboard(event.getGuild()));
                //send message
                event.retrieveMessage().queue(message -> {
                    message.editMessage(leaderboard.build()).queue(); // Edit message to show the balance leaderboard
                    event.getReaction().removeReaction(event.getUser()).queue(); // Remove reaction
                });
            }
        }
    }

    private String balanceLeaderboard(Guild guild) {
        List<MemberDocument> leaderboard = new Database(guild).getMembers().getLeaderboard(LeaderboardType.BALANCE); //get leaderboard
        // Create leaderboard
        StringBuilder top10 = new StringBuilder();
        // Add first 10 members
        for (int i = 0; i < leaderboard.size(); i++) {
            if (i > 10) break; // Show only the first 10 members
            if (guild.getMemberById(leaderboard.get(i).getId()) == null) continue;
            top10.append(i + 1 + " \uD83C\uDF97 `" + Utilities.getUtils().formatNumber(leaderboard.get(i).getBalance()) + "` **" + guild.getMemberById(leaderboard.get(i).getId()).getUser().getName() + "**\n");
        }
        return top10.toString(); // Return the leaderboard as a string
    }

    private String levelLeaderboard(Guild guild) {
        List<MemberDocument> leaderboard = new Database(guild).getMembers().getLeaderboard(LeaderboardType.LEVEL); //get leaderboard
        // Create leaderboard
        StringBuilder top10 = new StringBuilder();
        // Add first 10 members
        for (int i = 0; i < leaderboard.size(); i++) {
            if (i > 10) break; // Show only the first 10 members
            if (guild.getMemberById(leaderboard.get(i).getId()) == null) continue;
            top10.append(i + 1 + " \uD83C\uDF97 `" + leaderboard.get(i).getLevel() + "` **" + guild.getMemberById(leaderboard.get(i).getId()).getUser().getName() + "**\n");
        }

        /*        String top10 =
                        "1 \uD83D\uDC51 `" + leaderboardList.get(0).getLevel() + "` **" + leaderboardList.get(0).getName() + "**\n" +
                                "2 \uD83D\uDD31 `" + leaderboardList.get(1).getLevel() + "` **" + leaderboardList.get(1).getName() + "**\n" +
                                "3 \uD83C\uDFC6 `" + leaderboardList.get(2).getLevel() + "` **" + leaderboardList.get(2).getName() + "**\n" +
                                "4 \uD83C\uDF96 `" + leaderboardList.get(3).getLevel() + "` **" + leaderboardList.get(3).getName() + "**\n" +
                                "5 \uD83C\uDFC5 `" + leaderboardList.get(4).getLevel() + "` **" + leaderboardList.get(4).getName() + "**\n" +
                                "6 \u26A1 `" + leaderboardList.get(5).getLevel() + "` **" + leaderboardList.get(5).getName() + "**\n" +
                                "7 \uD83C\uDF97 `" + leaderboardList.get(6).getLevel() + "` **" + leaderboardList.get(6).getName() + "**\n" +
                                "8 \uD83C\uDF97 `" + leaderboardList.get(7).getLevel() + "` **" + leaderboardList.get(7).getName() + "**\n" +
                                "9 \uD83C\uDF97 `" + leaderboardList.get(8).getLevel() + "` **" + leaderboardList.get(8).getName() + "**\n" +
                                "10 \uD83C\uDF97 `" + leaderboardList.get(9).getLevel() + "` **" + leaderboardList.get(9).getName() + "**\n";*/

        return top10.toString(); // Return the leaderboard as a string
    }
}
