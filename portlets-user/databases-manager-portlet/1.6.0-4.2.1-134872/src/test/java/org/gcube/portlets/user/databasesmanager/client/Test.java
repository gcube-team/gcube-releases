package org.gcube.portlets.user.databasesmanager.client;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.uriresolvermanager.UriResolverManager;
import org.gcube.portlets.user.uriresolvermanager.exception.UriResolverMapException;

public class Test {
	public static void main(String[] args) {
		testSMP();
	}
public static void testSMP() {
		
		try {
			ScopeProvider.instance.set("/gcube/devsec/devVRE");
			UriResolverManager resolver = new UriResolverManager("SMP");
			Map<String, String> params = new HashMap<String, String>();
			params.put("smp-uri","smp://Home/database.manager/Workspace/.applications/StatisticalManager/File14 11 2014 10_41_36?5ezvFfBOLqb3YESyI/kesN4T+ZD0mtmc/4sZ0vGMrl0lgx7k85j8o2Q1vF0ezJi/TEYl7d+F4sKR7EwqeONAlQygGb2MgXevBuU5BA4ahZl9CHdGNt1RznRRA9MqSKeNkz0ze4VoCR7VJeXtBAc6lUJs/lgJNczCGmbP5+HKCzc=");
			params.put("fileName", "wikipediaLogo");
			params.put("contentType", "text/plain");
//			params.put("contentType", "image/jpeg"); //true, link is shorted otherwise none
			String shortLink = resolver.getLink(params, true);
			System.out.println(shortLink);
		} catch (UriResolverMapException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
