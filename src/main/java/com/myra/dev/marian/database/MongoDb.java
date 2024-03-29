package com.myra.dev.marian.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;

public class MongoDb {
    // Mongo db atlas
    /*
    // Instance
    private final static MongoDb MONGO_DB = new MongoDb();

    // Get instance
    public static MongoDb getInstance() {


        return MONGO_DB;
    }

    final String uri = "mongodb+srv://Marian:dGP3e3Iewlqypmxq@cluster0.epzcx.mongodb.net/test?retryWrites=true&w=majority";
    // Haven't tested it yet
    private MongoClientURI clientURI = new MongoClientURI(uri);
    private MongoClient client = new MongoClient(clientURI);
    public MongoDatabase database = client.getDatabase("Myra");


    //get Collection method
    public MongoCollection<Document> getCollection(String collection) {
        return database.getCollection(collection);
    }

    //close connection method
    public void close() {
        client.close();
    }
     */
    private final MongoClient client;
    private final MongoDatabase database;

    public MongoDb() {
        final String user = "marian"; // the user name
        final String database = "admin"; // the name of the database in which the user is defined
        final char[] password = "marian2005".toCharArray(); // the password as a character array

        MongoCredential credential = MongoCredential.createCredential(user, database, password);
        MongoClientOptions options = MongoClientOptions.builder().sslEnabled(false).build();

        this.client = new MongoClient(
                new ServerAddress("h2911463.stratoserver.net"),
                Arrays.asList(credential),
                options
        );
        this.database = client.getDatabase("Myra");
    }

    // Instance
    private final static com.myra.dev.marian.database.MongoDb MONGO_DB = new com.myra.dev.marian.database.MongoDb();

    // Get instance
    public static com.myra.dev.marian.database.MongoDb getInstance() {
        return MONGO_DB;
    }

    //get Collection method
    public MongoCollection<Document> getCollection(String collection) {
        return database.getCollection(collection);
    }
}