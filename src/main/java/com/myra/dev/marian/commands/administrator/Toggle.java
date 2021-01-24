package com.myra.dev.marian.commands.administrator;

import com.myra.dev.marian.commands.help.Help;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.Manager;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;
import java.util.Map;

@CommandSubscribe(
        name = "toggle",
        requires = Permissions.ADMINISTRATOR

)
public class Toggle implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        //command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("toggle", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "toggle <command>`", "\uD83D\uDD11 │ Toggle commands on and off", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Toggle commands on or off
        //get command without prefix
        String command;
        if (ctx.getArguments()[0].startsWith(ctx.getPrefix())) {
            command = ctx.getArguments()[0].substring(ctx.getPrefix().length());
        } else command = ctx.getArguments()[0];
        // Go throw every command
        // If a alias matches the given command
        for (Map.Entry<Command, CommandSubscribe> entry : Manager.getCommands().entrySet())
            if (Arrays.stream(entry.getValue().aliases()).anyMatch(command::equalsIgnoreCase) || command.equalsIgnoreCase(entry.getValue().name())) {
                // Command is a help command
                if (entry.getKey().getClass().getPackage().equals(Help.class.getPackage())) {
                    new Error(ctx.getEvent())
                            .setCommand("toggle")
                            .setEmoji("\uD83D\uDD11")
                            .setMessage("You can't toggle `help` commands")
                            .send();
                    return;
                }
                Database db = new Database(ctx.getGuild());
                // Get command name
                command = entry.getValue().command();
                // Get new value of command
                boolean newValue = !db.getNested("commands").get(command, Boolean.class);
                // Update database
                db.getNested("commands").set(command, newValue, Manager.type.BOOLEAN);
                // Success information
                Success success = new Success(ctx.getEvent())
                        .setCommand("toggle")
                        .setEmoji("\uD83D\uDD11")
                        .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
                if (newValue) success.setMessage("Members can now use the command `" + command + "` again");
                else success.setMessage("From now on members can no longer use the command `" + command + "`");
                return;
            }
        // Command doesn't exist
        new Error(ctx.getEvent())
                .setCommand("toggle")
                .setEmoji("\uD83D\uDD11")
                .setMessage("The command doesn't exist")
                .send();
        return;
    }
}
