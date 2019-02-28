/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.impl;

import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 21, 2018
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper=false)
public class ImageFile extends FileItem implements org.gcube.common.storagehubwrapper.shared.tohl.items.ImageFileItem{
	/**
	 *
	 */
	private static final long serialVersionUID = 7586826417080458164L;

	private Long width;
	private Long height;
	private Long thumbnailWidth;
	private Long thumbnailHeight;

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.tohl.folder.items.Image#getWidth()
	 */
	@Override
	public Long getWidth() {

		// TODO Auto-generated method stub
		return width;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.tohl.folder.items.Image#getHeight()
	 */
	@Override
	public Long getHeight() {

		// TODO Auto-generated method stub
		return height;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.tohl.folder.items.Image#getThumbnail()
	 */
	@Override
	public InputStream getThumbnail()
		throws InternalErrorException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.tohl.folder.items.Image#getThumbnailWidth()
	 */
	@Override
	public Long getThumbnailWidth() {

		// TODO Auto-generated method stub
		return thumbnailWidth;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.tohl.folder.items.Image#getThumbnailHeight()
	 */
	@Override
	public Long getThumbnailHeight() {

		// TODO Auto-generated method stub
		return thumbnailHeight;
	}
}
