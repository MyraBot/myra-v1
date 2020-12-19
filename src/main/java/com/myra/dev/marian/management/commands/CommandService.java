package com.myra.dev.marian.management.commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Map;

/**
 * Represents a service to register or unregister commands.
 */
public interface CommandService {

    /**
     * Registers the given {@link Command}.
     *
     * @param command The command to register.
     */
    void register(Command command);

    /**
     * Registers the given {@link Command} array.
     *
     * @param commands The commands to register.
     */
    void register(Command... commands);

    /**
     * Unregisters the given {@link Command} from the service.
     *
     * @param command The command to unregister.
     */
    void unregister(Command command);

    /**
     * Unregisters all registered commands.
     */
    void unregisterAll();

    /**
     * Retrieves a key-value system with all registered {@link Command}'s and their subscribers.
     *
     * @return A key-value system with all registered commands.
     */
    Map<Command, CommandSubscribe> getCommands();

    /**
     * Processes the execution of the registered commands.
     *
     * @param event The event method.
     */
    void processCommandExecution(GuildMessageReceivedEvent event, EventWaiter waiter) throws Exception;
}