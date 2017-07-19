//package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.controller;
//
//import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra.CassandraConnector;
//
//import javax.servlet.ServletContext;
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//import javax.servlet.annotation.WebListener;
//
//@WebListener
//public class CassandraContextListener implements ServletContextListener {
//
//    CassandraConnector client;
//
//    public void contextInitialized(ServletContextEvent servletContextEvent) {
//        ServletContext ctx = servletContextEvent.getServletContext();
//
//        client = CassandraConnector.getInstance();
//        client.startConnection(ctx.getInitParameter("CASSANDRA_HOST"));
//        ctx.setAttribute("CASSANDRA_CLIENT", client);
//    }
//
//    public void contextDestroyed(ServletContextEvent servletContextEvent) {
//        client.closeConnection();
//    }
//}
