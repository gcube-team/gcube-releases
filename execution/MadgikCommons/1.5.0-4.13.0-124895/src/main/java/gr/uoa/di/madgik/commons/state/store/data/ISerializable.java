package gr.uoa.di.madgik.commons.state.store.data;

import gr.uoa.di.madgik.commons.state.StateManager;

/**
 * Interace that all objects that want to be stored through the
 * {@link StateManager#Put(String, ISerializable)} need to implement.
 * These objects must also have a default constructor publicly accessible.
 *
 * @author gpapanikos
 */
public interface ISerializable
{

	/**
	 * Serializes the needed information for the interface implementing object instance
	 *
	 * @return the byte array containing the information capable of restoring the implementing objects state
	 * @throws java.lang.Exception The serialization could not be performed
	 */
	public byte[] Serialize() throws Exception;

	/**
	 * Deserializes the the information as serialized by the implementing object's {@link ISerializable#Serialize()}
	 * 
	 * @param array The payload that was previously created by {@link ISerializable#Serialize()}
	 * @throws java.lang.Exception The deserialization could not be performed
	 */
	public void Deserialize(byte[] array) throws Exception;
}
