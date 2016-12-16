package org.gcube.opensearch.opensearchlibrary;

import java.net.URI;

import javax.activation.MimeType;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class implementing the functionality of an Image element contained in a {@link DescriptionDocument}
 * 
 * @author gerasimos.farantatos
 *
 */
public class ImageElement {
	
//	private Logger logger = LoggerFactory.getLogger(ImageElement.class.getName());
	
	private int width;
	private int height;
	private String MIMEType = null; 
	private URI uri = null;
	
	/**
	 * Creates a new {@link ImageElement} instance
	 * 
	 * @param image A DOM Element that will be processed in order for the {@link ImageElement} instance to be created
	 * @throws Exception If the image element structure does not conform to the OpenSearch specification or in case of other error
	 */
	public ImageElement(Element image) throws Exception {
		Node n = image.getAttributeNode("width");
		if(n == null)
			throw new Exception("Image element lacks width attribute");
		try {
			width = Integer.parseInt(n.getNodeValue());
		}catch(Exception e) {
			throw new Exception("Invalid value in width element", e);
		}
		
		n = image.getAttributeNode("height");
		if(n == null)
			throw new Exception("Image element lacks height attribute");
		
		try {
			height = Integer.parseInt(n.getNodeValue());
		}catch(Exception e) {
			throw new Exception("Invalid value in height element", e);
		}
		
		n = image.getAttributeNode("type");
		if(n == null)
			throw new Exception("Image element lacks type attribute");
			
		MIMEType = n.getNodeValue();
	
		try {
			new MimeType(MIMEType);
		}catch(Exception e) {
			throw new Exception("Malformed MIME type in image element", e);
		}
		String s = image.getFirstChild().getNodeValue();
		try {
			uri = new URI(s);
		}catch(Exception e) {
			throw new Exception("Value of image element is not a valid URI", e);
		}

	}
	
	/**
	 * Returns the image width
	 * 
	 * @return The image width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Returns the image height
	 * 
	 * @return The image height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Returns the MIME type of the image
	 * 
	 * @return The MIME type of the image
	 */
	public String getMimeType() {
		return MIMEType;
	}
	
	/**
	 * Returns the URI of the image
	 * 
	 * @return The URI of the image
	 * @throws Exception In case of error
	 */
	public URI getURI() throws Exception {
		URI u = null;
		try {
			u = new URI(uri.toString());
		}catch(Exception e) { 
			throw new Exception("Unexpected exception caught while copying a URI", e);		
		}
		return u;
	}
}
