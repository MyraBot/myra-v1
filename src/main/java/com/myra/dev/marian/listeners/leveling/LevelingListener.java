package com.myra.dev.marian.listeners.leveling;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.allMethods.GetMember;
import com.myra.dev.marian.management.listeners.Listener;
import com.myra.dev.marian.management.listeners.ListenerContext;
import com.myra.dev.marian.management.listeners.ListenerSubscribe;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.time.Duration;
import java.util.HashMap;
import java.util.Random;

@ListenerSubscribe(
        name = "leveling"
)
public class LevelingListener implements Listener {
    private final Leveling LEVELING = new Leveling();

    @Override
    public void execute(ListenerContext ctx) throws Exception {
        if (ctx.getEvent().getAuthor().isBot()) return; // Check if member is a bot

        final GetMember db = new Database(ctx.getGuild()).getMembers().getMember(ctx.getEvent().getMember()); // Get member from database

        // Update message count
        final Integer messages = db.getInteger("messages"); // Get current messages
        db.setInteger("messages", messages + 1); // Add 1 message

        if (ctx.getMessage().getContentRaw().startsWith(new Database(ctx.getGuild()).getString("prefix")))
            return; // Message is a command
        if (!cooldown(ctx)) return; // Cooldown

        LEVELING.levelUp(ctx.getEvent().getMember(), ctx.getChannel(), db, getXpFromMessage(ctx.getMessage())); // Check for new level
        db.setInteger("xp", db.getInteger("xp") + getXpFromMessage(ctx.getMessage())); // Update xp
    }

    private static HashMap<Guild, HashMap<Member, Message>> cooldown = new HashMap<Guild, HashMap<Member, Message>>();

    private boolean cooldown(ListenerContext event) {
        boolean returnedValue = true;
        //check if guild isn't in the HashMap yet
        if (cooldown.get(event.getGuild()) == null) {
            //crate new Map for member
            HashMap<Member, Message> memberMap = new HashMap<>();
            memberMap.put(event.getMessage().getMember(), event.getMessage());
            //put guild in the 'cooldown' Map
            cooldown.put(event.getGuild(), memberMap);
        }
        //check if user isn't in the HashMap yet
        else if (cooldown.get(event.getGuild()).get(event.getMessage().getMember()) == null) {
            cooldown.get(event.getGuild()).put(event.getMessage().getMember(), event.getMessage());
        }
        //check if 1 minutes passed
        else {
            if (Duration.between(event.getMessage().getTimeCreated(), cooldown.get(event.getGuild()).get(event.getMessage().getMember()).getTimeCreated()).toMinutes() < 1) {
                cooldown.get(event.getGuild()).replace(event.getMessage().getMember(), event.getMessage());
                returnedValue = false;
            }
        }
        return returnedValue;
    }

    //return xp
    public int getXpFromMessage(Message rawMessage) {
        //return 0 if the author is a bot
        if (rawMessage.getAuthor().isBot()) return 0;
        //define variable
        String stringMessage = rawMessage.getContentDisplay();
        //return '1' or '2' random
        Random random = new Random();
        int oneOrTwo = random.nextInt(3 - 1) + 1;
        //remove quoted message
        if (stringMessage.startsWith("> ") && stringMessage.contains("\n")) {
            //split message into paragraphs
            String[] paragraphs = stringMessage.split("\n");
            //remove all paragraphs, which aren't quotes
            for (String paragraph : paragraphs) {
                if (paragraph.startsWith("> ")) {
                    stringMessage = stringMessage.replace(paragraph, "");
                }
            }
        }
        //if contains link
        String[] eachWord = rawMessage.getContentRaw().split("\\s+");
        for (String word : eachWord) {
            //remove all links
            if (word.startsWith("http") || word.startsWith("www")) {
                stringMessage = stringMessage.replace(word, "");
            }
        }
        //convert message to character array
        char[] msg = stringMessage.toCharArray();
        //calculate the xp for the message
        return msg.length / 20 + oneOrTwo;
    }
}
