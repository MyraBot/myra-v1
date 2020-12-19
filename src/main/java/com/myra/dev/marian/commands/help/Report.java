package com.myra.dev.marian.commands.help;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.Webhook;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "report",
        aliases = {"bug"}
)
public class Report implements Command {
    private final String webhookUrl = "https://discord.com/api/v6/webhooks/788764863106252800/ZN7j5NCIEtekAxyKXJ55BUp8UqLmvsUuGAh2-Dlsndul0ziuxxyxpGiDtVBmsLd_beBF";

    @Override
    public void execute(final CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("report", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "report <bug>`", "\uD83D\uDC1B │ Report a bug you found", false)
                    .setFooter("You can also add attachments");
            ctx.getChannel().sendMessage(usage.build()).queue(); // Send usage
            return;
        }

        // Bug report
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
        report.execute(); // Send report as a webhook

        Utilities.getUtils().success(ctx.getChannel(), "report", "\uD83D\uDC1B", "Successfully reported your bug", "Your bug report was successfully reported", ctx.getAuthor().getEffectiveAvatarUrl(), false, null);
    }
}
