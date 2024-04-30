<%@ page import="project.dao.exam.Emp" %>
<%@ page import="java.util.List" %>
<%@ page import="project.dao.exam.Plat" %>
<!doctype html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Insertion</title>
</head>
<body>
<%
    List<Emp> empList = (List<Emp>) request.getAttribute("empList");
    List<Plat> platList = (List<Plat>) request.getAttribute("platList");
%>

<h1>Insertion plat consomme</h1>
<% if (request.getAttribute("message") != null) { %>
<p><%= request.getAttribute("message")%></p>
<% } %>

<p>
    <%= request.getAttribute("localDate") %>
    <%= request.getAttribute("empId") %>
    <%= request.getAttribute("platId") %>
    <%= request.getAttribute("file") %>
</p>

<form method="post" enctype="multipart/form-data" action="${pageContext.request.contextPath}/">
    <label>
        Date
        <input type="date" name="date">
    </label> <br> <br>
    <label>
        Employe
        <select name="emp_id">
            <% for(Emp emp: empList) { %>
            <option value="<%= emp.getId() %>"><%= emp.getNom() + " " + emp.getPrenom() %></option>
            <% } %>
        </select>
    </label> <br> <br>
    <label>
        Plat
        <select name="plat_id">
            <% for(Plat plat: platList) { %>
            <option value="<%= plat.getId() %>"><%= plat.getLibelle() %></option>
            <% } %>
        </select>
    </label> <br> <br>
    <label>
        Fichier
        <input type="file" name="file">
    </label> <br> <br>
    <input type="submit" value="Inserer">
</form>
</body>
</html>
