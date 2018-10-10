package org.apache.commons.vfs2.provider.test;

import org.apache.commons.vfs2.provider.DecryptSmpUrl;

public class Decrypt {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String url = "smp://Wikipedia_logo_silver.png?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeSxqJkp9OeWKkznDnXYgDz7F/ELBV1lV8qTh/bosrhjOzQb50+GI/1DUWNMQZdZbHMJfYMmmXptQ==";
		String[] parts = url.split("\\?");
		DecryptSmpUrl.decrypt(parts[1]);
		System.out.println("DecryptSmpUrl.serviceClass="+DecryptSmpUrl.serviceClass+"\n"+
				"DecryptSmpUrl.serviceName="+DecryptSmpUrl.serviceName+"\n"+
				"DecryptSmpUrl.owner="+DecryptSmpUrl.owner+"\n"+
				"DecryptSmpUrl.accessType="+DecryptSmpUrl.accessType+"\n"+
				"DecryptSmpUrl.scopeType="+DecryptSmpUrl.scopeType);
	}

}
