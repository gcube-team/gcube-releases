package gr.cite.repo.auth.app.utils;

import java.io.IOException;

public interface LocationResolver {
	public String getContents(String location) throws IOException;
}
