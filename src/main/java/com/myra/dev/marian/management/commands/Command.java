package com.myra.dev.marian.management.commands;

/**
 * Represents a command.
 */
public interface Command {
    /**
     * Executes the command when the implementation if called.
     */
    void execute(CommandContext ctx) throws Exception;
}