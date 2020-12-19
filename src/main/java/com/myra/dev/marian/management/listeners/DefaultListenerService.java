package com.myra.dev.marian.management.listeners;

import com.myra.dev.marian.Bot;
import com.myra.dev.marian.management.commands.CommandService;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.Permissions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of the {@link CommandService}.
 */
public class DefaultListenerService implements ListenerService {

    private final Map<Listener, ListenerSubscribe> listeners;

    public DefaultListenerService() {
        this.listeners = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(Listener listener) {
        if (this.isSubscribed(listener.getClass()) && this.hasListener(listener.getClass())) {
            this.listeners.put(listener, listener.getClass().getAnnotation(ListenerSubscribe.class));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(Listener... listeners) {
        for (Listener listener : listeners) {
            this.register(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregister(Listener listener) {
        this.listeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterAll() {
        this.listeners.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Listener, ListenerSubscribe> getListeners() {
        return this.listeners;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processCommandExecution(GuildMessageReceivedEvent event) throws Exception {
        // Split rawMessage
        final String[] splitMessage = event.getMessage().getContentRaw().split("\\s+");

        for (Map.Entry<Listener, ListenerSubscribe> entry : this.listeners.entrySet()) {
            //if listener doesn't need an executor
            if (!entry.getValue().needsExecutor()) {
                entry.getKey().execute(new ListenerContext(event, splitMessage));
            }
            //check for executor
            else {
                //add executors
                List<String> executors = new ArrayList<>();
                executors.add(entry.getValue().name().toLowerCase());
                //check if there are no aliases
                if (!entry.getValue().aliases()[0].equals("")) {
                    //add aliases to executors
                    for (String alias : entry.getValue().aliases()) {
                        executors.add(alias.toLowerCase());
                    }
                }
                //check for executors
                if (executors.stream().anyMatch(event.getMessage().getContentRaw().toLowerCase()::contains)) {
                    // Check for permissions
                    if (!hasPermissions(event.getMember(), entry.getValue().requires())) return;
                    //run listener
                    entry.getKey().execute(new ListenerContext(event, splitMessage));
                }
            }
        }
    }

    /**
     * Check if the Class is a listener.
     *
     * @param cls The class of the listener, which should be executed.
     * @return Returns if the Listener contains the annotation.
     */
    private boolean isSubscribed(Class<?> cls) {
        return cls.isAnnotationPresent(ListenerSubscribe.class);
    }

    private boolean hasListener(Class<?> cls) {
        for (Class<?> implement : cls.getInterfaces()) {
            if (implement == Listener.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a member is allowed to execute the listener.
     *
     * @param member             The author, who executed the listener.
     * @param requiresPermission The permission the member needs to execute the listener.
     * @return Returns if the member can execute the listener.
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