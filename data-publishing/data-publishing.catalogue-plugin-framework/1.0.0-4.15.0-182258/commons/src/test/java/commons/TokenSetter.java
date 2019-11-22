package commons;

import java.util.Properties;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenSetter {

	private static Properties props=new Properties();

	private static final Logger log= LoggerFactory.getLogger(TokenSetter.class);

	static{
		try {
			props.load(TokenSetter.class.getResourceAsStream("/tokens.properties"));
		} catch (Exception e) {
			throw new RuntimeException("YOU NEED TO SET TOKEN FILE IN CONFIGURATION");
		}
	}


	public static void set(String scope){
		try{
			if(!props.containsKey(scope)) throw new RuntimeException("No token found for scope : "+scope);
			SecurityTokenProvider.instance.set(props.getProperty(scope));
		}catch(Throwable e){
			log.warn("Unable to set token for scope "+scope,e);
		}
		ScopeProvider.instance.set(scope);
	}
}
