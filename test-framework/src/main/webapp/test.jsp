<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <h1>Test work jsp! <%= request.getAttribute("name") %> </h1>
    <form method="post">
        <input type="text" name="name">
        <input type="date" name="creation">
        <input type="submit" value="submit">
    </form>
</body>
</html>
