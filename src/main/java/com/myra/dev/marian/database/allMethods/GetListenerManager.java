package com.myra.dev.marian.database.allMethods;

import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.utilities.Utilities;
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
        if (newValue) {
            Utilities.getUtils().success(event.getChannel(), listener, commandEmoji, "`" + listener + "` got toggled on", "`" + listener + "` is now enabled", event.getAuthor().getEffectiveAvatarUrl(), false, null);
        } else {
            Utilities.getUtils().success(event.getChannel(), listener, commandEmoji, "`" + listener + "` got toggled off", "`" + listener + "` is now disabled", event.getAuthor().getEffectiveAvatarUrl(), false, null);
        }
    }
}
