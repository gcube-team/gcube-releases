package gr.cite.windowslive.scribe;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.AccessTokenExtractor;
import com.github.scribejava.core.extractors.TokenExtractor20Impl;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.Verb;

public abstract class DefaultApiMy extends DefaultApi20{
	/**
     * Returns the access token extractor.
     *
     * @return access token extractor
     */
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new TokenExtractor20Impl();
    }

    /**
     * Returns the verb for the access token endpoint (defaults to GET)
     *
     * @return access token endpoint verb
     */
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    /**
     * Returns the URL that receives the access token requests.
     *
     * @return access token URL
     */
    public abstract String getAccessTokenEndpoint();

    /**
     * Returns the URL where you should redirect your users to authenticate your application.
     *
     * @param config OAuth 2.0 configuration param object
     * @return the URL where you should redirect your users
     */
    public abstract String getAuthorizationUrl(OAuthConfig config);

    /**
     * {@inheritDoc}
     */
    @Override
    public OAuthMyService createService(OAuthConfig config) {
        return new OAuthMyService(this, config);
    }
}
