package net.redsierra.Spacio.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class Database {

    private final MongoDatabase database;

    public Database(String mongoUri) {
        MongoClient client = MongoClients.create(mongoUri);
        this.database = client.getDatabase("test"); // o el nombre real de tu base
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
