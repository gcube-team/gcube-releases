package org.gcube.common.homelibrary.jcr.workspace.util;

import lombok.Data;

@Data
public class PdfMeta {

	int numberOfPages;
	
	String version;
	
	String author;
	
	String title;
	
	String producer;
	
	protected PdfMeta(){}
}
