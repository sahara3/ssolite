<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
  <head>
    <meta charset="utf-8" />
    <title>Client Application by Struts2</title>
  </head>
  <body>
    <h1>SSOLite - Client Application Page (Struts2)</h1>
    <p>This page requires authentication.</p>

    <s:form action="logout.do" method="POST">
      <input type="submit" value="logout" />
      <!-- input type="hidden" name="_csrf" th:value="${_csrf.token}" / -->
    </s:form>
  </body>
</html>
