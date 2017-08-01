<%-- 
    Author     : Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
--%>

<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html;charset=utf-8" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link rel="icon" href="files/graphics/favicon.ico" type="image/x-icon" />
        <title>X-Search | Linking Marine Resources - Configuration Page - Login</title>


        <link rel="stylesheet" type="text/css" href="css/admin.css" />

    </head>
    <body>

        <%
            String loggedin = (String) session.getAttribute("loggedin");
            if (loggedin == null) {
                loggedin = "";
            }

            if (loggedin.equals("yes")) {
                response.sendRedirect("admin.jsp");
            }
        %>

        <div class="headerContainer">
            <div class="header">

                <div class="logo">
                    <a title="X-Search - Home Page" href="./">
                        <img border="0" src="files/graphics/xsearch_logo.png" />
                    </a>
                </div>
                <div class="menu">
                    <div class="nav">
                        <span class="adminTitle">Configuration Page</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="contentContainer">
            <div class="content">
                <span style="font-family: Calibri;font-size:22px;">
                    Global X-Search Configuration: 
                </span>
                <br />&nbsp;<br />
                <form method="post" action="Admin">
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="inputtext">Username: </span><input type="text" name="username" />
                    <br />&nbsp;<br />        
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="inputtext">Password: </span><input type="password" name="password" />
                    <br />&nbsp;<br />    
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" name="Submit" value="Log in" />
                </form>

                <%
                    if (loggedin.equals("wrong")) {
                %>
                <br />
                <span class="wrongmessage">Wrong username or password! Please try again!</span>
                <%    }
                %>

                <%
                    if (loggedin.equals("no")) {
                %>
                <br />
                <span class="wrongmessage">You must log in!</span>
                <%    }
                %>
                
                <br />&nbsp;<br /><span style="font-family: Calibri;font-size:22px;">or: </span><br />
                <span style="font-family: Calibri;font-size:18px;">
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a title="Session-based configuration" href="sesadmin.jsp">Session-based X-Search Configuration</a>
                </span>
            </div>
        </div>



        <div class="footer">
            &nbsp;
        </div>




    </body>
</html>
