/**
 *
 */
package org.gcube.portlets.user.tdtemplate.server.session;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.portal.PortalContext;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.portlets.user.tdtemplate.shared.PortalContextInfo;
import org.gcube.portlets.user.tdtemplate.shared.TdTemplateDefinition;
import org.gcube.portlets.user.tdtemplate.shared.validator.ViolationDescription;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class SessionUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 30, 2017
 */
public class SessionUtil {

	protected static final String TDT_TEMPLATE_DEF = "TDT_TEMPLATE_DEF";
	private static final String TEMPLATE_CONSTRAINTS_VIOLATIONS = "TEMPLATE_CONSTRAINTS_VIOLATIONS";
	private static final String TEMPLATE_CACHE_EXPRESSIONS = "TEMPLATE_CACHE_EXPRESSIONS";
	private static final String TDT_TEMPLATE_CREATED = "TDT_TEMPLATE_CREATED";

	public static Logger logger = LoggerFactory.getLogger(SessionUtil.class);


	/**
	 * Gets the portal context.
	 *
	 * @param request the request
	 * @return the portal context
	 */
	public static PortalContextInfo getPortalContext(HttpServletRequest request)
	{

		PortalContext pContext = PortalContext.getConfiguration();
		//USER
		GCubeUser user = pContext.getCurrentUser(request);
		String username = user.getUsername();
		String fullName = user.getFullname();
		String email = user.getEmail();
		String avatarID = user.getUserAvatarId();
		String avatarURL = user.getUserAvatarURL();
		//SESSION
		String currentScope = pContext.getCurrentScope(request);
		String userToken = pContext.getCurrentUserToken(request);
		long currGroupId = pContext.getCurrentGroupId(request);

		return new PortalContextInfo(username, fullName, email, avatarID, avatarURL, currentScope, userToken, currGroupId);
	}

	/**
	 * Gets the portal context.
	 *
	 * @param request the request
	 * @return the portal context having minimal parameters: username, scope and user-token
	 * All other attributes are null
	 */
	public static PortalContextInfo getMinPortalContext(HttpServletRequest request)
	{

		PortalContext pContext = PortalContext.getConfiguration();
		//USER
		GCubeUser user = pContext.getCurrentUser(request);
		String username = user.getUsername();
		//SESSION
		String currentScope = pContext.getCurrentScope(request);
		String userToken = pContext.getCurrentUserToken(request);

		return new PortalContextInfo(username,currentScope, userToken);
	}



	/**
	 * Gets the key to session.
	 *
	 * @param key the key
	 * @param scope the scope
	 * @return the key to session
	 */
	private static String getKeyToSession(String key, String scope){
		return key+scope;
	}


	/**
	 * Sets the template definition.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @param templateType the template type
	 */
	public static void setTemplateDefinition(HttpSession session, String scope, TdTemplateDefinition templateType){
		session.setAttribute(getKeyToSession(TDT_TEMPLATE_DEF,scope), templateType);
	}


	/**
	 * Gets the template definition.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @return the template definition
	 */
	public static TdTemplateDefinition getTemplateDefinition(HttpSession session, String scope){
		return (TdTemplateDefinition) session.getAttribute(getKeyToSession(TDT_TEMPLATE_DEF,scope));
	}

	/**
	 * Sets the constraints violations.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @param violations the violations
	 */
	public static void setConstraintsViolations(HttpSession session, String scope, List<ViolationDescription> violations) {
		session.setAttribute(getKeyToSession(TEMPLATE_CONSTRAINTS_VIOLATIONS,scope), violations);

	}

	/**
	 * Gets the constraints violations.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @return the constraints violations
	 */
	@SuppressWarnings("unchecked")
	public static List<ViolationDescription> getConstraintsViolations(HttpSession session, String scope) {
		return (List<ViolationDescription>) session.getAttribute(getKeyToSession(TEMPLATE_CONSTRAINTS_VIOLATIONS,scope));
	}


	/**
	 * Gets the cache expression.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @return the cache expression
	 */
	@SuppressWarnings("unchecked")
	public static CacheServerExpressions getCacheExpression(HttpSession session, String scope) {
		return (CacheServerExpressions) session.getAttribute(getKeyToSession(TEMPLATE_CACHE_EXPRESSIONS,scope));
	}

	/**
	 * Sets the cache expression.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @param cache the cache
	 */
	@SuppressWarnings("unchecked")
	public static void setCacheExpression(HttpSession session, String scope, CacheServerExpressions cache) {
		session.setAttribute(	getKeyToSession(TEMPLATE_CACHE_EXPRESSIONS,scope), cache);
	}

	/**
	 * Gets the template.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @return the template
	 */
	public static Template getTemplate(HttpSession session, String scope){
		return (Template) session.getAttribute(getKeyToSession(TDT_TEMPLATE_CREATED,scope));
	}

	/**
	 * Sets the template.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @param template the template
	 */
	public static void setTemplate(HttpSession session, String scope, Template template){
		session.setAttribute(getKeyToSession(TDT_TEMPLATE_CREATED,scope), template);
	}

}
