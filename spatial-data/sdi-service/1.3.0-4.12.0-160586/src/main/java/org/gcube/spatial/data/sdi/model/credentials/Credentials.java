package org.gcube.spatial.data.sdi.model.credentials;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Credentials {

	@NonNull
	private String username;
	@NonNull
	private String password;
	@NonNull
	private AccessType accessType;
	@Override
	public String toString() {
		return "Credentials [username=" + username + ", password=****, accessType=" + accessType + "]";
	}
	
	
	
}
