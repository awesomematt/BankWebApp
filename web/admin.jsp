<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Admin Module</title>
    <style>
        table, th, td {
            border: 1px solid black;
        }
    </style>
</head>
<body>
<%-- Users details, managment and database logs --%>
<c:if test="${not empty requestScope.usernames}">
    <table>
        <tbody>
        <tr>
            <th>List of usernames</th>
            <th>User account data</th>
            <th>Database Logs</th>
        </tr>
        <tr>
            <td>
                <c:forEach items="${requestScope.usernames}" var="userdata">
                    <c:out value="${userdata}"/><br>
                </c:forEach>
            </td>
            <td>
                <form action='<c:out value="${adminURL}"></c:out>' method="post">
                    Username: <input type="text" name="username">
                    <input type="submit" value="Show account data"/>
                </form>
                <br>
                <c:if test="${requestScope.userInfo ne null}">
                    <strong style="color: darkmagenta;"><c:out value="${requestScope.userInfo}"/></strong>
                </c:if>
                <c:if test="${requestScope.deletionInfo ne null}">
                    <strong style="color: darkred;"><c:out value="${requestScope.deletionInfo}"/></strong>
                </c:if>
                <br><br>
                <c:if test="${requestScope.accountData ne null}">
                    <c:forEach items="${requestScope.accountData}" var="accountdata">
                        <c:out value="${accountdata}"/><br>
                    </c:forEach><br><br>
                    <form action='<c:out value="${adminURL}"></c:out>' method="post">
                        <input type="hidden" name="accountDelete" value="true"/>
                        <input type="submit" value="Delete User & Account data"/>
                    </form>
                </c:if>
            </td>
            <td>
                <c:forEach items="${requestScope.dblogs}" var="logs">
                    <c:out value="${logs}"/><br>
                </c:forEach>
            </td>
        </tr>
        </tbody>
    </table>
</c:if>
<c:url value="/admin" var="adminURL"></c:url>
<c:url value="/login" var="loginURL"></c:url>
<br><br>
<form action='<c:out value="${loginURL}"></c:out>' method="get">
    <input type="submit" value="Log Out"/>
</form>
</body>
</html>
