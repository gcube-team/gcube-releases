package gr.cite.geoanalytics.security;

import org.springframework.security.crypto.password.PasswordEncoder;

public class PBKDF2PasswordEncoder implements PasswordEncoder {
	@Override
	public String encode(CharSequence cs) {
		return new PasswordAuthentication().hash(cs.toString().toCharArray());
	}

	@Override
	public boolean matches(CharSequence cs, String string) {
		return new PasswordAuthentication().authenticate(cs.toString().toCharArray(), string);
	}
}
