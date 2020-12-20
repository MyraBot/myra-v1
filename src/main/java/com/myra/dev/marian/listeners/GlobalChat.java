package com.myra.dev.marian.listeners;

import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.management.listeners.Listener;
import com.myra.dev.marian.management.listeners.ListenerContext;
import com.myra.dev.marian.management.listeners.ListenerSubscribe;
import com.myra.dev.marian.utilities.Webhook;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;

import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

@ListenerSubscribe(
        name = "global chat",
        premium = true
)
public class GlobalChat implements Listener {
    @Override
    public void execute(ListenerContext ctx) throws Exception {
        final String guildWebhook = new Database(ctx.getGuild()).getString("globalChat"); // Get global chat webhook url
        if (guildWebhook == null) return;

        ctx.getChannel().retrieveWebhooks().queue(webhooks -> {
            if (webhooks.stream().noneMatch(webhook -> webhook.getUrl().equals(guildWebhook))) return;

            for (Document document : MongoDb.getInstance().getCollection("guilds").find(eq("premium", true))) {
                final String url = document.getString("globalChat"); // Get global chat webhook url
                if (url == null) continue; // No global chat set

                final Guild guild = ctx.getEvent().getJDA().getGuildById(document.getString("guildId")); // Get guild
                if (ctx.getGuild() == guild) continue; // Same guild as author guild

                guild.retrieveWebhooks().queue(guildWebhooks -> guildWebhooks.forEach(webhook -> {
                    if (webhook.getUrl().equals(url)) {
                        final User user = ctx.getEvent().getAuthor(); // Get author
                        try {
                            // Create webhook message
                            Webhook message = new Webhook(url);
                            message.setUsername(user.getName());
                            message.setAvatarUrl(user.getEffectiveAvatarUrl());

                            // Message is a reply
                            if (ctx.getMessage().getReferencedMessage() != null) {
                                final Message reply = ctx.getMessage().getReferencedMessage(); // Get message to reply

                                final String replyingUser = reply.getAuthor().getName();
                                if (!reply.getMentionedUsers().isEmpty())
                                    reply.getMentionedUsers().get(0).getAsMention().toString();

                                message.setContent("> " + reply.getContentRaw() + "\\n " + replyingUser + " "); // In JSON \n is \\n
                            }
                                message.appendContent(ctx.getMessage().getContentRaw());
                            // Message has an attachments
                            if (!ctx.getMessage().getAttachments().isEmpty()) {
                                Webhook.EmbedObject attachment = new Webhook.EmbedObject()
                                        .setImage(ctx.getMessage().getAttachments().get(0).getUrl());
                                message.addEmbed(attachment);
                            }
                            message.execute(); // Send message
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }));
            }
        });
    }
}
