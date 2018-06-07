<%-- 
    Document   : config
    Created on : Jul 17, 2013, 2:59:02 PM
    Author     : fafalios
--%>

<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.util.Properties"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>X-Search-Portlet Configuration</title>
    </head>
    <body>
        <%

            String id = request.getParameter("id");
            if (id == null) {
                id = "";
            }
            if (!id.equals("112233")) {
                out.print("<h1>Sorry, you are not authorised to see this page :)</h1>");
                return;
            }

            Properties prop = new Properties();
            InputStream in = new FileInputStream(this.getServletContext().getRealPath("PropertyFiles/XSearch.properties"));
            prop.load(in);

        %>

        <h2>X-Search-Portlet Configuration Page</h2>

        <form method="post" action="Config">

            <%
                for (String pname : prop.stringPropertyNames()) {
                    String pnameEnding = pname.substring(pname.indexOf(".") + 1);
            %>
            <label for="<%=pnameEnding%>"><%=pname%></label>
            <input size="50px" type="text" value="<%=prop.getProperty(pname)%>" id="<%=pnameEnding%>" name="<%=pnameEnding%>" />
            <br />
            <%
                }
            %>
            <input type="submit" name="Submit" value="Submit" /><input type="reset" name="Reset" value="Reset" />
        </form>
            
        <br />
        <%
               in.close();
               String success = request.getParameter("success");
               if (success == null) {
                   success = "";
               }
               
               if (success.trim().toLowerCase().equals("true")) {
                   out.print("<h3 style='color:green'>The property file was updated successfully!</h3>");
               }
               
               if (success.trim().toLowerCase().equals("false")) {
                   out.print("<h3 style='color:red'>Problem in updating the properties file! Please try again!</h3>");
               }
               
               
        %>

    </body>
</html>
