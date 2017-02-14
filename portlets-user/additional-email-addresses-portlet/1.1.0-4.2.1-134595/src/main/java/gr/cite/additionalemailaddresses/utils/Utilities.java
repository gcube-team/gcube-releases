package gr.cite.additionalemailaddresses.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.UUID;

import org.apache.commons.validator.EmailValidator;
import org.springframework.stereotype.Component;

/**
 * @author mnikolopoulos
 *
 */
@Component
public class Utilities {

	public String readFile(String fileName) throws IOException {
		
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
        StringBuilder friendRequestTemplate = new StringBuilder();
        String line;
        
    	while ((line = reader.readLine()) != null) {
    		friendRequestTemplate.append(line);
    	}
        reader.close();
        return friendRequestTemplate.toString();
	}

	public String getBody(String firstName, String emailAddress, String domainName, String generatedUUID) throws IOException {
		String template = readFile(AdditionalEmailAddressesConstants.EMAIL_TEMPLATE);
		String link = domainName + "/c" + AdditionalEmailAddressesConstants.STRUTS_ACTION_PATH + "?code=" + generatedUUID;
		String body = MessageFormat.format(template, firstName, emailAddress, link);
		return body;
	}

	public String getSubject(String domainName) {
		String subject = domainName + ", " + AdditionalEmailAddressesConstants.EMAIL_SUBJECT;
		return subject;
	}
	
	public boolean validateEmail(String email) {
		EmailValidator emailValidator = EmailValidator.getInstance();
		return emailValidator.isValid(email);
	}

	public UUID generateUUID() {
		return UUID.randomUUID();
	}
}
