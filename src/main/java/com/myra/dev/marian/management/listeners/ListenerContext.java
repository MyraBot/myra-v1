package com.myra.dev.marian.management.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ListenerContext {
    // Variables
    private final GuildMessageReceivedEvent event;
    private final String[] arguments;

    // Constructor
    public ListenerContext(GuildMessageReceivedEvent event, String[] arguments) {
        this.event = event;
        this.arguments = arguments;
    }

    // Get event
    public GuildMessageReceivedEvent getEvent() {
        return event;
    }

    // Get Guild
    public Guild getGuild() {
        return event.getGuild();
    }

    // Get channel
    public TextChannel getChannel() {
        return event.getChannel();
    }

    // Get Message
    public Message getMessage() {
        return event.getMessage();
    }


    // Get arguments
    public String[] getArguments() {
        return arguments;
    }
}
