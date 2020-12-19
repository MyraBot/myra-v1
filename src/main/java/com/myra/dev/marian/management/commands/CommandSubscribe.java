package com.myra.dev.marian.management.commands;

import com.myra.dev.marian.utilities.Permissions;

import java.lang.annotation.*;

/**
 * This annotation marks an implementation of the {@link Command}.
 * <p>
 * So the {@link CommandService} can know if the registered command is really a command.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface CommandSubscribe {
    /**
     * Retrieves the name of the command.
     * @return The command name.
     */
    String command() default "";

    /**
     * Retrieves the main executor of the command
     * @return The command executor.
     */
    String name();

    /**
     * Retrieves all aliases of the command.
     * @return The command aliases.
     */
    String[] aliases() default "";

    /**
     * Retrieves the required permissions for this command.
     * @return The required permissions.
     */
    Permissions requires() default Permissions.MEMBER;
}