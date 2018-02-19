package gr.cite.regional.data.collection.application.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "attributes")
public class AttributesDto {
	@JsonIgnore
	private static final Logger logger = LoggerFactory.getLogger(AttributesDto.class);
	
	@JsonProperty("tabmanInfo")
	@XmlElement(name = "tabmanInfo")
	private TabmanInfoDto tabmanInfo;
	
	@JsonProperty("metadata")
	@XmlElement(name = "metadata")
	private MetadataDto metadata;
	
	public TabmanInfoDto getTabmanInfo() {
		return tabmanInfo;
	}
	
	public void setTabmanInfo(TabmanInfoDto tabmanInfo) {
		this.tabmanInfo = tabmanInfo;
	}
	
	public MetadataDto getMetadata() {
		return metadata;
	}
	
	public void setMetadata(MetadataDto metadata) {
		this.metadata = metadata;
	}
	
	/*private static final String DC_NS = "http://purl.org/dc/elements/1.1/";
	private static final String DCTERMS_NS = "http://purl.org/dc/terms/";
	
	@JsonProperty("owner")
	@XmlElement(name = "rightsHolder", namespace = DCTERMS_NS)
	private String owner;
	
	@JsonProperty("context")
	@XmlElement(name = "collection", namespace = DCTERMS_NS)
	private String context;
	
	@JsonProperty("author")
	@XmlElement(name = "creator", namespace = DC_NS)
	private String author;
	
	@JsonProperty("title")
	@XmlElement(name = "title", namespace = DC_NS)
	private String title;
	
	@JsonProperty("publisher")
	@XmlElement(name = "publisher", namespace = DC_NS)
	private String publisher;
	
	@JsonProperty("creationDate")
	@XmlElement(name = "created", namespace = DCTERMS_NS)
	private String creationDate;
	
	@JsonProperty("lastUpdateDate")
	@XmlElement(name = "date", namespace = DC_NS)
	private String lastUpdateDate;
	
	@JsonProperty("expiryDate")
	@XmlElement(name = "valid", namespace = DCTERMS_NS)
	private String expiryDate;
	
	@JsonProperty("copyrightLicense")
	@XmlElement(name = "rights", namespace = DC_NS)
	private String copyrightLicense;
	
	@JsonProperty("spatialScale")
	@XmlElement(name = "spatial", namespace = DCTERMS_NS)
	private String spatialScale;
	
	@JsonProperty("language")
	@XmlElement(name = "language", namespace = DC_NS)
	private String language;
	
	@JsonProperty("identifier")
	@XmlElement(name = "identifier", namespace = DC_NS)
	private String identifier;*/
	
	/*@Override
	public String toString() {
		JAXBContext ctx;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		try {
			ctx = JAXBContext.newInstance(AttributesDto.class);
			Marshaller marshaller = ctx.createMarshaller();
			//marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(this, outputStream);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return outputStream.toString();
	}*/
	
	public static String toXml(AttributesDto attributesDto) throws JAXBException {
		if(attributesDto == null) return null;
		JAXBContext ctx;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		ctx = JAXBContext.newInstance(AttributesDto.class);
		Marshaller marshaller = ctx.createMarshaller();
		//marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(attributesDto, outputStream);
		
		return outputStream.toString();
	}
	
	public static AttributesDto fromXml(String xml) throws JAXBException {
		if (xml == null) return null;
		
		JAXBContext jaxbContext = JAXBContext.newInstance(AttributesDto.class);
		
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		
		ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		return (AttributesDto) jaxbUnmarshaller.unmarshal(inputStream);
	}
}
