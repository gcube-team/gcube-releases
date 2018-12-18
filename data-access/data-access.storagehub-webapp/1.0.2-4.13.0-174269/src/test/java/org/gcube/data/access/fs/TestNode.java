package org.gcube.data.access.fs;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.junit.Test;

public class TestNode {

	/*@Test
	public void testShared() throws Exception{
		
		BufferedImage buf = ImageIO.read(new File("/home/lucio/Downloads/djbattle.png"));
		byte[] bigImageInByte = ((DataBufferByte) buf.getData().getDataBuffer()).getData();
		
		System.out.println(new String(Base64.getEncoder().encode(bigImageInByte)));
		
		/*
		Image image = buf.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
		BufferedImage buffered = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
		buffered.getGraphics().drawImage(image, 0, 0, null);
		byte[] imageInByte = ((DataBufferByte) buffered.getData().getDataBuffer()).getData();
		*/
		
		
		/*buffered.getGraphics().drawImage(image, 0, 0 , null);
		ImageIO.write(buffered, "png", buffer );
		byte[] imageInByte = buffer.toByteArray();
	}*/
	
	
	
}
