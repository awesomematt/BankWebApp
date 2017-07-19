package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra;

import com.datastax.driver.core.Session;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.Account;

public interface CassAccountDAO {
    Account findAccount(Session session, String username);

    boolean insertAccount(Session session, Account account);

    boolean updateAccount(Session session, Account account);

    boolean deleteAccount(Session session, Account account);
}
