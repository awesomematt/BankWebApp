package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.controller;

import com.mongodb.BasicDBObject;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra.CassandraConnector;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.mongo.MongoDBJDBC;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin")
public class AdminController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AdminController() {
        super();
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String admin = request.getSession().getAttribute("admin").toString();
        request.getSession().setAttribute("usernameUnderAnalysis", "");
        //FOR MONGO DATABASE
        if (authenticateAndInitializeData(request, admin)) {
        //--------------------------------------
        //FOR CASSANDRA DATABASE
        //if (authenticateAndInitializeDataForCassandra(request, admin)) {
            //--------------------------------------
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/admin.jsp");
            rd.forward(request, response);
        } else {
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/unauthorized.jsp");
            rd.forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        String admin = request.getSession().getAttribute("admin").toString();
        String username = request.getParameter("username");
        String userAccountDeletionFlag = request.getParameter("accountDelete");
        String usernameUnderAnalysis = request.getSession().getAttribute("usernameUnderAnalysis").toString();
        //FOR MONGO DATABASE
        if (authenticateAndInitializeData(request, admin)) {
        //--------------------------------------
        //FOR CASSANDRA DATABASE
        //if (authenticateAndInitializeDataForCassandra(request, admin)) {
            //--------------------------------------
            if ((username != null) && (!username.equals(""))) {
                //FOR MONGO DATABASE
                if (checkUserExistance(request, username)) {
                //--------------------------------------
                //FOR CASSANDRA DATABASE
                //if (checkUserExistanceInCassandra(request, username)) {
                    //--------------------------------------
                    request.getSession().setAttribute("usernameUnderAnalysis", username);
                    if (admin.equals(username))
                        request.setAttribute("userInfo", "Action Forbidden - Cannot print admin account details");
                    else
                        //FOR MONGO DATABASE
                        request.setAttribute("accountData", getListOfWholeUserAccountData(request, username));
                        //--------------------------------------------------------------------------------------
                        //FOR CASSANDRA DATABASE
                        //request.setAttribute("accountData", getListOfWholeUserAccountDataFromCassandra(request, username));
                    //--------------------------------------------------------------------------------------
                }
            }
            if ((usernameUnderAnalysis != null) && (userAccountDeletionFlag != null) && (!userAccountDeletionFlag.equals(""))) {
                //FOR MONGO DATABASE
                checkStatusAndDeleteFullUserData(request, userAccountDeletionFlag, usernameUnderAnalysis);
                authenticateAndInitializeData(request, admin);
                //--------------------------------------------------------------------------------------
                //FOR CASSANDRA DATABASE
                //checkStatusAndDeleteFullUserDataForCassandra(request, userAccountDeletionFlag, usernameUnderAnalysis);
                //authenticateAndInitializeDataForCassandra(request, admin);
                //--------------------------------------------------------------------------------------
            }
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/admin.jsp");
            rd.forward(request, response);
        } else {
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/unauthorized.jsp");
            rd.forward(request, response);
        }
    }

    private List<String> getListOfUsernames(HttpServletRequest request) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        MongoDBJDBC dbInstance = dbHandler.initializeAndGetDBInstance(request);
        return dbHandler.getAllUsernames(dbInstance);
    }

    private List<String> getListOfUsernamesFromCassandra(HttpServletRequest request) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        CassandraConnector client = dbHandler.initializeAndGetCassandraDBInstance(request);
        return dbHandler.getAllUsernamesFromCassandra(client);
    }

    private List<String> getListOfDBLogs(HttpServletRequest request) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        MongoDBJDBC dbInstance = dbHandler.initializeAndGetDBInstance(request);
        MessageHandler msgHandler = MessageHandler.getInstance();
        return msgHandler.getAllDatabaseLogsForAdmin(dbInstance);
    }

    private List<String> getListOfDBLogsFromCassandra(HttpServletRequest request) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        CassandraConnector client = dbHandler.initializeAndGetCassandraDBInstance(request);
        MessageHandler msgHandler = MessageHandler.getInstance();
        return msgHandler.getAllDatabaseLogsForAdminFromCassandra(client);
    }

    private List<String> getListOfWholeUserAccountData(HttpServletRequest request, String username) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        MongoDBJDBC dbInstance = dbHandler.initializeAndGetDBInstance(request);
        String info = "--Retrieved User & Account Data Document--" + System.lineSeparator();
        List<String> userDataList = new ArrayList<>();
        List<BasicDBObject> userDocsList = dbHandler.getListOfWholeAccountData(dbInstance, username);
        userDataList.add(info);
        for (BasicDBObject o : userDocsList)
            userDataList.add(o.toString());
        return userDataList;
    }

    private List<String> getListOfWholeUserAccountDataFromCassandra(HttpServletRequest request, String username) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        CassandraConnector client = dbHandler.initializeAndGetCassandraDBInstance(request);
        String info = "--Retrieved User & Account Data Document--" + System.lineSeparator();
        List<String> userDataList = dbHandler.getListOfWholeAccountDataFromCassandra(client, username);
        userDataList.add(0, info);
        return userDataList;
    }

    private Boolean authenticateAndInitializeData(HttpServletRequest request, String admin) {
        if ((admin != null) && (!admin.equals(""))) {
            request.setAttribute("usernames", getListOfUsernames(request));
            request.setAttribute("dblogs", getListOfDBLogs(request));
            return true;
        } else
            return false;
    }

    private Boolean authenticateAndInitializeDataForCassandra(HttpServletRequest request, String admin) {
        if ((admin != null) && (!admin.equals(""))) {
            request.setAttribute("usernames", getListOfUsernamesFromCassandra(request));
            request.setAttribute("dblogs", getListOfDBLogsFromCassandra(request));
            return true;
        } else
            return false;
    }

    private void checkStatusAndDeleteFullUserData(HttpServletRequest request, String deletion, String username) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        MongoDBJDBC dbInstance = dbHandler.initializeAndGetDBInstance(request);
        if ((deletion != null) && (!deletion.equals(""))) {
            dbHandler.deleteUserWithAccount(dbInstance, username);
            request.setAttribute("deletionInfo", "Data successfully erased");
        } else
            request.setAttribute("deletionInfo", "Error - blank input");
    }

    private void checkStatusAndDeleteFullUserDataForCassandra(HttpServletRequest request, String deletion, String username) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        CassandraConnector client = dbHandler.initializeAndGetCassandraDBInstance(request);
        if ((deletion != null) && (!deletion.equals(""))) {
            dbHandler.deleteUserWithAccountForCassandra(client, username);
            request.setAttribute("deletionInfo", "Data successfully erased");
        } else
            request.setAttribute("deletionInfo", "Error - blank input");
    }

    private Boolean checkUserExistance(HttpServletRequest request, String username) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        MongoDBJDBC dbInstance = dbHandler.initializeAndGetDBInstance(request);
        if (dbHandler.checkIfUserExists(dbInstance, username)) {
            request.setAttribute("userInfo", "User data found in database");
            return true;
        } else {
            request.setAttribute("userInfo", "User doesn't exist in database");
            return false;
        }
    }

    private Boolean checkUserExistanceInCassandra(HttpServletRequest request, String username) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        CassandraConnector client = dbHandler.initializeAndGetCassandraDBInstance(request);
        if (dbHandler.checkIfUserExistsInCassandraDB(client, username)) {
            request.setAttribute("userInfo", "User data found in database");
            return true;
        } else {
            request.setAttribute("userInfo", "User doesn't exist in database");
            return false;
        }
    }
}
