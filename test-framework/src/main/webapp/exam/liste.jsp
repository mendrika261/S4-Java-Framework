<%@ page import="java.util.List" %>
<%@ page import="project.dao.exam.PlatConsoView" %>
<!doctype html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Liste</title>
</head>
<body>
    <% List<PlatConsoView> platConsoList = (List<PlatConsoView>) request.getAttribute("platConsoList"); %>
    <h1>Liste</h1>
    <table>
        <tr>
            <th>Date</th>
            <th>Emp</th>
            <th>Plat</th>
            <th>File</th>
        </tr>
        <% for (PlatConsoView platConso: platConsoList) { %>
        <tr>
            <td><%= platConso.getDate() %></td>
            <td><%= platConso.getNom() %> <%= platConso.getPrenom() %></td>
            <td><%= platConso.getPlat() %></td>
            <td><a href="${pageContext.request.contextPath}/upload/<%= platConso.getFile() %>" download="<%= platConso.getFile() %>">Fichier</a></td>
        </tr>
        <% } %>
    </table>
</body>
</html>