package com.myra.dev.marian.commands.fun;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.APIs.Reddit;
import com.myra.dev.marian.utilities.Utilities;

@CommandSubscribe(
        command = "meme",
        name = "meme",
        aliases = {"memes"}
)
public class Meme implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        try {
            ctx.getChannel().sendMessage(new Reddit().getMeme(ctx.getAuthor()).build()).queue();
        } catch (Exception e) {
            Utilities.getUtils().error(ctx.getChannel(), "meme", "\uD83E\uDD2A", "Couldn't load meme", "Please try again later", ctx.getAuthor().getEffectiveAvatarUrl());
        }
    }
}