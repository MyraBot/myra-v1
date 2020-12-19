package com.myra.dev.marian.listeners.welcome.welcomeDirectMessage;


import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "welcome direct message",
        aliases = {"welcome dm"},
        requires = Permissions.ADMINISTRATOR
)
public class WelcomeDirectMessageHelp implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Usage
        EmbedBuilder welcomeDirectMessage = new EmbedBuilder()
                .setAuthor("welcome direct message", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .addField("`" + ctx.getPrefix() + "welcome direct message toggle`", "\uD83D\uDD11 │ Toggle welcome images on and off", false)
                .addField("`" + ctx.getPrefix() + "welcome direct message message <message>`", "\uD83D\uDCAC │ change the text of the direct messages", false);
        ctx.getChannel().sendMessage(welcomeDirectMessage.build()).queue();
    }
}
