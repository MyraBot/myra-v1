package com.myra.dev.marian.management.commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.myra.dev.marian.Bot;
import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.Permissions;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;


/**
 * Default implementation of the {@link CommandService}.
 */
public class DefaultCommandService implements CommandService {
    private final Map<Command, CommandSubscribe> commands;

    public DefaultCommandService() {
        this.commands = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(Command command) {
        if (this.isSubscribed(command.getClass()) && this.hasCommand(command.getClass())) {
            this.commands.put(command, command.getClass().getAnnotation(CommandSubscribe.class));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(Command... commands) {
        for (Command command : commands) {
            this.register(command);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregister(Command command) {
        this.commands.remove(command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterAll() {
        this.commands.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Command, CommandSubscribe> getCommands() {
        return this.commands;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processCommandExecution(GuildMessageReceivedEvent event, EventWaiter waiter) throws Exception {
        //get prefix
        final String prefix = new Database(event.getGuild()).getString("prefix");
        // If message doesn't start with prefix
        if (!event.getMessage().getContentRaw().startsWith(prefix)) return;
        // Get message without prefix
        String rawMessage = event.getMessage().getContentRaw().substring(prefix.length());
        // Split rawMessage
        String[] splitMessage = rawMessage.split("\\s+");

        for (Map.Entry<Command, CommandSubscribe> entry : this.commands.entrySet()) {
            // Create list for keywords
            List<String[]> executors = new ArrayList<>();
            // Add name of command
            executors.add(entry.getValue().name()
                    .replace("BOT_NAME", event.getJDA().getSelfUser().getName())
                    .replace("GUILD_NAME", event.getGuild().getName())
                    .split("\\s+")
            );
            //check if there are no aliases
            if (!entry.getValue().aliases()[0].equals("")) {
                //Add aliases of command
                for (String alias : entry.getValue().aliases()) {
                    String[] splitAlias = alias
                            .replace("BOT_NAME", event.getJDA().getSelfUser().getName())
                            .replace("GUILD_NAME", event.getGuild().getName())
                            .split("\\s+");
                    executors.add(splitAlias);
                }
            }
            //check for every executor
            for (String[] executor : executors) {
                // Check if executor is longer than message
                if (executor.length > splitMessage.length) continue;
                //check for every argument
                boolean Continue = false;
                for (int i = 0; i < executor.length; i++) {
                    //if one argument doesn't match the executor
                    if (!splitMessage[i].equalsIgnoreCase(executor[i])) {
                        Continue = true;
                        break;
                    }
                }
                // Continue if not every argument matches the executor
                if (Continue) continue;
// Run command
                if (!hasPermissions(event.getMember(), entry.getValue().requires()))
                    return; // Check for required permissions
                if (isDisabled(entry.getValue().command(), event.getGuild())) return; // Check if command is disabled
                //filter arguments
                // String[] commandArguments = Arrays.copyOfRange(splitMessage, executor.length, splitMessage.length);
                String commandArguments = rawMessage.substring(Utilities.getUtils().getString(executor).length()); // Remove command name and 1 more character, which is a space
                if (commandArguments.startsWith(" ")) {
                    commandArguments = commandArguments.substring(1);
                }
                //run command
                entry.getKey().execute(new CommandContext(prefix, event, commandArguments, waiter));
            }
        }
    }

    /**
     * Check if a command is disabled.
     *
     * @param command The command to check.
     * @param guild   The guild the command was executed.
     * @return Returns a Boolean value of isDisabled.
     */
    private boolean isDisabled(String command, Guild guild) {
        // If command isn't in the database
        if (command.equals("")) return false;
        // Get listener document
        Document commands = (Document) MongoDb.getInstance().getCollection("guilds").find(eq("guildId", guild.getId())).first().get("commands");
        // Return value of command
        if (commands.getBoolean(command) == null) return false; // Command doesn't exist
        return !commands.getBoolean(command); // Return inverted value of command
    }

    /**
     * Check if the Class has the CommandSubscribe annotation.
     *
     * @param cls The class of the command, which should be executed.
     * @return Returns if the Command contains the annotation.
     */
    private boolean isSubscribed(Class<?> cls) {
        return cls.isAnnotationPresent(CommandSubscribe.class);
    }

    /**
     * Check if the Class implements the Command Interface.
     *
     * @param cls The class of the command.
     * @return Returns if the Command implements the Interface.
     */
    private boolean hasCommand(Class<?> cls) {
        for (Class<?> implement : cls.getInterfaces()) {
            if (implement == Command.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a member is allowed to execute the command.
     *
     * @param member             The author, who executed the command.
     * @param requiresPermission The permission the member needs to execute the command.
     * @return Returns if the member can execute the command.
     */
    private boolean hasPermissions(Member member, Permissions requiresPermission) {
        if (requiresPermission == Permissions.MARIAN) {
            return member.getId().equals(Config.marian);
        } else if (requiresPermission == Permissions.SERVEROWNER) {
            return member.isOwner();
        } else if (requiresPermission == Permissions.ADMINISTRATOR) {
            return member.hasPermission(Permission.ADMINISTRATOR);
        } else if (requiresPermission == Permissions.MODERATOR) {
            return member.hasPermission(Permission.VIEW_AUDIT_LOGS);
        } else if (requiresPermission == Permissions.MEMBER) {
            return true;
        }
        return false;
    }
}