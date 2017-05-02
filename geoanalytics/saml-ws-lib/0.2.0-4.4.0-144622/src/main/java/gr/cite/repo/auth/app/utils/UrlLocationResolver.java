package gr.cite.repo.auth.app.utils;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class UrlLocationResolver implements LocationResolver {
	@Override
	public String getContents(String location) throws IOException {
		URL url = new URL(location);
		String contents = Resources.toString(url, Charsets.UTF_8);
		
		return contents;
	}

}
