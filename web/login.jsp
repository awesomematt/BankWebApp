<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>National Bank Service</title>
</head>
<body>
Log in, to access your bank account.<br>
<%-- Login logic --%>
<c:url value="/login" var="loginURL"/>
<c:url value="/signup" var="signupURL"/>
<br>
<form action='<c:out value="${loginURL}"/>' method="post">
    Enter username: <input type="text" name="username"> <br>
    Enter password: <input type="password" name="password"> <br>
    <input type="submit" value="Log in"/>
</form>
<br>
<c:if test="${requestScope.error ne null}">
    <strong style="color: red;"><c:out value="${requestScope.error}"/></strong>
</c:if>
Don't have an account? <a href="<c:out value="${signupURL}"/>">SIGN UP</a> for free!
</body>
</html>
