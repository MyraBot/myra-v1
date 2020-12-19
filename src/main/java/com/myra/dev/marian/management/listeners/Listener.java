package com.myra.dev.marian.management.listeners;

public interface Listener {
    /**
     * Executes the command when the implementation if called.
     */
    void execute(ListenerContext ctx) throws Exception;
}
