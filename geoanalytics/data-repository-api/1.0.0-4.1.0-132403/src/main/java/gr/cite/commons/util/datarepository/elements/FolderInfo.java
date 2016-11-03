package gr.cite.commons.util.datarepository.elements;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="folder")
@XmlSeeAlso(RepositoryFile.class)
public class FolderInfo
{
		private String id = null;
		
		private Set<RepositoryFile> files = new HashSet<RepositoryFile>();
		
		private URI uri = null;
		
		@XmlTransient private Map<String, RepositoryFile> lookup = new HashMap<String, RepositoryFile>();
		
		public String getId()
		{
			return id;
		}
		
		@XmlElement
		public void setId(String id)
		{
			this.id = id;
		}
		
		@XmlElementWrapper(name="files")
		@XmlElement(name="file")
		public Set<RepositoryFile> getFiles()
		{
			return files;
		}
		
		public RepositoryFile getFile(String id) throws Exception
		{
			return lookup.get(id);
		}
		
		public void addFile(RepositoryFile f) throws Exception
		{
			if(this.files.contains(f)) throw new Exception("File " + id + " already exists");
			this.files.add(f);
			this.lookup.put(f.getId(), f);
		}
		
		public void removeFile(String id) throws Exception
		{
			RepositoryFile f = this.lookup.get(id);
			if(f == null) throw new Exception("File" + id + " does not exist");
			this.files.remove(f);
			this.lookup.remove(id);
		}
		
		public void setFiles(Set<RepositoryFile> files)
		{
			this.files = files;
			this.lookup = new HashMap<String, RepositoryFile>();
			for(RepositoryFile f : files)
				this.lookup.put(f.getId(), f);
		}

		public URI getUri()
		{
			return uri;
		}

		@XmlJavaTypeAdapter(RelativePathAdapter.class)
		@XmlElement(name = "relativePath")
		public void setUri(URI uri)
		{
			this.uri = uri;
		}
		
		public void createLookups()
		{
			this.lookup = new HashMap<String, RepositoryFile>();
			for(RepositoryFile f : files) {
				f.markPersisted();
				this.lookup.put(f.getId(), f);
			}
		}
}
