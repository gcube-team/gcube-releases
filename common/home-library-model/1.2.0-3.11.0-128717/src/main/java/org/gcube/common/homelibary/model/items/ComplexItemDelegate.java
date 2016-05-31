package org.gcube.common.homelibary.model.items;

import java.io.InputStream;

import lombok.Data;

@Data
public class ComplexItemDelegate {
	
	ItemDelegate itemDelegate;
	
	String filename;

	InputStream file;
}
