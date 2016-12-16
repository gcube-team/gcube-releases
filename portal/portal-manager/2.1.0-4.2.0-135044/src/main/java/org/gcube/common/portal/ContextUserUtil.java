package org.gcube.common.portal;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.model.Company;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.Encryptor;
/**
 *
 * @author Massimiliano Assante, CNR-ISTI
 *
 */
public class ContextUserUtil {
	private static final Logger _log = LoggerFactory.getLogger(PortalContext.class);
	/**
	 * 
	 * @param httpServletRequest
	 * @returnthe current user LR id
	 */
	protected static Long getCurrentUserId(HttpServletRequest httpServletRequest) {		
		Cookie[] cookies = httpServletRequest.getCookies();
		String userId = null;
		String companyId = null;
		if (cookies != null) {
			for (Cookie c : cookies) {
				if ("COMPANY_ID".equals(c.getName())) {
					companyId = c.getValue();
				} else if ("ID".equals(c.getName())) {
					userId = hexStringToStringByAscii(c.getValue());
				}
			}
			if (userId != null && companyId != null) {
				try {
					Company company = CompanyLocalServiceUtil.getCompany(Long.parseLong(companyId));
					Key key = company.getKeyObj();
					String userIdPlain = Encryptor.decrypt(key, userId);
					return Long.valueOf(userIdPlain);              

				} catch (Exception pException) {
					_log.warn("Exception while getting current user from cookie, returning current user from http header");
					return getUserFromHeader(httpServletRequest);
				}
			} else {
				if (isWithinPortal()) {
					_log.debug("Something wrong with cookies, returning current user from http header");
					return getUserFromHeader(httpServletRequest);
				} else { //you must be in dev
					_log.debug("DEV MODE Intercepted ...");
					return null;
				}
			}
		} else {
			_log.warn("Cookies are not present, returning current user from http header");
			return getUserFromHeader(httpServletRequest);
		}
	}

	private static long getUserFromHeader(HttpServletRequest httpServletRequest) {
		String userHeaderIdString = httpServletRequest.getHeader(PortalContext.USER_ID_ATTR_NAME);
		long userIdToReturn = -1;
		try {
			userIdToReturn = Long.parseLong(userHeaderIdString);
		} catch (NumberFormatException e) {
			_log.error("The userId is not a number -> " + userHeaderIdString);
		}
		return userIdToReturn;
	}

	private static String hexStringToStringByAscii(String hexString) {
		byte[] bytes = new byte[hexString.length() / 2];
		for (int i = 0; i < hexString.length() / 2; i++) {
			String oneHexa = hexString.substring(i * 2, i * 2 + 2);
			bytes[i] = Byte.parseByte(oneHexa, 16);
		}
		try {
			return new String(bytes, "ASCII");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	private static boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (Exception ex) {			
			_log.trace("Development Mode ON");
			return false;
		}			
	}
}
