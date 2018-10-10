package org.gcube.data.access.storagehub.handlers.content;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.gcube.common.storagehub.model.annotations.MimeTypeHandler;
import org.gcube.common.storagehub.model.items.ImageFile;
import org.gcube.common.storagehub.model.items.nodes.ImageContent;
import org.gcube.common.storagehub.model.types.ItemAction;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageProcessor;

@MimeTypeHandler({"image/gif", "image/jpeg","image/png","image/svg+xml"})
public class ImageHandler implements ContentHandler{

	private static final int THUMB_MAX_DIM 				= 50;
	
	private ImageContent content = new ImageContent();
	

	@Override
	public void initiliseSpecificContent(InputStream is) throws Exception {
		BufferedImage buf = ImageIO.read(is);
		content.setWidth(Long.valueOf(buf.getWidth()));
		content.setHeight(Long.valueOf(buf.getHeight()));
		
		ImagePlus image = new ImagePlus("thumbnail", buf);
		
		int thumbSize[] = getThumbnailDimension(buf.getWidth(), buf.getHeight());
		
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream(); InputStream thumbstream = getThumbnailAsPng(image, thumbSize[0], thumbSize[1])){

			byte[] imgbuf = new byte[1024];
			int read = -1;
			while ((read=thumbstream.read(imgbuf))!=-1)
				baos.write(imgbuf, 0, read);
			
			content.setThumbnailHeight(Long.valueOf(thumbSize[1]));
			content.setThumbnailWidth(Long.valueOf(thumbSize[0]));
			content.setThumbnailData(Base64.getEncoder().encode(baos.toByteArray()));
		} catch (Exception e) {
			// TODO: handle exception
		}	

	}

	@Override
	public ImageContent getContent() {
		return content;
	}

	private int[] getThumbnailDimension(int original_width, int original_height) {
		int new_width = 0;
		int new_height = 0;

		if ((original_width < THUMB_MAX_DIM) &&  (original_height< THUMB_MAX_DIM)){
			new_width = original_width;
			new_height = original_height;
		}
		if (original_width > THUMB_MAX_DIM) {
			new_width = THUMB_MAX_DIM;
			new_height = (new_width * original_height) / original_width;
		}

		if (original_width < THUMB_MAX_DIM) {
			new_width = THUMB_MAX_DIM;
			new_height = (new_width * original_height) / original_width;
		}

		if (new_height > THUMB_MAX_DIM) {
			new_height = THUMB_MAX_DIM;
			new_width = (new_height * original_width) / original_height;
		}

		if (new_width > THUMB_MAX_DIM) {
			new_width = THUMB_MAX_DIM;
			new_height = (new_width * original_height) / original_width;
		}

		int[] dimension = {new_width, new_height};

		return dimension;
	}

	
	private InputStream getThumbnailAsPng(ImagePlus img, int thumbWidth, 
			int thumbHeight) throws IOException {

		InputStream stream = null;
		ImageProcessor processor = img.getProcessor();
		try{
			Image thumb = processor.resize(thumbWidth, thumbHeight).createImage();
			thumb = thumb.getScaledInstance(thumbWidth,thumbHeight,Image.SCALE_SMOOTH);

			FileSaver fs = new FileSaver(new ImagePlus("",thumb));
			File tmpThumbFile = File.createTempFile("THUMB", "TMP");
			tmpThumbFile.deleteOnExit();

			fs.saveAsPng(tmpThumbFile.getAbsolutePath());
			stream =  new FileInputStream(tmpThumbFile);

		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		return stream;
	}

	@Override
	public ImageFile buildItem(String name, String description, String login) {
		ImageFile item = new ImageFile();
		Calendar now = Calendar.getInstance();
		item.setName(name);
		item.setTitle(name);
		item.setDescription(description);
		//item.setCreationTime(now);
		item.setHidden(false);
		item.setLastAction(ItemAction.CREATED);
		item.setLastModificationTime(now);
		item.setLastModifiedBy(login);
		item.setOwner(login);
		item.setContent(this.content);
		return item;
	}

}
