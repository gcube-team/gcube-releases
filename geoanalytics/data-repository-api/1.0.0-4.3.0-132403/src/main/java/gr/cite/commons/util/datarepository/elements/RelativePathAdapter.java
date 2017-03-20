package gr.cite.commons.util.datarepository.elements;

import java.io.File;
import java.net.URI;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RelativePathAdapter extends XmlAdapter<URI, URI> {
	private String parentPath;
	public RelativePathAdapter(String parentPath) {
		this.parentPath = parentPath;
	}
	
	public RelativePathAdapter() {
	}
	
	@Override
	public URI unmarshal(URI v) throws Exception {
		if (parentPath == null) {
			return new File(v.getPath()).toURI();
		}
		
		if (parentPath.endsWith(File.separator))	
			return new File(parentPath + v.getPath()).toURI();
		else
			return new File(parentPath + File.separator + v.getPath()).toURI();
	}

	@Override
	public URI marshal(URI v) throws Exception {
		if (parentPath == null) {
			return new URI(v.toASCIIString().replaceFirst("file:", ""));
		}
		
		return new URI(v.toASCIIString().replaceFirst("(file:)?"+parentPath+"(/)?", ""));
	}
}
