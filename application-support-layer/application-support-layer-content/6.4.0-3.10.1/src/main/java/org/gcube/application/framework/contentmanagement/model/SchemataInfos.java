package org.gcube.application.framework.contentmanagement.model;

public class SchemataInfos {

	String schemaName;
	String schemaLanguage;
	
	public SchemataInfos () {
		schemaName = new String();
		schemaLanguage = new String();
	}
	
	public SchemataInfos (String name, String language) {
		schemaName = name;
		schemaLanguage = language;
	}
	
	public String getName() {
		return schemaName;
	}
	
	
	public String getLanguage() {
		return schemaLanguage;
	}
	
	public void setName(String name) {
		schemaName = name;
	}
	
	public void setLanguage (String lang) {
		schemaLanguage = lang;
	}
	
	public Boolean isEqual (SchemataInfos si) {
		
		if ((si.getName().equals(this.getName())) && (this.getLanguage().equals(si.getLanguage()))) 
			return true;
		else
			return false;
	}
	
	public SchemataInfos clone () {
		SchemataInfos newSchInfo = new SchemataInfos();
		String name = new String(schemaName);
		String schLang = new String(schemaLanguage);
		newSchInfo.setName(name);
		newSchInfo.setLanguage(schLang);
		
		return newSchInfo;
	}
			
}

