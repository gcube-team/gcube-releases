/*
 * Decompiled with CFR 0_101.
 * 
 * Could not load the following classes:
 *  javax.jcr.Credentials
 *  javax.jcr.Node
 *  javax.jcr.Repository
 *  javax.jcr.RepositoryException
 *  javax.jcr.Session
 *  javax.jcr.SimpleCredentials
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.jackrabbit.api.security.user.Authorizable
 *  org.apache.jackrabbit.api.security.user.User
 *  org.apache.jackrabbit.api.security.user.UserManager
 *  org.apache.jackrabbit.core.SessionImpl
 *  org.apache.jackrabbit.j2ee.PortalUserManager
 *  org.apache.jackrabbit.j2ee.RepositoryAccessServlet
 */
package org.apache.jackrabbit.j2ee;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.JCRWorkspaceItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Exception performing whole class analysis ignored.
 */
public class PortalUserManager extends HttpServlet {
    private static final long serialVersionUID = 1;

    
	protected static Logger logger = LoggerFactory.getLogger(PortalUserManager.class);

    public PortalUserManager() {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	logger.info("Servlet PortalUserManager called ......");
        String userId = request.getParameter("userId");
//        System.out.println("User " + userId);
        String pass = PortalUserManager.getSecurePassword((String)userId);
        String userHome = request.getParameter("userHome");
//        System.out.println("User home path " + userHome);
        String message = "";
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        Repository rep = RepositoryAccessServlet.getRepository((ServletContext)this.getServletContext());
        SessionImpl session = null;
        try {
            try {
                session = (SessionImpl)rep.login((Credentials)new SimpleCredentials("admin", "admin".toCharArray()));
                session.getNode(userHome);
                UserManager um = session.getUserManager();
                if (um.getAuthorizable(userId) == null) {
                 logger.info("User "+ userId + " not present");
                    um.createUser(userId, pass);
                    logger.info(userId + " created");
                } else {
                	logger.info("User present ");
                }
                message = "ok user set";
                response.setContentLength(message.length());
                out.println(message);
            }
            catch (RepositoryException e) {
                message = e.getMessage();
                response.setContentLength(message.length());
                out.println(message);
                if (session != null) {
                    session.logout();
                }
                out.close();
                out.flush();
            }
        }
        finally {
            if (session != null) {
                session.logout();
            }
            out.close();
            out.flush();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    public static String getSecurePassword(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 255));
            }
            digest = sb.toString();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return digest;
    }
}

