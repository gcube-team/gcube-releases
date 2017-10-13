package gr.cite.geoanalytics.security;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Sessions created under HTTPS, for which the session cookie is marked as “secure”, cannot subsequently be used under 
 * HTTP. The browser will not send the cookie back to the server and any session state will be lost (including the
 * security context information)
 *
 * Tomcat tracks user sessions with the help of the JSESSIONID cookie. If you enter into HTTPS with Tomcat, the cookie
 * will come back with the secure property being set to true. Subsequently when the redirection to http occurs, the
 * browser will not transmit the JSESSIONID cookie and you'll get a new session.
 *
 * This filter overrides the default Tomcat JSESSIONID behaviour
 */
public class HttpsCookieFilter implements Filter {

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final HttpSession session = httpRequest.getSession(false);

        if (session != null) {
            final Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
            sessionCookie.setMaxAge(-1);
            sessionCookie.setSecure(false);
            sessionCookie.setPath(httpRequest.getContextPath());
            httpResponse.addCookie(sessionCookie);
        }

        chain.doFilter(request, response);
    }

}