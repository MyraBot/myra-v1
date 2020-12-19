package com.myra.dev.marian.listeners;

import com.myra.dev.marian.management.listeners.Listener;
import com.myra.dev.marian.management.listeners.ListenerContext;
import com.myra.dev.marian.management.listeners.ListenerSubscribe;
import com.myra.dev.marian.utilities.Permissions;

import java.util.Random;

@ListenerSubscribe(
        name = "@someone",
        needsExecutor = true,
        requires = Permissions.ADMINISTRATOR
)
public class Someone implements Listener {
    @Override
    public void execute(ListenerContext ctx) throws Exception {
        //get random number
        Random random = new Random();
        int number = random.nextInt(ctx.getGuild().getMembers().size());
        //get random member
        String randomMember = ctx.getGuild().getMembers().get(number).getAsMention();

        String message = ctx.getMessage().getContentRaw().replace("@someone", randomMember);
        ctx.getChannel().deleteMessageById(ctx.getChannel().getLatestMessageIdLong()).queue();
        ctx.getChannel().sendMessage(message).queue();
    }
}
