package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.controller;

import com.mongodb.MongoClient;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra.CassandraConnector;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.mongo.MongoDBJDBC;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public LoginController() {
        super();
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        //FOR MONGO DATABASE
        initializeExampleDocuments(request);
        //--------------------------------------
        //FOR CASSANDRA DATABASE
        //initializeExampleDataForCassandra(request);
        //--------------------------------------
        request.getSession().setAttribute("username", "");
        request.getSession().setAttribute("admin", "");
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.jsp");
        rd.forward(request, response);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if ((username == null || username.equals("")) || (password == null || password.equals(""))) {
            request.setAttribute("error", "Login or password missing");
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.jsp");
            rd.forward(request, response);
        } else {
            //FOR MONGO DATABASE
            MongoClient mongo = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");
            MongoDBJDBC dbInstance = MongoDBJDBC.getInstance();
            dbInstance.setMongoClientAndInitializeDB(mongo, "clients");
            dbInstance.initializeCollections();
            DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
            Boolean auth = dbHandler.authenticateUserCredentials(dbInstance, username, password);
            //--------------------------------------------------------------------------------------------
            //FOR CASSANDRA DATABASE
            //CassandraConnector client = dbHandler.initializeAndGetCassandraDBInstance(request);
            //Boolean auth = dbHandler.authenticateUserCredentialsForCassandra(client, username, password);
            //--------------------------------------------------------------------------------------------
            RequestDispatcher rd;
            if (auth) {
                //FOR MONGO DATABASE
                //For security test ONLY
                //Add 'String password' variable to arguments of checkAdminFlag method below
                if (dbHandler.checkAdminFlag(dbInstance, username)) {
                    //--------------------------------------------------------------------------
                    //FOR CASSANDRA DATABASE
                    //if (dbHandler.checkAdminFlagForCassandra(client, username)) {
                    //---------------------------------------------------------------
                    request.getSession().setAttribute("admin", username);
                    rd = getServletContext().getRequestDispatcher("/adminloginsuccess.jsp");
                } else {
                    request.getSession().setAttribute("username", username);
                    rd = getServletContext().getRequestDispatcher("/success.jsp");
                }
            } else
                rd = getServletContext().getRequestDispatcher("/error.jsp");
            rd.forward(request, response);
        }
    }

    private void initializeExampleDocuments(HttpServletRequest request) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        dbHandler.initializeExampleDocuments(dbHandler.initializeAndGetDBInstance(request));
    }

    private void initializeExampleDataForCassandra(HttpServletRequest request) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        CassandraConnector client = dbHandler.initializeAndGetCassandraDBInstance(request);
        dbHandler.initializeExampleDataForCassandra(client);
    }
}
