package gr.cite.repo.auth.app.utils;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class FileLocatorResolver implements LocationResolver {

	@Override
	public String getContents(String location) throws IOException {
		String contents = Files.toString(new File(location), Charsets.UTF_8);
		
		return contents;
	}

}
