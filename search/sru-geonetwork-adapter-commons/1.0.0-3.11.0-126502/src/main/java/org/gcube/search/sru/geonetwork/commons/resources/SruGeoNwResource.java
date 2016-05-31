package org.gcube.search.sru.geonetwork.commons.resources;


import java.io.InputStream;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.stream.StreamSource;

import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;


@XmlRootElement
public class SruGeoNwResource extends StatefulResource {

	private static final long serialVersionUID = 1L;

	private String url;

	private String username;
	private String password;

	@XmlElement
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@XmlElement
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@XmlElement
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	@Override
	public void onLoad() throws StatefulResourceException {

	}

	@Override
	public void onClose() throws StatefulResourceException {

	}

	@Override
	public void onDestroy() throws StatefulResourceException {

	}

	@Override
	public String toXML() throws javax.xml.bind.JAXBException {
		String xml = super.toXML();

		String replace = "GUESS";

		xml = xml.replace(">" + this.password + "<", ">" + replace + "<")
				.replace(">" + this.username + "<", ">" + replace + "<");

		return xml;
	}
	
	public static SruGeoNwResource fromXML (String resourceXML){
	    try {
        	JAXBContext jc = JAXBContext.newInstance(SruGeoNwResource.class);
        	Unmarshaller unmarshaller = jc.createUnmarshaller();
        	StreamSource streamSource = new StreamSource(new StringReader(resourceXML));
        	JAXBElement<SruGeoNwResource> je = unmarshaller.unmarshal(streamSource, SruGeoNwResource.class);
        	return (SruGeoNwResource)je.getValue();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
	}
    

	@Override
	public String toString() {
		return "SRUGeonetworkResource [url=" + url + ", username="	+ username + ", password=" + password + "]";
	}

}
