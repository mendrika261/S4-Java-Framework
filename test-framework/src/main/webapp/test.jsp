<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <h1>Test work jsp! <%= request.getAttribute("name") %> </h1>
    <form method="post" enctype="multipart/form-data">
        <input type="text" name="name[]">
        <input type="text" name="name[]">
        <input type="file" name="file">
        <input type="submit" value="submit">
    </form>
</body>
</html>
