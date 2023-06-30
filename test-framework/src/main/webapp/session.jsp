<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Weby Framework</title>
</head>
<body>
    <h1>Session</h1>
    <a href="${pageContext.request.contextPath}/">Return home</a>
    <p>Set session here, and it will appear even if after changed the page</p>
    <p>Session: <%= session.getAttribute("session") %>(From session), <%= request.getAttribute("session") %>(From attribute in class)
        <a href="${pageContext.request.contextPath}/session?remove">Remove this session</a>
    </p>
    <p>Remove all session:
        <a href="${pageContext.request.contextPath}/session?invalidate">Invalidate</a>
    </p>
    <form action="${pageContext.request.contextPath}/session" method="POST">
        <input type="text" name="session" id="session" placeholder="Your name for example">
        <input type="submit" value="Set Session">
    </form>
</body>
</html>