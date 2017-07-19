<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create a new account</title>
</head>
<body>
<c:url value="/signup" var="signURL"/>
<strong>Fill the form below with your data to create a new account.<br>
    All fields are required.</strong><br><br>
<form action='<c:out value="${signURL}"/>' method="post">
    Name: <input type="text" name="newName" required><br><br>
    Surname: <input type="text" name="newSurname" required><br><br>
    PESEL: <input type="text" pattern="[0-9]{11}" name="newPesel" title="Enter 11 digits" required><br><br>
    Username: <input type="text" name="newUsername" required><br><br>
    Password: <input type="text" name="newPassword" required><br><br>
    <input type="submit" value="Create new account"/>
</form>
<br><br>
<c:if test="${requestScope.creationError ne null}">
    <strong style="color: red;"><c:out value="${requestScope.creationError}"/></strong>
</c:if>
</body>
</html>
