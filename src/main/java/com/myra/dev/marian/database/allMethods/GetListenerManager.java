package com.myra.dev.marian.database.allMethods;

import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.utilities.EmbedMessage;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class GetListenerManager {
    //variables
    private MongoDb mongoDb;
    private Guild guild;

    //constructor
    public GetListenerManager(MongoDb mongoDb, Guild guild) {
        this.mongoDb = mongoDb;
        this.guild = guild;
    }

    /**
     * methods
     */
    //check if listener is enabled
    public Boolean check(String command) throws Exception {
        //get listener object
        Document listeners = (Document) mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get("listeners");
        //return value of listener
        return listeners.getBoolean(command);
    }

    //toggle listener
    public void toggle(String listener, String commandEmoji, GuildMessageReceivedEvent event) {
        //get guildDocument
        Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        //get listener object
        Document listeners = (Document) updatedDocument.get("listeners");
        //get new value of listener
        boolean newValue = !listeners.getBoolean(listener);
        //replace String
        listeners.replace(listener, newValue);
        //replace guild Document
        mongoDb.getCollection("guilds").findOneAndReplace(mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(), updatedDocument);
        //success information
        EmbedMessage.Success success = new EmbedMessage.Success()
                .setCommand(listener)
                .setEmoji(commandEmoji)
                .setAvatar(event.getAuthor().getEffectiveAvatarUrl());
        if (newValue) success.setMessage("`" + listener + "` got toggled on").send(event.getChannel());
        else success.setMessage("`" + listener + "` got toggled off").send(event.getChannel());
    }
}