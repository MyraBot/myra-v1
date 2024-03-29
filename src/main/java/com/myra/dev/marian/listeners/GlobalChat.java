package com.myra.dev.marian.listeners;

import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.listeners.Listener;
import com.myra.dev.marian.management.listeners.ListenerContext;
import com.myra.dev.marian.management.listeners.ListenerSubscribe;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.Webhook;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import okhttp3.Request;
import okhttp3.Response;
import org.bson.Document;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.not;

@ListenerSubscribe(
        name = "global chat"
)
public class GlobalChat implements Listener {

    private final static List<Message> messages = new ArrayList<>();

    @Override
    public void execute(ListenerContext ctx) throws Exception {
        final String guildWebhookUrl = new Database(ctx.getGuild()).getString("globalChat"); // Get global chat webhook url
        if (guildWebhookUrl == null) return;

        final WebhookInformation guildWebhookInformation = new WebhookInformation(guildWebhookUrl); // Get webhook information
        if (!guildWebhookInformation.getChannelId().equals(ctx.getChannel().getId())) return; // Wrong channel

        if (messages.size() >= 15) messages.remove(0); // Remove first stored message
        messages.add(ctx.getMessage()); // Add new message

        for (Document document : MongoDb.getInstance().getCollection("guilds").find(not(eq("globalChat", null)))) {
            final String url = document.getString("globalChat"); // Get global chat webhook url
            if (url == null) continue; // No global chat set

            final Guild guild = ctx.getEvent().getJDA().getGuildById(document.getString("guildId")); // Get guild
            if (ctx.getGuild() == guild) continue; // Same guild as author guild

            final User user = ctx.getEvent().getAuthor(); // Get author
            // Create webhook message
            Webhook message = new Webhook(url);
            message.setUsername(user.getName());
            message.setAvatarUrl(user.getEffectiveAvatarUrl());

            // Message is a reply
            if (ctx.getMessage().getReferencedMessage() != null) {
                final Message reply = ctx.getMessage().getReferencedMessage(); // Get message to reply
                message.setContent("> " + reply.getContentRaw() + "\n"); // In JSON \n is \\n
            }
            message.appendContent(ctx.getMessage().getContentRaw()); // Add message
            // Message has an attachments
            if (!ctx.getMessage().getAttachments().isEmpty()) {
                message.addAttachment(ctx.getMessage().getAttachments().get(0));
            }
            message.send(); // Send message
        }
    }

    public void messageEdited(GuildMessageUpdateEvent event) throws IOException {
        final String guildWebhookUrl = new Database(event.getGuild()).getString("globalChat"); // Get global chat webhook url
        if (guildWebhookUrl == null) return;

        final WebhookInformation guildWebhookInformation = new WebhookInformation(guildWebhookUrl); // Get webhook information
        if (!guildWebhookInformation.getChannelId().equals(event.getChannel().getId())) return; // Wrong channel

        // Message is still in range to edit
        if (messages.contains(event.getMessage())) {
            for (Document document : MongoDb.getInstance().getCollection("guilds").find(not(eq("globalChat", null)))) {
                final String url = document.getString("globalChat"); // Get global chat webhook url
                if (url == null) continue; // No global chat set

                final Guild guild = event.getJDA().getGuildById(document.getString("guildId")); // Get guild
                if (event.getGuild() == guild) continue; // Same guild as author guild

                final WebhookInformation webhookInformation = new WebhookInformation(url); // Get webhook information
                final TextChannel channel = guild.getTextChannelById(webhookInformation.getChannelId()); // Get global chat

                channel.getHistory().retrievePast(15).queue(history -> { // Retrieve last 15 messages
                    history.forEach(historyMessage -> { // Check every message
                        messages.forEach(originalMessage -> {
                            if (historyMessage.getContentRaw().equals(originalMessage.getContentRaw()) && historyMessage.getAuthor().getName().equals(originalMessage.getAuthor().getName())) {
                                Webhook webhook = new Webhook(url);
                                webhook.setContent(event.getMessage().getContentRaw());
                                try {
                                    webhook.edit(historyMessage.getId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    });
                });
            }
        }
    }

    private static class WebhookInformation {
        private final String url;
        private final JSONObject response;

        private WebhookInformation(String url) throws IOException {
            this.url = url;

            final Request webhookRequest = new Request.Builder()
                    .url(url) // Get information about the webhook
                    .build();
            try (Response response = Utilities.HTTP_CLIENT.newCall(webhookRequest).execute()) {
                this.response = new JSONObject(response.body().string()); // Get information as a json object
            }
        }

        private String getUrl() {
            return url;
        }

        private String getGuildId() {
            return response.getString("guild_id");
        }

        private String getChannelId() {
            return response.getString("channel_id");
        }
    }
}
