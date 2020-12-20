package com.myra.dev.marian.commands.administrator;

import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

@CommandSubscribe(
        name = "global chat",
        requires = Permissions.ADMINISTRATOR
)
public class GlobalChatChannel implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("global chat", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "global chat <channel>`", "\uD83C\uDF10 │ Communicate with other servers", false);
            ctx.getChannel().sendMessage(usage.build()).queue(); // Send usage
            return;
        }

        final TextChannel channel = Utilities.getUtils().getTextChannel(ctx.getEvent(), ctx.getArguments()[0], "global chat", "\uD83C\uDF10"); // Get text channel
        if (channel == null) return;

        final String webhookUrl = new Database(ctx.getGuild()).getString("globalChat"); // Get current webhook url

        // Create global chat
        if (webhookUrl == null) {
           createWebhook(channel);

            Utilities.getUtils().success(ctx.getChannel(), "global chat", "\uD83C\uDF10", "Added a global chat", "Set the global chat to " + channel.getAsMention(), ctx.getAuthor().getEffectiveAvatarUrl(), false, null);
            Utilities.getUtils().success(channel, "global chat", "\uD83C\uDF10", "Added a global chat", "Here you will receive messages from other servers", ctx.getAuthor().getEffectiveAvatarUrl(), false, null);
        }

        // Change global chat
        if (webhookUrl != null) {
            ctx.getGuild().retrieveWebhooks().queue(webhooks -> webhooks.forEach(webhook -> {
                if (!webhook.getUrl().equals(webhookUrl)) return;

                // Remove global chat
                if (webhook.getChannel() == channel) {
                    new Database(ctx.getGuild()).set("globalChat", null); // Update database
                    webhook.delete().queue(); // Delete webhook
                    Utilities.getUtils().success(ctx.getChannel(), "global chat", "\uD83C\uDF10", "Removed global chat", "The global chat has been removed", ctx.getAuthor().getEffectiveAvatarUrl(), false, null);
                }

                // Change global chat
                else {
                    webhook.delete().queue(); // Delete webhook
                    createWebhook(channel); // Create webhook
                    Utilities.getUtils().success(ctx.getChannel(), "global chat", "\uD83C\uDF10", "Changed global chat", "Global chat is now set to " + channel.getAsMention(), ctx.getAuthor().getEffectiveAvatarUrl(), false, null);
                    Utilities.getUtils().success(channel, "global chat", "\uD83C\uDF10", "Changed global chat", "Here you will receive messages from other servers", ctx.getAuthor().getEffectiveAvatarUrl(), false, null);
                }
            }));
        }
    }

    private void createWebhook(TextChannel channel) {
        channel.createWebhook("global chat").queue(webhook -> {
            final String url = webhook.getUrl(); // Get webhook url
            new Database(channel.getGuild()).set("globalChat", url); // Update database
        });
    }
}
