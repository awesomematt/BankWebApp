# Bank Web Application
Bank Simulation Web Application.

Simple Java EE Servlet application for GlassFish server with NoSQL database - MongoDB or Cassandra.\
Project created for security tests only - testing the behaviour of NoSQL databases durring injection attacks.

Project was developed in two parts:
- First part, was a creation of unsecured web application which was simulating bank account system.\
  Attempt to attack an unsecured website - trying to log in and steal data from database (injecting malicious code).\
  Collect information about the results from the injection attack (successful injection).
  
- Second part, was an implementation of security "barriers" which should prevent from another injection attack.\
  Attempt to attack a secured website - trying to steal data or log in as an user. 
  Collect information about the results from the injection attack (injection attack fail).
  
# Short project description:
Bank system enables registered users to log in to their accounts, simulate money transfer and also gives them an
opportunity to change their personal account data. Unregistered users can create a new bank account. There is also 
one administrator account, which has access to all users data, specific database information and possibility to delete
any user account from the database.

Try to guess what could happen, if the attacker would be able to take control over the administration account ;)

There could be only one database connected to the application server at the same time.\
Project uses JDBC interface to communicate with a database.\
Databases can be switched - look at the comments in the source code.\
(.JSP files contain pure html code, without any styles and external CSS files)

