package org.gcube.data.access.storagehub.handlers.content;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.gcube.common.storagehub.model.annotations.MimeTypeHandler;
import org.gcube.common.storagehub.model.items.ImageFile;
import org.gcube.common.storagehub.model.items.nodes.ImageContent;
import org.gcube.common.storagehub.model.types.ItemAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MimeTypeHandler({"image/gif", "image/jpeg","image/png","image/svg+xml"})
public class ImageHandler implements ContentHandler{

	private static final int THUMB_MAX_DIM 	= 300;

	private ImageContent content = new ImageContent();

	private static final Logger logger = LoggerFactory.getLogger(ImageHandler.class);

	@Override
	public void initiliseSpecificContent(InputStream is, String fileName) throws Exception {
		Image image = javax.imageio.ImageIO.read(is);

		int width = image.getWidth(null);
		int height = image.getHeight(null);

		content.setWidth(Long.valueOf(width));
		content.setHeight(Long.valueOf(height));

		try {
			int[] dimension = getThumbnailDimension(width, height);

			byte[] buf = transform(image, fileName, dimension[0], dimension[1]).toByteArray();


			content.setThumbnailHeight(Long.valueOf(dimension[1]));
			content.setThumbnailWidth(Long.valueOf(dimension[0]));
			content.setThumbnailData(buf);
		}catch(Throwable t) {
			logger.warn("thumbnail for file {} cannot be created ", fileName,t);
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


	public ByteArrayOutputStream transform(Image image, 
			String originalFile, int thumbWidth,
			int thumbHeight)
					throws Exception {

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

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(thumbImage, ext, os);
		return os;
	}

}
