/**
 *
 */

package org.gcube.portlets.user.workspace;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class ThumbnailGenerator {

	public ThumbnailGenerator() {

	}

	public static void transform(
		String originalFile, int thumbWidth,
		int thumbHeight)
		throws Exception {

		Image image = javax.imageio.ImageIO.read(new File(originalFile));
		double thumbRatio = (double) thumbWidth / (double) thumbHeight;
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		double imageRatio = (double) imageWidth / (double) imageHeight;
		if (thumbRatio < imageRatio) {
			thumbHeight = (int) (thumbWidth / imageRatio);
		}
		else {
			thumbWidth = (int) (thumbHeight * imageRatio);
		}
		if (imageWidth < thumbWidth && imageHeight < thumbHeight) {
			thumbWidth = imageWidth;
			thumbHeight = imageHeight;
		}
		else if (imageWidth < thumbWidth)
			thumbWidth = imageWidth;
		else if (imageHeight < thumbHeight)
			thumbHeight = imageHeight;

		BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);

		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setBackground(Color.WHITE);
		graphics2D.setPaint(Color.WHITE);
		graphics2D.fillRect(0, 0, thumbWidth, thumbHeight);
		graphics2D.setRenderingHint(
			RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);

		String ext = FilenameUtils.getExtension(originalFile);
		String baseName = FilenameUtils.getBaseName(originalFile);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(thumbImage, ext, os);
		InputStream is = new ByteArrayInputStream(os.toByteArray());

		File tempFile = stream2file(is, "thunmb-"+baseName, ext);
		System.out.println(tempFile.getAbsolutePath());

	}

    public static File stream2file (InputStream in, String filename, String extension) throws IOException {

    	extension = extension.startsWith(".")?extension:"."+extension;
        final File tempFile = File.createTempFile(filename, extension);
        //tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

	public static void main(String[] args) throws Exception {

		String originalFile = "/home/francesco-mangiacrapa/Pictures/Screenshot from 2016-04-11 10:20:43.png";
		int thumbWidth = 200;
		int thumbHeight = 200;

		transform(originalFile, thumbWidth, thumbHeight);

	}
}
