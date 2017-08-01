package org.gcube.common.authorization.client.proxy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	private static final String NEW_TOKEN_REGEXPR ="[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}-[0-9]+";

	private static final String OLD_TOKEN_REGEXPR ="[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

	private static final String REAL_TOKEN_REGEXPR ="([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})(-[0-9]+)?";


	protected static int getInfrastructureHashfromContext(String context) {
		try{
			String infrastructure = context.split("/")[1];
			return infrastructure.hashCode();
		}catch(Exception e){
			throw new RuntimeException("invalid context");
		}
	}

	public static int getInfrastructureHashFromToken(String token, String defaultInfrastructureToUse) {
		if (token==null) throw new RuntimeException("token required for this method");
		
		if (token.matches(NEW_TOKEN_REGEXPR)){
			String hashCodeAsString = token.substring(token.lastIndexOf("-")+1, token.length());
			return Integer.parseInt(hashCodeAsString);
		} else if (token.matches(OLD_TOKEN_REGEXPR))
			return defaultInfrastructureToUse.hashCode();

		throw new RuntimeException("valid token required for this method");
	}


	protected static String addInfrastructureHashToToken(String token, int infrastructureHash) {
		return String.format("%s-%d", token, infrastructureHash);
	}

	protected static String getRealToken(String token) {
		try{
			Pattern pattern = Pattern.compile(REAL_TOKEN_REGEXPR);
			Matcher matcher = pattern.matcher(token);
			matcher.find();
			String realToken = matcher.group(1);
			return realToken;
		}catch(Exception e){
			throw new RuntimeException("token required for this method", e);
		}
	}
}
