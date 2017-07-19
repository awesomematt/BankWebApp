package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.Account;

interface AccountDAO {
    BasicDBObject findDocInCollection(MongoCollection<BasicDBObject> collection, String key, String value);

    boolean insertAccount(MongoCollection<BasicDBObject> collection, Account account);

    boolean updateAccount(MongoCollection<BasicDBObject> collection, BasicDBObject accountOld, Account accountNew);

    boolean deleteAccount(MongoCollection<BasicDBObject> collection, BasicDBObject account);
}
