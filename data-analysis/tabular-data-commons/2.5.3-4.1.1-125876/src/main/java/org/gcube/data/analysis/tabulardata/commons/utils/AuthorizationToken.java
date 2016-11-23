package org.gcube.data.analysis.tabulardata.commons.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationToken implements Serializable {

	private static HexBinaryAdapter hexAdapter = new HexBinaryAdapter();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String user;
	private String group = null;
	
	protected AuthorizationToken(){}
	
	public AuthorizationToken(String user){
		this.user = user;
	}

	public AuthorizationToken(String user, String group){
		this.user = user;
		this.group = group;
	}
	
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	
	public String getGroup() {
		return group;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuthorizationToken [user=" + user + "]";
	}
	
	public static String marshal(AuthorizationToken v) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(v);
        oos.close();
        byte[] serializedBytes = baos.toByteArray();
        return hexAdapter.marshal(serializedBytes);
    }

    public static AuthorizationToken unmarshal(String v) throws Exception {
        byte[] serializedBytes = hexAdapter.unmarshal(v);
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        AuthorizationToken result = (AuthorizationToken) ois.readObject();
        bais.close();
        return result;
    }
	
}
