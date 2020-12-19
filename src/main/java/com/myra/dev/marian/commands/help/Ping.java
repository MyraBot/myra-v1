package com.myra.dev.marian.commands.help;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "ping",
        aliases = {"latency"}
)
public class Ping implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (!ctx.getAuthor().isBot()) {
            EmbedBuilder ping = new EmbedBuilder()
                    .setAuthor("pong", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().blue)
                    .addField("\uD83C\uDFD3 │ latency", "My ping is `" + ctx.getEvent().getJDA().getGatewayPing() + "` ms", true);
            ctx.getChannel().sendMessage(ping.build()).queue();
        }
    }
}
