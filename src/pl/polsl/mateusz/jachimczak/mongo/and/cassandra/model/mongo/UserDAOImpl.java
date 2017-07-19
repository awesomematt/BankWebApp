package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public BasicDBObject findDocInCollection(MongoCollection<BasicDBObject> collection, String key, String value) {
        BasicDBObject query = new BasicDBObject();
        query.put(key, value);
        BasicDBObject foundDocument = collection.find(query).first();
        return foundDocument;
    }

    //For security test ONLY
    //Method with unsecured access to data - swap usage with method above
    public BasicDBObject findDocInCollectionWith2Param(MongoCollection<BasicDBObject> collection,
                                                       String firstKey, String firstValue, String secondKey, String secondValue) {
        String jsonData = "{ " + firstKey + ": " + "'" + firstValue + "'" + ", " + secondKey + ": " + "'" + secondValue + "'" + " }";
        BasicDBObject query = BasicDBObject.parse(jsonData);
        BasicDBObject foundDocument = collection.find(query).first();
        return foundDocument;
    }

    @Override
    public boolean insertUser(MongoCollection<BasicDBObject> collection, User user) {
        collection.insertOne(createDocumentFromUserObject(user));
        return true;
    }

    //For security test ONLY
    //Method with unsecured access to data - swap it with method above
    @Override
    public boolean insertUser2(MongoCollection<BasicDBObject> collection, String jsonDoc) {
        BasicDBObject doc = BasicDBObject.parse(jsonDoc);
        collection.insertOne(doc);
        return true;
    }

    @Override
    public boolean updateUser(MongoCollection<BasicDBObject> collection, BasicDBObject userOld, User userNew) {
        collection.replaceOne(userOld, createDocumentFromUserObject(userNew));
        return true;
    }

    @Override
    public boolean deleteUser(MongoCollection<BasicDBObject> collection, BasicDBObject user) {
        collection.deleteOne(user);
        return true;
    }

    public BasicDBObject createDocumentFromUserObject(User user) {
        BasicDBObject doc = new BasicDBObject("name", user.getName()).
                append("surname", user.getSurname()).
                append("pesel", user.getPesel()).
                append("username", user.getUsername()).
                append("password", user.getPassword());
        return doc;
    }

    public BasicDBObject createAdminDocumentFromUserObject(User user) {
        BasicDBObject adminDoc = createDocumentFromUserObject(user);
        adminDoc.append("admin", "true");
        return adminDoc;
    }

    public List<User> createListOfExampleUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User("Zbigniew", "Stonoga", "67020406022", "zbysiu65", "karakan13"));
        users.add(new User("Lukas", "Hauke", "69080406045", "lukas666", "zabawkawszafie"));
        users.add(new User("Robert", "Mateja", "88040608012", "robcio88", "GdzieJestMojePole"));
        users.add(new User("Zibi", "Smolarek", "72022306022", "ZibiMistrz", "SmolarekStrzelec99"));
        users.add(new User("Zbigniew", "Haduszek", "99020506089", "ZbigniewH", "hakihonor15"));
        users.add(new User("Karolina", "Herera", "89080701033", "karolcia90", "bialybohater"));
        users.add(new User("Elzbieta", "Gaz", "87020406022", "elzbieta99", "eliPola90"));
        users.add(new User("Stanislawa", "Kwas", "76021006033", "StasiaTheBest", "wooohooo999"));
        users.add(new User("Kamila", "Wichrej", "93041509122", "kamilcia93", "budujemyMostY"));
        users.add(new User("Janusz", "Belgrad", "98090904467", "Januszysko98", "januszGosc!@#"));
        return users;
    }
}
