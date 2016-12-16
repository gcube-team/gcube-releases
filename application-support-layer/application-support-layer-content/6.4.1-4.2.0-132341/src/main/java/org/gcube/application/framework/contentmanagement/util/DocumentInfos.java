package org.gcube.application.framework.contentmanagement.util;

public class DocumentInfos {
	
	private String pdfURI;
	private String documentId;
	private String collectionId;
	private String name;
	private String referenceId;
	
	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String refId) {
		this.referenceId = refId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DocumentInfos() {
		pdfURI = new String();
		documentId = new String();
		collectionId = new String();
		referenceId = new String();
		name = new String();
	}
	
	public String getPdfURI() {
		return pdfURI;
	}
	
	public void setPdfURI(String pdfURI) {
		this.pdfURI = pdfURI;
	}
	
	public String getDocumentId() {
		return documentId;
	}
	
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	public String getCollectionId() {
		return collectionId;
	}
	
	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}
	
	

}
