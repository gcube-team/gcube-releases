package gr.cite.geoanalytics.context;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Splitter;

@Component
public class SmtpConfig {

	public static enum SMTPAuthentication {
		NONE,
		PLAIN,
		TLS,
		SSL
	}
	
	public static enum SMTPAlert {
		UserAccountLock,
		IllegalRequestAttempt,
		IllegalLayerAccessAttempt,
		IllegalLayerZoomAttempt
	}
	
	private static final SMTPAuthentication smtpAuthenticationTypeDefault = SMTPAuthentication.NONE;
	private static final String smtpAuthenticationTypeDefaultStr = "NONE";
	
	private static final String smtpAlertUserAccountLockDefault = "false";
	private static final String smtpAlertIllegalRequestAttemptDefault = "false";
	private static final String smtpAlertIllegalLayerAccessDefault = "false";
	private static final String smtpAlertIllegalLayerZoomDefault = "false";

	
	private SMTPAuthentication smtpAuthenticationType = smtpAuthenticationTypeDefault;
	private Map<SMTPAlert, Boolean> smtpAlert = new HashMap<>();
	private String smtpServer = null;
	private String smtpServerPort = null;
	private String smtpServerUsername = null;
	private String smtpServerPassword = null;
	
	public SMTPAuthentication getSmtpAuthenticationType() {
		return smtpAuthenticationType;
	}
	@Value("${gr.cite.geoanalytics.app.smtpServerAuthentication:" + smtpAuthenticationTypeDefaultStr + "}")
	public void setSmtpAuthenticationType(SMTPAuthentication smtpAuthenticationType) {
		this.smtpAuthenticationType = smtpAuthenticationType;
	}
	
	public boolean isSmtpAuthenticationEnabled() {
		return smtpAuthenticationType != SMTPAuthentication.NONE;
	}
	
	public String getSmtpServer() {
		return smtpServer;
	}
	@Value("${gr.cite.geoanalytics.app.smtpServerHost}")
	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}
	
	public String getSmtpServerPort() {
		return smtpServerPort;
	}
	@Value("${gr.cite.geoanalytics.app.smtpServerPort}")
	public void setSmtpServerPort(String smtpServerPort) {
		this.smtpServerPort = smtpServerPort;
	}
	
	public String getSmtpServerUsername() {
		return smtpServerUsername;
	}
	@Value("${gr.cite.geoanalytics.app.smtpServerUsername}")
	public void setSmtpServerUsername(String smtpServerUsername) {
		this.smtpServerUsername = smtpServerUsername;
	}
	
	public String getSmtpServerPassword() {
		return smtpServerPassword;
	}
	@Value("${gr.cite.geoanalytics.app.smtpServerPassword}")
	public void setSmtpServerPassword(String smtpServerPassword) {
		this.smtpServerPassword = smtpServerPassword;
	}
	public boolean isSmtpAlertEnabled(SMTPAlert alert) {
		Boolean val = smtpAlert.get(alert);
		return val != null ? val : false;
	}
	public void setSmtpAlert(String smtpAlert) {
		Map<String, String> alerts =  Splitter.on(",").withKeyValueSeparator("=").split(smtpAlert);
		for(Map.Entry<String, String> e : alerts.entrySet())
			this.smtpAlert.put(SMTPAlert.valueOf(e.getKey()), Boolean.parseBoolean(e.getValue()));
	}
	
	@Value("${gr.cite.geoanalytics.app.smptAlert.userAccountLock:" + smtpAlertUserAccountLockDefault + "}")
	public void setUserAccountLockSmtpAlert(boolean value) {
		smtpAlert.put(SMTPAlert.UserAccountLock, value);
	}
	@Value("${gr.cite.geoanalytics.app.smptAlert.illegalRequestAttempt:" + smtpAlertIllegalRequestAttemptDefault + "}")
	public void setIllegalRequestAttemptSmtpAlert(boolean value) {
		smtpAlert.put(SMTPAlert.IllegalRequestAttempt, value);
	}
	@Value("${gr.cite.geoanalytics.app.smptAlert.illegalLayerAccesstAttempt:" + smtpAlertIllegalLayerAccessDefault + "}")
	public void setIllegalRequestLayerAccessSmtpAlert(boolean value) {
		smtpAlert.put(SMTPAlert.IllegalLayerAccessAttempt, value);
	}
	@Value("${gr.cite.geoanalytics.app.smptAlert.illegalLayerZoomAttempt:" + smtpAlertIllegalLayerZoomDefault + "}")
	public void setIllegalRequestLayerZoomSmtpAlert(boolean value) {
		smtpAlert.put(SMTPAlert.IllegalLayerZoomAttempt, value);
	}
	
}
