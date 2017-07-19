package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.User;

interface UserDAO {

    BasicDBObject findDocInCollection(MongoCollection<BasicDBObject> collection, String key, String value);

    //For security test ONLY
    //Method with unsecured access to data
    BasicDBObject findDocInCollectionWith2Param(MongoCollection<BasicDBObject> collection, String firstKey, String firstValue, String secondKey, String secondValue);

    boolean insertUser(MongoCollection<BasicDBObject> collection, User user);

    //For security test ONLY
    //Method with unsecured access to data
    boolean insertUser2(MongoCollection<BasicDBObject> collection, String jsonDoc);

    boolean updateUser(MongoCollection<BasicDBObject> collection, BasicDBObject userOld, User userNew);

    boolean deleteUser(MongoCollection<BasicDBObject> collection, BasicDBObject user);

}
