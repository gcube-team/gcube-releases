package gr.uoa.di.madgik.commons.channel.events;

/**
 * Interface that must be implemented by objects that need to be passed through the {@link ObjectPayloadChannelEvent}.
 * Implementing classes of this interface must also have a default constructor publicly available
 * 
 * @author gpapanikos
 */
public interface ISerializable {
	
	/**
	 * Gets the class name of the object being serialized. This name is used to instantiate the object
	 * when unmarshaled
	 * 
	 * @return the class name
	 */
	public String GetSerializableClassName();
	
	/**
	 * Encodes the payload of the implementing object so that is can later be unmarshaled
	 * 
	 * @return the byte[] the encoded payload
	 * @throws Exception The serialization could not be performed
	 */
	public byte[] Encode() throws Exception;
	
	/**
	 * Decodes the payload of the implementing object as it was returned by {@link ISerializable#Encode()}
	 * 
	 * @param payload the serialization
	 * @throws Exception the Serialization could not be performed
	 */
	public void Decode(byte[] payload) throws Exception;
}
