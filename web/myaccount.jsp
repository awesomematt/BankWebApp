<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Account Management</title>
    <style>
        table, th, td {
            border: 1px solid black;
        }
    </style>
</head>
<body>
<%-- Account details & managment --%>
<c:if test="${requestScope.edition ne null}">
    <strong style="color: orange;"><c:out value="${requestScope.edition}"/></strong>
</c:if>
<c:if test="${not empty requestScope.userData}">
    <table>
        <tbody>
        <tr>
            <th>Name</th>
            <th>Surname</th>
            <th>PESEL</th>
            <th>Username</th>
            <th>Password</th>
            <th>Account Number</th>
            <th>Account Balance</th>
        </tr>
        <tr>
            <c:forEach items="${requestScope.userData}" var="data">
                <td><c:out value="${data}"/></td>
            </c:forEach>
        </tr>
        <tr>
            <c:if test="${requestScope.edition ne null}">
                <form action='<c:out value="${accountURL}"></c:out>' method="post">
                    <input type="hidden" name="dataEdition" value="true">
                    <td><input type="text" name="editedName"></td>
                    <td><input type="text" name="editedSurname"></td>
                    <td><input type="text" pattern="[0-9]{11}" name="editedPesel" title="Enter 11 digits"></td>
                    <td><input type="text" name="editedUsername"></td>
                    <td><input type="text" name="editedPassword"></td>
                    <td><input type="submit" value="Save Data" align="center"/></td>
                </form>
            </c:if>
        </tr>
        </tbody>
    </table>
</c:if>
<c:url value="/myaccount" var="accountURL"></c:url>
<c:url value="/login" var="loginURL"></c:url>
<br><br>
<form action='<c:out value="${accountURL}"></c:out>' method="post">
    Payment amount: <input type="number" step="any" name="paymentAmount" required="true">
    <input type="submit" value="Receive Payment"/>
</form>
<br>
<form action='<c:out value="${accountURL}"></c:out>' method="post">
    Transfer amount: <input type="number" step="any" name="transferAmount" required="true">
    <input type="submit" value="Make a Transfer"/>
</form>
<br><br>
<form action='<c:out value="${accountURL}"></c:out>' method="post">
    <input type="hidden" name="editAccount" value="true">
    <input type="submit" value="Edit Account Details"/>
</form>
<br>
<form action='<c:out value="${loginURL}"></c:out>' method="get">
    <input type="submit" value="Log Out"/>
</form>
<br><br>
<c:if test="${requestScope.paymentInfo ne null}">
    <strong style="color: green;"><c:out value="${requestScope.paymentInfo}"/></strong>
</c:if>
<c:if test="${requestScope.transferInfo ne null}">
    <strong style="color: green;"><c:out value="${requestScope.transferInfo}"/></strong>
</c:if>
<c:if test="${requestScope.success ne null}">
    <strong style="color: green;"><c:out value="${requestScope.success}"/></strong>
</c:if>
<br>
</body>
</html>
