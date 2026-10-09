<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Forward directly to HomeServlet controller
    response.sendRedirect(request.getContextPath() + "/home");
%>
