package com.myra.dev.marian.commands.help;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.Webhook;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "feature",
        aliases = {"submit"}
)
public class Feature implements Command {
    private final String webhookUrl = "https://discord.com/api/v6/webhooks/788769270384558120/A_6jJ1gstVcqih6lD8pTIAereQBhTJRn9vtbljqevVQ4uiOXAEXPTWZBh6n99ZJJrwPd";

    @Override
    public void execute(final CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("feature", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "feature <feature description>`", "\uD83D\uDCCC │ Submit a feature", false)
                    .setFooter("You can also add attachments");
            ctx.getChannel().sendMessage(usage.build()).queue(); // Send usage
            return;
        }

        // Feature submit
        final Webhook report = new Webhook(webhookUrl); // Set webhook
        report.setUsername(ctx.getAuthor().getName()); // Set webhook name
        report.setAvatarUrl(ctx.getAuthor().getEffectiveAvatarUrl()); // Set webhook profile picture

        Webhook.EmbedObject bug = new Webhook.EmbedObject() // Create JSON embed
                .setDescription(ctx.getArgumentsRaw()); // Add bug description to JSON embed

        // Attachment is given
        if (!ctx.getEvent().getMessage().getAttachments().isEmpty()) {
            bug.setImage(ctx.getEvent().getMessage().getAttachments().get(0).getUrl()); // Add image to JSON embed
        }

        report.addEmbed(bug); // Add the JSON embed to webhook
        report.send(); // Send feature submit as a webhook

        ctx.getEvent().getJDA().getGuildById(Config.marianServer).retrieveWebhooks().queue(webhooks -> webhooks.forEach(webhook -> { // Go through every webhook

            if (webhook.getUrl().equals(webhookUrl)) { // Webhook is the feature submit webhook
                final String messageId = webhook.getChannel().getLatestMessageId(); // Get latest message id
                webhook.getChannel().retrieveMessageById(messageId).queue(message -> { // Retrieve feature suggestion
                    // Add reactions
                    message.addReaction("\uD83D\uDC4D").queue(); // 👍
                    message.addReaction("\uD83D\uDC4E").queue(); // 👎
                });
            }

        }));

        // Success information
        Success success = new Success(ctx.getEvent())
                .setCommand("feature")
                .setEmoji("\uD83D\uDCCC")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage("Your feature request was successfully submitted");
        success.send();
    }
}
