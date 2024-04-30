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
    <h1>File upload and input array</h1>
    <a href="${pageContext.request.contextPath}/">Return home</a>
    <p> You get a class File and you can do anything you want with it <br>
        You can also get the tags as an array using html input name ending with []
    </p>
    <p>
        Files: <%= request.getAttribute("file") %> <br>
        Tags: <%= request.getAttribute("tag") %> <br>
        Local datetime: <%= request.getAttribute("localDateTime") %>
        Local date: <%= request.getAttribute("localDate") %>
        Local time: <%= request.getAttribute("localTime") %>
    </p>
    <form action="${pageContext.request.contextPath}/file" method="post" enctype="multipart/form-data">
        <input type="file" name="file" placeholder="File to upload">
        <input type="text" name="tag[]" placeholder="Tag">
        <input type="text" name="tag[]" placeholder="Another tag">
        <input type="datetime-local" name="localDateTime">
        <input type="date" name="localDate">
        <input type="time" name="localTime">
        <input type="submit">
    </form>
</body>
</html>