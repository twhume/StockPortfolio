<%-- 
    Document   : index
    Created on : Feb 29, 2012, 10:35:16 AM
    Author     : twhume
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Stock Portfolio</title>
    </head>
    <body>
        <h1>Welcome to the Stock Portfolio</h1>
        <h2>Your transactions</h2>
        
        <h:dataTable id="dt1" value="#{portfolio.transactions}" var="item" first="0" rules="all">

  <h:outputText value="This is 'dataTable' demo" />

<h:column>
   <h:outputText value="#{item.id}"></h:outputText>
</h:column>

<h:column>
   <h:outputText value="#{item.amount}"></h:outputText>
</h:column>

<h:column>
   <h:outputText value="#{item.pricePaid}"></h:outputText>
</h:column>


</h:dataTable>
        
        <!-- Transaction list goes here, with delete button -->
        <p><a href="#">Click here</a> to add a new transaction.</p>
    </body>
</html>
