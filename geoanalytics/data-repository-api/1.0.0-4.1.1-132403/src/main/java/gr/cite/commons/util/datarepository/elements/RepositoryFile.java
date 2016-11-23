package gr.cite.commons.util.datarepository.elements;

import java.io.InputStream;
import java.net.URI;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import gr.cite.commons.util.datarepository.utils.InputstreamDeserializer;
import gr.cite.commons.util.datarepository.utils.InputstreamSerializer;

@XmlRootElement(name="file")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE) 
@JsonInclude(value = Include.NON_NULL)
public class RepositoryFile
{
	public static enum State {
		TRANSIENT,
		PERSISTED
	}
	
	public static Logger log = LoggerFactory.getLogger(RepositoryFile.class);
	
	private String id;
	private String originalName;
	private String dataType;
	private long size = 0;
	boolean permanent = false;
	
	@JsonSerialize(using = InputstreamSerializer.class)
	@JsonDeserialize(using = InputstreamDeserializer.class)
	private InputStream inputStream = null;
	
	private URI uri = null;
	
	private URI localImage = null; //set only if file is temporarily mapped to local filesystem
	                               //the value is a file scheme URI
	private long timestamp; //files receive a timestamp upon creation so that their local image is not sweeped until a period equal to sweep period elapses
	
	private State state = State.TRANSIENT;
	
	public String getId()
	{
		return id;
	}
	
	@XmlElement
	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getOriginalName()
	{
		return originalName;
	}
	
	@XmlElement
	public void setOriginalName(String originalName)
	{
		this.originalName = originalName;
	}
	
	public String getDataType()
	{
		return dataType;
	}
	
	@XmlElement
	public void setDataType(String dataType)
	{
		this.dataType = dataType;
	}
	
	public long getSize()
	{
			return size;
	}
	
	@XmlElement
	public void setSize(long size)
	{
		this.size = size;
	}
	
	public boolean isPermanent()
	{
		return permanent;
	}

	@XmlElement
	public void setPermanent(boolean permanent)
	{
		this.permanent = permanent;
	}

	/**
	 * Retrieves the set input stream. If the file's state is {@link State#PERSISTED} a new stream will be opened based on the
	 * {@link RepositoryFile}'s local image, otherwise the current instance is returned
	 * @return
	 */
	public InputStream getInputStream()
	{
		if(state == State.PERSISTED && localImage != null) {
			try {
				inputStream = localImage.toURL().openStream();
			}catch(Exception e) {
				log.error("Unable to obtain stream from local image", e);
			}
		}
		return inputStream;
	}
	
	public boolean hasInputStream() {
		return inputStream != null;
	}
	
	public InputStream peekInputStream() {
		return inputStream;
	}
	
	@XmlTransient 
	public void setInputStream(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}
	
	public URI getUri()
	{
		return uri;
	}

	public void setUri(URI file)
	{
		this.uri = file;
	}
	
	public URI getLocalImage()
	{
		return localImage;
	}
	
	@XmlTransient
	public State getState() {
		return state;
	}

	public void markPersisted() {
		this.state = State.PERSISTED;
	}

	@XmlJavaTypeAdapter(RelativePathAdapter.class)
	@XmlElement(name = "relativePath")
	public void setLocalImage(URI localImage)
	{
		this.localImage = localImage;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	@XmlElement
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
	
}
