package gr.cite.geoanalytics.dataaccess.entities.principal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum PrincipalClass {
	ITEM,
	GROUP,
	PROJECT_GROUP;
	
	private UUID classCode;
	
	private static final Map<UUID,PrincipalClass> lookup  = new HashMap<UUID,PrincipalClass>();
	 
	static {
	      for(PrincipalClass it : EnumSet.allOf(PrincipalClass.class)) {
	    	  try {
	    		  it.setIType(UUID.nameUUIDFromBytes(MessageDigest.getInstance("md5").digest(
	    				  (it.toString()).getBytes())));
	    	  }catch(NoSuchAlgorithmException e) {
	    		  e.printStackTrace();
	    	  }
	          lookup.put(it.classCode(), it);
	      }
	 }
	
	PrincipalClass() { }
	
	PrincipalClass(UUID classCode) {
		this.classCode = classCode;
	}
	
	public UUID classCode() { return classCode; }

	private void setIType(UUID classCode) { this.classCode = classCode; }
	
	public static PrincipalClass fromIType(UUID iType) {
		return lookup.get(iType);
	}
};
