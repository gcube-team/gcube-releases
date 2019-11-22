package org.gcube.smartgears.persistence;


import static org.gcube.smartgears.utils.Utils.*;

import java.io.File;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.annotations.NotNull;

@XmlRootElement(name="persistence")
public class DefaultPersistence implements Persistence {

	@XmlAttribute(name="location") @NotNull
	private String location;
	
	public DefaultPersistence() {}
	
	public DefaultPersistence(String location) {
		
		notNull("persistence location",location);
		
		this.location=location;
		validate();
	}
	
	@Override
	public String location() {
		return location;
	}

	@Override
	public File writefile(String path) {
		
		notNull("relative path", path);
		
		return fileAt(new File(location, path).getAbsolutePath()).toWrite();
	}
	
	@Override
	public File file(String path) {
		
		notNull("relative path", path);
		
		return fileAt(new File(location, path).getAbsolutePath()).toRead();
	}
	
	
	//called after JAXB unmarshalling to purge unavailable handlers
	void afterUnmarshal(Unmarshaller u, Object parent) {
		
		validate();
	}
	
	public void validate() {
		
		File locationDir = new File(location);
		if (!(locationDir.exists() && locationDir.isDirectory() && locationDir.canRead() && locationDir.canWrite()))
				throw new IllegalStateException("invalid node configuration: home "+location+" does not exist or is not a directory or cannot be accessed in read/write mode");
	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultPersistence other = (DefaultPersistence) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "local persistence in "+location;
	}
	
}
