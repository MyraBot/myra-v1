package com.myra.dev.marian.commands.music.commands;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        command = "leave",
        name = "leave",
        aliases = {"disconnect"}
)
public class MusicLeave implements Command {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Get utilities
        Utilities utilities = Utilities.getUtils();
// Errors
        // Not connected to a voice channel
        if (!ctx.getGuild().getAudioManager().isConnected()) {
            new Error(ctx.getEvent())
                    .setCommand("leave")
                    .setEmoji("\uD83D\uDCE4")
                    .setMessage("I'm not connected to a voice channel")
                    .send();
            return;
        }
        // If author isn't in a voice channel yet
        if (!ctx.getEvent().getMember().getVoiceState().inVoiceChannel()) {
            new Error(ctx.getEvent())
                    .setCommand("leave")
                    .setEmoji("\uD83D\uDCE4")
                    .setMessage("You need to join a voice channel first to use this command")
                    .send();
            return;
        }
        // Author isn't in the same voice channel as the bot
        if (!ctx.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(ctx.getEvent().getMember())) {
            new Error(ctx.getEvent())
                    .setCommand("leave")
                    .setEmoji("\uD83D\uDCE4")
                    .setMessage("You have to be in the same voice channel as me to use this command")
                    .send();
            return;
        }
// Leave voice channel
        // Leave from current channel
        ctx.getGuild().getAudioManager().closeAudioConnection();
        // Send success message
        EmbedBuilder success = new EmbedBuilder()
                .setAuthor("leave", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(utilities.blue)
                .setDescription("Left voice channel: **" + ctx.getEvent().getMember().getVoiceState().getChannel().getName() + "**");
        ctx.getChannel().sendMessage(success.build()).queue();
    }
}
