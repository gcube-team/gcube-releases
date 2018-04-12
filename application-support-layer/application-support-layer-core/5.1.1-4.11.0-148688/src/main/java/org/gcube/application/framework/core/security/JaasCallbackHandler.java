package org.gcube.application.framework.core.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JaasCallbackHandler implements CallbackHandler{
	
	protected String username = null;
    protected String password = null;

    private static final Logger logger = LoggerFactory.getLogger(JaasCallbackHandler.class);
    
    public JaasCallbackHandler(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public void handle(Callback callbacks[])
            throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                logger.debug("responding to NameCallback");
                ((NameCallback) callbacks[i]).setName(username);
            } else if (callbacks[i] instanceof PasswordCallback) {
                logger.debug("responding to PasswordCallback");
                ((PasswordCallback) callbacks[i]).setPassword(password != null ? password.toCharArray() : new char[0]);
            } else {
                logger.debug("unsupported callback: " + callbacks[i].getClass());
                throw new UnsupportedCallbackException(callbacks[i]);
            }
        }
    }

}
