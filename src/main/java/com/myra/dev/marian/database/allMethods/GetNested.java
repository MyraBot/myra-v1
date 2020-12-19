package com.myra.dev.marian.database.allMethods;

import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.management.Manager;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class GetNested {
    //variables
    private MongoDb mongoDb;
    private Guild guild;
    private String nested;

    //constructor
    public GetNested(MongoDb mongoDb, Guild guild, String nested) {
        this.mongoDb = mongoDb;
        this.guild = guild;
        this.nested = nested;
    }

    /**
     *
     * @param key The key to search.
     * @param type Class type to return.
     * @param <T> The generic.
     * @return Returns a value as the specified class type.
     */
    public <T> T get(String key, Class<T> type) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get(nested, Document.class).get(key, type);
    }

    /**
     * @param key The key to search.
     * @return Returns a value from the given key.
     */
    public String getString(String key) {
        return mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get(nested, Document.class).getString(key); // Return value
    }

    // Set Object
    public void set(String key, Object value, Manager.type type) {
        // Get guildDocument
        Document guildDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first();
        //get nested object
        Document nestedDocument = (Document) guildDocument.get(nested);
// Get variable type
        // String
        if (type.equals(Manager.type.STRING)) {
            // Replace String
            nestedDocument.replace(key, (String) value);
        }
        if (type.equals(Manager.type.INTEGER)) {
            // Replace String
            nestedDocument.replace(key, (Integer) value);
        }
        if (type.equals(Manager.type.BOOLEAN)) {
            // Replace String
            nestedDocument.replace(key, (Boolean) value);
        }
        // Replace guild Document
        mongoDb.getCollection("guilds").findOneAndReplace(mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(), guildDocument);
    }
}
