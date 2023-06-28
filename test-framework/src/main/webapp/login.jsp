<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Weby framework</title>
</head>
<body>
    <h1>Weby framework</h1>
    <p>
        This is a simple test page for Weby framework. <br>
        <strong>Login: </strong> admin <br>
        <strong>Password: </strong> admin
    </p>
    <% if(request.getAttribute("error") != null) { %>
    <p> <%= request.getAttribute("error") %> </p>
    <% } %>
    <form action="${pageContext.request.contextPath}/login" method="POST">
        <input type="text" name="username" placeholder="username">
        <input type="password" name="password" placeholder="password">
        <input type="submit" value="Login">
    </form>
</body>
</html>