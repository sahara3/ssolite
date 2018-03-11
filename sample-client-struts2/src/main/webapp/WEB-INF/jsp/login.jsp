<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
  <head>
    <meta charset="utf-8" />
    <title>Local Login Page</title>
  </head>
  <body>
    <h1>Local Login Page</h1>
    <p>Login with user name and password. (admin/struts2)</p>
    <s:if test="#parameters.error.defined">
      <p class="error">Wrong user name or password.</p>
    </s:if>
    <s:if test="#parameters.logout.defined">
      <p class="info">You have been logged out.</p>
    </s:if>
    <s:form action="login.do" method="POST">
      <table>
        <tr>
          <td>User:</td>
          <td><input type="text" name="username" value="" autofocus="autofocus" /></td>
        </tr>
        <tr>
          <td>Password:</td>
          <td><input type="password" name="password" /></td>
        </tr>
        <tr>
          <td colspan="2"><input name="submit" type="submit" value="Login" /></td>
        </tr>
      </table>
      <s:if test="from != null">
        <div><input type="hidden" name="from" value="${from}" /></div>
      </s:if>
    </s:form>
  </body>
</html>
