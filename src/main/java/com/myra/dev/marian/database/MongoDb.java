package com.myra.dev.marian.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDb {
    // Instance
    private final static MongoDb MONGO_DB = new MongoDb();

    // Get instance
    public static MongoDb getInstance() {
        return MONGO_DB;
    }

    final String uri = "mongodb+srv://Marian:dGP3e3Iewlqypmxq@cluster0-epzcx.mongodb.net/test";
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
}
