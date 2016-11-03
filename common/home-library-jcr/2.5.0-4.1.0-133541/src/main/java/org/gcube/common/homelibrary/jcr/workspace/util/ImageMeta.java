package org.gcube.common.homelibrary.jcr.workspace.util;

import lombok.Data;

@Data
public class ImageMeta {

	int[] thumbnailSize;

	int height;

	int width;

	protected ImageMeta(){}
}
