package com.myra.dev.marian.utilities.EmbedMessage;

import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandUsage {
    private String command;
    private String avatar;
    private List<Usage> usages = new ArrayList<>();

    public CommandUsage setCommand(String command) {
        this.command = command;
        return this;
    }

    public CommandUsage setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public CommandUsage addCommand(Usage... usages) {
        this.usages.addAll(Arrays.asList(usages));
        return this;
    }


    public void send(TextChannel channel) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(this.command, null, this.avatar)
                .setColor(Utilities.getUtils().blue);
        // Add all commands usages
        usages.forEach(usage -> {
            embed.addField(
                    String.format("`%s`", usage.getUsage()),
                    String.format("%s │ %s", usage.getEmoji(), usage.getDescription()),
                    false);
        });
        // Send command usage
        channel.sendMessage(embed.build()).queue();
    }
}
