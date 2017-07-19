<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Login Error</title>
    <c:url value="/login" var="loginURL"></c:url>
    <meta http-equiv="refresh" content="4; <c:out value="${loginURL}"></c:out>"/>
</head>
<body>
<b>An error has occurred: </b><strong style="color: red;">Bad login credentials.</strong><br>
You will be redirected to the login page automatically, after 5 seconds. If not, simply click
<a href='<c:out value="${loginURL}"></c:out>'>here</a>.
</body>
</html>
