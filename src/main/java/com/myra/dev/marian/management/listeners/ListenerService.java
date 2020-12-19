package com.myra.dev.marian.management.listeners;

import com.myra.dev.marian.management.commands.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Map;

public interface ListenerService {

    /**
     * Registers the given {@link Command}.
     *
     * @param command The command to register.
     */
    void register(Listener listener);

    /**
     * Registers the given {@link Command} array.
     *
     * @param commands The commands to register.
     */
    void register(Listener... listeners);

    /**
     * Unregisters the given {@link Command} from the service.
     *
     * @param command The command to unregister.
     */
    void unregister(Listener listener);

    /**
     * Unregisters all registered commands.
     */
    void unregisterAll();

    /**
     * Retrieves a key-value system with all registered {@link Command}'s and their subscribers.
     *
     * @return A key-value system with all registered commands.
     */
    Map<Listener, ListenerSubscribe> getListeners();

    /**
     * Processes the execution of the registered commands.
     *
     * @param event The event method.
     */
    void processCommandExecution(GuildMessageReceivedEvent event) throws Exception;
}
