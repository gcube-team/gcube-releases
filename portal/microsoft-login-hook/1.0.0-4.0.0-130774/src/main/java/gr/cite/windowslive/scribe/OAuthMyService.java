package gr.cite.windowslive.scribe;

import com.github.scribejava.core.model.AbstractRequest;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20Service;

public class OAuthMyService extends OAuth20Service{

	private static final String VERSION = "2.0";
    private final DefaultApiMy api;

    public OAuthMyService(DefaultApiMy api, OAuthConfig config) {
        super(api, config);
        this.api = api;
    }

    public final Token getAccessTokenWithPost(Verifier verifier) {
    	final OAuthConfig config = getConfig();
    	OAuthRequest oAuthRequest =  new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint(), this);
    	oAuthRequest.addBodyParameter(OAuthConstants.GRANT_TYPE, OAuthConstants.AUTHORIZATION_CODE);
    	oAuthRequest.addBodyParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
    	oAuthRequest.addBodyParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
    	oAuthRequest.addBodyParameter(OAuthConstants.REDIRECT_URI, config.getCallback());
    	oAuthRequest.addBodyParameter(OAuthConstants.CODE,verifier.getValue());
        final Response response = createAccessTokenRequest(verifier, oAuthRequest).send();
        return api.getAccessTokenExtractor().extract(response.getBody());
    }

    protected <T extends AbstractRequest> T createAccessTokenRequest(Verifier verifier, T request) {
        final OAuthConfig config = getConfig();
        request.addParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
        request.addParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
        request.addParameter(OAuthConstants.CODE, verifier.getValue());
        request.addParameter(OAuthConstants.REDIRECT_URI, config.getCallback());
        if (config.hasScope()) {
            request.addParameter(OAuthConstants.SCOPE, config.getScope());
        }
        if (config.hasGrantType()) {
            request.addParameter(OAuthConstants.GRANT_TYPE, config.getGrantType());
        }
        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion() {
        return VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void signRequest(Token accessToken, AbstractRequest request) {
        request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getToken());
    }

    /**
     * Returns the URL where you should redirect your users to authenticate your application.
     *
     * @return the URL where you should redirect your users
     */
    public String getAuthorizationUrl() {
        return api.getAuthorizationUrl(getConfig());
    }

    public DefaultApiMy getApi() {
        return api;
    }
	
}
