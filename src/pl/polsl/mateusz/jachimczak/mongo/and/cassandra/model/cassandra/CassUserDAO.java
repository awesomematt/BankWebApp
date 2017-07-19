package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra;

import com.datastax.driver.core.Session;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.User;

public interface CassUserDAO {
    User findUser(Session session, String username);

    //For security test ONLY
    //User findUser(Session session, String username, String password);

    boolean insertUser(Session session, User user, Integer id);

    boolean updateUser(Session session, User user, Integer userID);

    boolean deleteUser(Session session, User user);
}
