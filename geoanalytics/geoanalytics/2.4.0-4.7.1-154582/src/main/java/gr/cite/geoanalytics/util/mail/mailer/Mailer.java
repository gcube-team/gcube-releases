package gr.cite.geoanalytics.util.mail.mailer;

import java.util.List;

public interface Mailer
{
	public void sendTo(String recipient, List<String> cc, List<String> bcc, String subject, String text) throws Exception;
}
