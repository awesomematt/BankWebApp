<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Creation successful</title>
    <c:url value="/login" var="loginURL"></c:url>
    <meta http-equiv="refresh" content="4; <c:out value="${loginURL}"></c:out>"/>
</head>
<body>
<strong style="color: green;">Your account has been successfully created.</strong><br>
You will be redirected to the login page automatically, after 5 seconds. If not, simply click
<a href='<c:out value="${loginURL}"></c:out>'>here</a>.
</body>
</html>
