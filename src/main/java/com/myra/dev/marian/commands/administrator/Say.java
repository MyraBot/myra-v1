package com.myra.dev.marian.commands.administrator;


import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "say",
        aliases = {"write"},
        requires = Permissions.ADMINISTRATOR
)
public class Say implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        //command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("say", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "say <message>`", "\uD83D\uDCAC │ Let the bot say something", true);
            ctx.getChannel().sendMessage(embed.build()).queue();
            return;
        }
// write message
        //get arguments
        String message = "";
        for (int i = 0; i < ctx.getArguments().length; i++) {
            message += ctx.getArguments()[i] + " ";
        }
        //remove last space
        message = message.substring(0, message.length() - 1);
        //delete command
        ctx.getEvent().getMessage().delete().queue();
        //send message
        ctx.getChannel().sendMessage(message).queue();
    }
}