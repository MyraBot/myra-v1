package com.myra.dev.marian.commands.economy.blackjack;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.allMethods.GetMember;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.HashMap;

@CommandSubscribe(
        name = "blackjack",
        aliases = {"bj"}
)
public class BlackJack implements Command {
    private static HashMap<String, HashMap<String, Game>> games = new HashMap<>();

    /**
     * Start game.
     *
     * @param ctx The command context.
     * @throws Exception
     */
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("blackjack", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "blackjack <bet>`", "\uD83C\uDCCF │ Play blackjack against " + ctx.getEvent().getJDA().getSelfUser().getName(), false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Errors
        // Search user in games
        if (games.containsKey(ctx.getGuild().getId())) { // Only check for user if guild is already in the hashmap
            for (String messageId : games.get(ctx.getGuild().getId()).keySet()) {
                for (Player player : games.get(ctx.getGuild().getId()).get(messageId).getPlayers()) {
                    if (player.getPlayer().equals(ctx.getAuthor())) {
                        ctx.getChannel().retrieveMessageById(messageId).queue(message -> {
                            // If user has already started a game
                            Utilities.getUtils().error(ctx.getChannel(), "blackjack", "\uD83C\uDCCF", "You already started a game", "Please finish the " + Utilities.getUtils().hyperlink("game", message.getJumpUrl()) + " you started first", ctx.getAuthor().getEffectiveAvatarUrl());
                        });
                        return;
                    }
                }
            }
        }
        // Invalid amount of money
        if (!ctx.getArguments()[0].matches("\\d+")) {
            Utilities.getUtils().error(ctx.getChannel(), "blackjack", "\uD83C\uDCCF", "Invalid number", "Make sure you only use digits", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
        // If game isn't a test match
        if (!ctx.getArguments()[0].equals("0")) {
            final int win = new Database(ctx.getGuild()).getMembers().getMember(ctx.getMember()).getInteger("balance") + Integer.parseInt(ctx.getArguments()[0]); // Get amount of money you would ge if you win
            // Balance limit would be reached
            if (win > Config.ECONOMY_MAX) {
                Utilities.getUtils().error(ctx.getChannel(), "blackjack", "\uD83C\uDCCF", "lol", "If you play you would have to much money...", ctx.getAuthor().getEffectiveAvatarUrl());
                return;
            }
        }
        // Not enough money
        if (new Database(ctx.getGuild()).getMembers().getMember(ctx.getMember()).getBalance() < Integer.parseInt(ctx.getArguments()[0])) {
            Utilities.getUtils().error(ctx.getChannel(), "blackjack", "\uD83C\uDCCF", "You don't have enough money", "The bank doesn't want to lend you money anymore", ctx.getAuthor().getEffectiveAvatarUrl());
            return;
        }
// Blackjack
        // Get all players
        final Player player = new Player(ctx.getAuthor());
        final Player dealer = new Player(ctx.getEvent().getJDA().getSelfUser());
        // Create a new Game
        final Game game = new Game(Integer.parseInt(ctx.getArguments()[0]), player, dealer);

        // Add new Cards to player
        player.add(game.getRandomCard(), game.getRandomCard());
        // Add new Cards to dealer
        dealer.add(game.getRandomCard(), game.getRandomCard());

        // If player's value is more than 21
        if (player.getValue() > 21) {
            // Switch ace
            player.switchAce();
        }
        // If dealer's value is more than 21
        if (dealer.getValue() > 21) {
            // Switch ace
            dealer.switchAce();
        }
        // Send match message
        ctx.getChannel().sendMessage(getEmbed(player, dealer, game, ctx.getGuild()).build()).queue(message -> {
            final MessageEmbed embed = message.getEmbeds().get(0); // Get send embed

            // Game continues
            if (embed.getFooter().getText().equals("Hit or stay?")) {
                // Add reactions
                message.addReaction("\u23CF").queue(); // Hit
                message.addReaction("\u23F8").queue(); // Stay

                // Guild isn't in the hashmap yet
                if (!games.containsKey(ctx.getGuild().getId())) {
                    games.put(ctx.getGuild().getId(), new HashMap<>()); // Add guild to hashmap
                }
                games.get(ctx.getGuild().getId()).put(message.getId(), game); // Add game to hashmap
            }
        });
    }


    public void reaction(GuildMessageReactionAddEvent event) {
        // Get variables
        final String guildId = event.getGuild().getId(); // Get guild id
        final String messageId = event.getMessageId(); // Get message id

        // Wrong reaction
        if (!games.containsKey(guildId)) return;
        if (!games.get(guildId).containsKey(messageId)) return;

        final Game game = games.get(guildId).get(messageId); // Get game

        // Wrong user reacted to the message
        if (game.getPlayers().get(0).getPlayer().equals(event.getUser()) || game.getPlayers().get(1).getPlayer().equals(event.getUser())) {
            // Get players
            final Player player = game.getPlayers().get(0);
            final Player dealer = game.getPlayers().get(1);
// Hit
            if (event.getReactionEmote().getEmoji().equals("\u23CF")) {
                player.add(game.getRandomCard()); // Add a new card to player

                // If player's value is more than 21
                if (player.getValue() > 21) {
                    player.switchAce(); // Switch ace value
                }

                // Update match message
                event.retrieveMessage().queue(message -> { // Get message
                    message.editMessage(getEmbed(player, dealer, game, event.getGuild()).build()).queue(updateMessage -> {// Update message
                        final MessageEmbed embed = updateMessage.getEmbeds().get(0); // Get embed

                        // gamed continues
                        if (embed.getFooter().getText().equals("Hit or stay?")) {
                            event.getReaction().removeReaction(event.getUser()).queue(); // Remove reaction
                        }
                        // Game ended
                        else {
                            message.clearReactions().queue(); // Clear reactions
                            games.get(guildId).remove(event.getMessageId()); // Remove game
                        }
                    });
                });
            }
//Stay
            else if (event.getReactionEmote().getEmoji().equals("\u23F8")) {
                final GetMember dbMember = new Database(event.getGuild()).getMembers().getMember(event.getMember()); // Get database

                // Add cards to the dealer until his card value is at least 17
                while (dealer.getValue() < 17) {
                    dealer.add(game.getRandomCard()); // Add a random card
                }

                String footer = "";
                final int playerValue = player.getValue(); // Get value of player
                final int dealerValue = dealer.getValue(); // Get value of dealer
// Return credits
                // Player and dealer have the same value and they aren't over 21
                if (playerValue == dealerValue && playerValue <= 21) {
                    footer = "Returned " + game.getBetMoney(); // Set footer
                }
// Won
                // Player has higher value than dealer and player's value is not more than 21
                else if (playerValue > dealerValue && playerValue <= 21) {
                    footer = "You won +" + game.getBetMoney() * 2 + "!"; // Set footer
                    dbMember.setBalance(dbMember.getBalance() + game.getBetMoney()); // Add money
                }
                // Dealer's value is more than 21
                else if (dealerValue > 21) {
                    footer = "You won +" + game.getBetMoney() * 2 + "!"; // Set footer
                    dbMember.setBalance(dbMember.getBalance() + game.getBetMoney()); // Add money
                }
// Lost
                // Dealer has higher value than player and dealer's value is not more than 21
                else if (dealerValue > player.getValue() && dealerValue <= 21) {
                    footer = "The dealer won!"; // Set footer
                    dbMember.setBalance(dbMember.getBalance() - game.getBetMoney()); // Remove money
                }
                // If dealer and player have the same value
                else if (playerValue == dealerValue) {
                    footer = "The dealer won!"; // Set footer
                    dbMember.setBalance(dbMember.getBalance() - game.getBetMoney()); // Remove money
                }
                // Player's value is more than 21
                else if (playerValue > 21 && dealerValue <= 21) {
                    footer = "The dealer won!"; // Set footer
                    dbMember.setBalance(dbMember.getBalance() - game.getBetMoney()); // Remove money
                }
                // Create match message
                EmbedBuilder match = new EmbedBuilder()
                        .setAuthor("blackjack", null, player.getPlayer().getEffectiveAvatarUrl())
                        .setColor(Utilities.getUtils().getMemberRoleColour(event.getMember()))
                        // Player cards
                        .addField("Your cards: " + playerValue, getPlayerCards(player, event.getJDA()), false)
                        // Dealer cards
                        .addField("Dealer cards: " + dealerValue, getDealerCards(dealer, event.getJDA(), true), false)
                        .setFooter(footer);
                // Update message
                event.getChannel().editMessageById(event.getMessageId(), match.build()).queue();
                event.getChannel().editMessageById(event.getMessageId(), match.build()).queue();

                // Clear reaction
                event.retrieveMessage().queue(message -> { // Retrieve message
                    message.clearReactions().queue(); // Clear reactions
                });
                // Remove game
                games.get(guildId).remove(event.getMessageId());
            }
        }
    }

    /**
     * @param player The player.
     * @param dealer The dealer.
     * @param game   The game.
     * @param guild  The guild.
     * @return Returns an embed for the match.
     */
    private EmbedBuilder getEmbed(Player player, Player dealer, Game game, Guild guild) {
        // Create embed
        EmbedBuilder match = new EmbedBuilder()
                .setAuthor("blackjack", null, player.getPlayer().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().getMemberRoleColour(guild.getMember(player.getPlayer())))
                // Player cards
                .addField("Your cards: " + player.getValue(), getPlayerCards(player, guild.getJDA()), false);
        // Get member in database
        final GetMember dbMember = new Database(guild).getMembers().getMember(guild.getMember(player.getPlayer()));
// Lost
        // Dealer and player have a value of 21
        if (dealer.getValue() == player.getValue() && dealer.getValue() == 21) {
            // Remove balance
            dbMember.setBalance(dbMember.getBalance() - game.getBetMoney());
            match
                    .addField("Dealer cards: " + dealer.getValue(), getDealerCards(dealer, guild.getJDA(), true), false)
                    .setFooter("The dealer won!");
        }
        // Dealer's value is 21
        else if (dealer.getValue() == 21) {
            // Remove balance
            dbMember.setBalance(dbMember.getBalance() - game.getBetMoney());
            match
                    .addField("Dealer cards: " + dealer.getValue(), getDealerCards(dealer, guild.getJDA(), true), false)
                    .setFooter("The dealer won!");
        }
        // If player's value is more than 21
        else if (player.getValue() > 21) {
            // Remove balance
            dbMember.setBalance(dbMember.getBalance() - game.getBetMoney());
            match
                    .addField("Dealer cards: " + dealer.getValue(), getDealerCards(dealer, guild.getJDA(), true), false)
                    .setFooter("The dealer won!");
        }
// Won
        // Player's value is 21
        else if (player.getValue() == 21) {
            // Add balance
            dbMember.setBalance(dbMember.getBalance() + game.getBetMoney() * 2);
            match
                    .addField("Dealer cards: " + dealer.getValue(), getDealerCards(dealer, guild.getJDA(), true), false)
                    .setFooter("You won! +" + game.getBetMoney() * 2);
        }
        // Continue game
        else {
            match
                    .addField("Dealer shows:", getDealerCards(dealer, guild.getJDA(), false), false)
                    .setFooter("Hit or stay?");
        }
        return match;
    }

    /**
     * @param player The player.
     * @param jda    The jda entity.
     * @return Returns a String with the cards of the player as emotes.
     */
    private String getPlayerCards(Player player, JDA jda) {
        // Get cards of player as emotes
        String playerCards = "";
        for (Card playerCard : player.getCards()) {
            playerCards += playerCard.getEmote(jda) + " ";
        }
        return playerCards;
    }

    /**
     * @param dealer The dealer.
     * @param jda    The jda entity.
     * @return Returns a String with the cards of the dealer as emotes.
     */
    private String getDealerCards(Player dealer, JDA jda, boolean showsAll) {
        // Get cards of dealer as emotes
        String dealerCards = "";
        for (Card dealerCard : dealer.getCards()) {
            if (dealer.getCards().get(0).equals(dealerCard) && !showsAll) {
                dealerCards += jda.getGuildById("776389239293607956").getEmotesByName("CardBlank", true).get(0).getAsMention() + " ";
            } else
                dealerCards += dealerCard.getEmote(jda) + " ";

        }
        return dealerCards;
    }
}
