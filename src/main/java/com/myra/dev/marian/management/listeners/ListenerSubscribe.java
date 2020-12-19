package com.myra.dev.marian.management.listeners;

import com.myra.dev.marian.utilities.Permissions;

import java.lang.annotation.*;

/**
 * This annotation marks an implementation of the {@link Listener}.
 * <p>
 * So the {@link ListenerService} can know if the registered command is really a command.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ListenerSubscribe {

    /**
     * Retrieves the name of the listener.
     * @return The listener name.
     */
    String name();

    /**
     * Retrieves all aliases of the listener.
     * @return The listener aliases.
     */
    String[] aliases() default "";

    /**
     * Retrieves the listener type.
     * @return If the command has to be executed with a keyword.
     */
    boolean needsExecutor() default false;

    /**
     * Retrieves the required permissions for this listener.
     * @return The required permissions.
     */
    Permissions requires() default Permissions.MEMBER;
}