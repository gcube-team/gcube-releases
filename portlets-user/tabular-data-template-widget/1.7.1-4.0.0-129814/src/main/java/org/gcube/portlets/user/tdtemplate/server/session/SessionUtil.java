/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server.session;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.tdtemplate.shared.TdTemplateDefinition;
import org.gcube.portlets.user.tdtemplate.shared.validator.ViolationDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 18, 2013
 *
 */
public class SessionUtil {

	/**
	 * 
	 */
//	protected static final String TDT_TEMPLATE_TYPE = "TDT_TEMPLATE_TYPE";
	protected static final String TDT_TEMPLATE_DEF = "TDT_TEMPLATE_DEF";
	private static final String TEMPLATE_CONSTRAINTS_VIOLATIONS = "TEMPLATE_CONSTRAINTS_VIOLATIONS";
	private static final String TEMPLATE_CACHE_EXPRESSIONS = "TEMPLATE_CACHE_EXPRESSIONS";
	private static final String TDT_TEMPLATE_CREATED = "TDT_TEMPLATE_CREATED";
	
	public static Logger logger = LoggerFactory.getLogger(SessionUtil.class);
	
	public static ASLSession getAslSession(HttpSession httpSession)
	{
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (user == null) {

			//for test only
			user = "test.user";
//			user = "lucio.lelii";
//			user = "pasquale.pagano";
//			user = "francesco.mangiacrapa";
//			user = "giancarlo.panichi";
			String scope = "/gcube/devsec/devVRE"; //Development
//			String scope = "/d4science.research-infrastructures.eu/gCubeApps/BiodiversityResearchEnvironment"; //Production
			
			httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);
			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
			session.setScope(scope);

//			logger.warn("TdTemplateServiceImpl STARTING IN TEST MODE - NO USER FOUND");
//			logger.warn("Created fake Asl session for user "+user + " with scope "+scope);
			
			return session;
		}

		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	
//	public static void setTemplateType(ASLSession session, String templateType){
//		session.setAttribute(TDT_TEMPLATE_TYPE, templateType);
//	}
	
	public static void setTemplateDefinition(ASLSession session, TdTemplateDefinition templateType){
		session.setAttribute(TDT_TEMPLATE_DEF, templateType);
	}
	
	
	public static TdTemplateDefinition getTemplateDefinition(ASLSession session){
		return (TdTemplateDefinition) session.getAttribute(TDT_TEMPLATE_DEF);
	}

	/**
	 * @param violations
	 */
	public static void setConstraintsViolations(ASLSession session, List<ViolationDescription> violations) {
		session.setAttribute(TEMPLATE_CONSTRAINTS_VIOLATIONS, violations);
		
	}

	/**
	 * @param session
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public static List<ViolationDescription> getConstraintsViolations(ASLSession session) {
		return (List<ViolationDescription>) session.getAttribute(TEMPLATE_CONSTRAINTS_VIOLATIONS);
	}
	
	
	@SuppressWarnings("unchecked")
	public static CacheServerExpressions getCacheExpression(ASLSession session) {
		return (CacheServerExpressions) session.getAttribute(TEMPLATE_CACHE_EXPRESSIONS);
	}
	
	@SuppressWarnings("unchecked")
	public static void setCacheExpression(ASLSession session, CacheServerExpressions cache) {
		session.setAttribute(TEMPLATE_CACHE_EXPRESSIONS, cache);
	}
	
	public static Template getTemplate(ASLSession session){
		return (Template) session.getAttribute(TDT_TEMPLATE_CREATED);
	}
	
	public static void setTemplate(ASLSession session, Template template){
		session.setAttribute(TDT_TEMPLATE_CREATED, template);
	}

}
