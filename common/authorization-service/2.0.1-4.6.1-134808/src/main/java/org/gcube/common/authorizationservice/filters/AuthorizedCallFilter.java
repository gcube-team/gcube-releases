package org.gcube.common.authorizationservice.filters;


import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorizationservice.configuration.AllowedEntity;
import org.gcube.common.authorizationservice.configuration.AuthorizationConfiguration;
import org.gcube.common.authorizationservice.configuration.AuthorizationRule;
import org.gcube.common.authorizationservice.configuration.ConfigurationHolder;
import org.gcube.common.authorizationservice.util.TokenPersistence;

@WebFilter(urlPatterns={"/*"}, filterName="authorizationFilter")
@Slf4j
public class AuthorizedCallFilter implements Filter {

	private static final String TOKEN_HEADER="gcube-token";

	public static final String AUTH_ATTRIBUTE="authorizationInfo";

	@Inject
	TokenPersistence persistence;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		String token = request.getParameter(TOKEN_HEADER)==null?((HttpServletRequest)request).getHeader(TOKEN_HEADER):
			request.getParameter(TOKEN_HEADER);

		AuthorizationEntry info = null;
		if (token!=null){
			info = persistence.getAuthorizationEntry(token);
			log.info("call from {} ",info);
		} else log.info("call without token");

		request.setAttribute(AUTH_ATTRIBUTE, info);

		String pathInfo = ((HttpServletRequest) request).getPathInfo();
		log.info("called path {}", pathInfo);

		if (pathInfo==null || pathInfo.isEmpty()){
			log.info("call rejected from filters: invalid path info");
			return;
		}


		if (requiresToken(pathInfo) && token==null ){
			((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
			log.info("call rejected from filters, call requires caller token");
			return;
		}

		String callerIp = ((HttpServletRequest)request).getHeader("x-forwarded-for");
		if(callerIp==null)
			callerIp=request.getRemoteHost();
		log.info("caller ip {}", callerIp);

		/*X509Certificate certs[] = 
			    (X509Certificate[])req.getAttribute("javax.servlet.request.X509Certificate");
		// ... Test if non-null, non-empty.

		X509Certificate clientCert = certs[0];

		// Get the Subject DN's X500Principal
		X500Principal subjectDN = clientCert.getSubjectX500Principal();*/

		if (!checkAllowed(pathInfo, callerIp, info)){
			((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
			log.info("call rejected from filters");
			return;
		}

		chain.doFilter(request, response);
	}

	private boolean requiresToken(String pathInfo) {
		AuthorizationConfiguration conf = ConfigurationHolder.getConfiguration();
		List<AuthorizationRule> rules = conf.getAuthorizationRules();
		for (AuthorizationRule rule: rules)
			if (pathInfo.startsWith(rule.getServletPath()) || pathInfo.equals(rule.getServletPath()))
				return rule.isTokenRequired();
		return false;
	}

	
	//TODO: review it, something is not working check if is correct that they are in OR
	private boolean checkAllowed(String pathInfo, String callerIp, AuthorizationEntry info){
		AuthorizationConfiguration conf = ConfigurationHolder.getConfiguration();
		List<AuthorizationRule> rules = conf.getAuthorizationRules();
		for (AuthorizationRule rule: rules){
			if (pathInfo.startsWith(rule.getServletPath()) || pathInfo.equals(rule.getServletPath())){
				if (!rule.getAcceptedTokenType().isEmpty() && !rule.getAcceptedTokenType().contains(info.getClientInfo().getType())){
					log.info("rejecting the call: callerType {} is not in the allowed types defined {} ", info.getClientInfo().getType(), rule.getAcceptedTokenType());
					return false;
				}
				
				if (!rule.getEntities().isEmpty()){
					for (AllowedEntity entity : rule.getEntities()){
						switch(entity.getType()){ 
						case IP:
							log.trace("checking ip rule : {} -> {}", entity.getValue(), callerIp);
							if(checkIpInRange(callerIp, entity.getValue()))
								return true;
							break;
						case USER: 
							log.trace("checking user rule : {} -> {}", entity.getValue(), info.getClientInfo().getId());
							if (entity.getValue().equals(info.getClientInfo().getId()))
								return true;
							break;
						case ROLE:
							log.trace("checking role rule : {} -> {}", entity.getValue(), info.getClientInfo().getRoles());
							if (info.getClientInfo().getRoles().contains(entity.getValue()))
								return true;
							break;
						}
					}
					//IF a servlet path matches the caller is not allowed to that servlet (the call should be rejected)
					return false;
				}
			}
		}
		return true;
	}


	private static  boolean checkIpInRange(String ip, String mask) {

		String[] maskArray = mask.split("\\.");
		String[] ipArray = ip.split("\\.");

		int[] maskByte= new int[4];
		int[] ipByte = new int[4];

		for (int i=0; i<4; i++){
			maskByte[i] = ((Integer)Integer.parseInt(maskArray[i])).byteValue();
			ipByte[i] = ((Integer)Integer.parseInt(ipArray[i])).byteValue();
		}
		return (maskByte[0]==0 || maskByte[0]==ipByte[0]) &&  (maskByte[1]==0 || maskByte[1]==ipByte[1]) &&
				(maskByte[2]==0 || maskByte[2]==ipByte[2]) && (maskByte[3]==0 || maskByte[3]==ipByte[3]);

	}


	@Override
	public void destroy() {}


}
