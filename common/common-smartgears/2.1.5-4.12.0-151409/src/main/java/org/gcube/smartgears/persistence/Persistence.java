package org.gcube.smartgears.persistence;

import java.io.File;

public interface Persistence {

	String location();

	File file(String path);
	
	File writefile(String path);

}