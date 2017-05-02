package gr.cite.repo.auth.app.cookies;

import  javax.ws.rs.core.Cookie;

import com.google.inject.Singleton;

@Singleton
public class CookieFactory {

	public Cookie createCookie(String sessionId){
		return new Cookie("JSESSIONID", sessionId);
	}

}
