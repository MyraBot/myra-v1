package com.myra.dev.marian.commands.moderation;


import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "clear",
        aliases = {"purge", "delete"},
        requires = Permissions.MODERATOR
)
public class Clear implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // If amount isn't a number
        if (!ctx.getArguments()[0].matches("\\d+")) return;
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("clear", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "clear <amount>`", "\uD83D\uDDD1 │ clear", true);
            ctx.getChannel().sendMessage(embed.build()).queue();
            return;
        }
// Clear messages
        // Delete messages
        try {
            // Retrieve messages
            ctx.getChannel().getHistory().retrievePast(Integer.parseInt(ctx.getArguments()[0]) + 1).queue(messages -> {
                ctx.getChannel().deleteMessages(messages).queue(); // Delete messages
            });
            // Success information
            utilities.success(ctx.getChannel(), "clear", "\uD83D\uDDD1", "the message were deleted successfully", "`" + ctx.getArguments()[0] + "` messages have been deleted", ctx.getEvent().getJDA().getSelfUser().getEffectiveAvatarUrl(), true, null);
        }
        // Errors
        catch (Exception exception) {
            //to many messages
            if (ctx.getArguments()[0].equals("0") || exception.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval")) {
                utilities.error(ctx.getChannel(), "clear", "\uD83D\uDDD1", "Invalid amount of messages", "An amount between 1 and 100 messages can be deleted", ctx.getAuthor().getEffectiveAvatarUrl());
            }
            //message too late
            else {
                utilities.error(ctx.getChannel(), "clear", "\uD83D\uDDD1", "You selected too old messages", "I can't delete messages older than 2 weeks", ctx.getAuthor().getEffectiveAvatarUrl());
            }
        }
    }
}
