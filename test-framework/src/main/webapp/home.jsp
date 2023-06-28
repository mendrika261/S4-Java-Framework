<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Weby Framework</title>
</head>
<body>
    <h1> Weby Framework </h1>
    <p> Welcome to Weby Framework </p>
    <ul>
        <li><a href="${pageContext.request.contextPath}/session">Session Test</a></li>
        <li><a href="${pageContext.request.contextPath}/json">View a JSON Response (via modelView)</a> </li>
        <li><a href="${pageContext.request.contextPath}/json2">View a JSON Response (returning directly the object)</a> </li>
        <li><a href="${pageContext.request.contextPath}/file">File Upload and input array</a></li>
        <li><a href="${pageContext.request.contextPath}/index.html">Html direct access (bug resolved)</a> </li>
        <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
    </ul>
</body>
</html>