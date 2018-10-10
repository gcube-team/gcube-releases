/**
 * 
 */
package org.gcube.common.informationsystem.publisher.impl.registrations.states;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.publisher.impl.utils.Helper;

/**
 * A <code>RegKey</code> class models the key to provide in order to retrive the related Registration object from the Registrations map
 * 
 */
public class RegKey {

    protected static final GCUBELog logger = new GCUBELog(RegKey.class);

    private String source = "";

    private String sink = "";

    private String name = "";

    /**
     * 
     * @param source the endpoint of the registration source
     * @param sink the endpoint of the registration sink
     * @param name the name
     * 
     */
    public RegKey(EndpointReferenceType source, EndpointReferenceType sink, String... name) {
	this.source = Helper.EPR2String(source);
	this.sink = Helper.EPR2String(sink);
	this.name = (name != null && name.length > 0) ? name[0] : "";
    }

    /**
     * Used by the HashMap to compare two keys
     * 
     * @param o the key to compater
     * @return true if the two objects are the same Key
     */
    public boolean equals(Object o) {
	// check the class this object is instance of
	if (!(o instanceof RegKey))
	    return false;
	// compare the two objects
	RegKey key = (RegKey) o;
	if ((key.source.equals(this.source)) && (key.sink.equals(this.sink)) && (key.name.equals(this.name)))
	    return true;
	else
	    return false;

    }

    /**
     * Used by the HashMap
     * 
     * @return the hashcode
     */
    public int hashCode() {
	return this.toString().hashCode();
    }

    /**
     * Returns a String representation of the Key
     * 
     * @return the String representation of the key
     */
    public String toString() {
	return this.source + "-" + this.sink + "-" + this.name;
    }

}