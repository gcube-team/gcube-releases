package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import java.awt.Image;

public class ImageDescriptor{
	private String name;		
	private Image image;
	public ImageDescriptor(String name, Image image) {
		super();
		this.name = name;
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public Image getImage() {
		return image;
	}
	
}