package org.gcube.portlets.user.reportgenerator.client.toolbar;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

/**
 * This {@link ImageBundle} is used for all the button icons. Using an image
 * bundle allows all of these images to be packed into a single image, which
 * saves a lot of HTTP requests, drastically improving startup time.
 */
@SuppressWarnings("deprecation")
public interface Images extends ImageBundle {
	/**
	 * @return .
	 */
	AbstractImagePrototype importing();
	/**
	 * @return .
	 */
	AbstractImagePrototype bold();
	/**
	 * @return .
	 */
	AbstractImagePrototype createLink();
	/**
	 * @return .
	 */
	AbstractImagePrototype hr();
	/**
	 * @return .
	 */
	AbstractImagePrototype indent();
	/**
	 * @return .
	 */
	AbstractImagePrototype insertImage();
	/**
	 * @return .
	 */
	AbstractImagePrototype italic();
	/**
	 * @return .
	 */
	AbstractImagePrototype justifyCenter();
	/**
	 * @return .
	 */
	AbstractImagePrototype justifyLeft();
	/**
	 * @return .
	 */
	AbstractImagePrototype justifyRight();
	/**
	 * @return .
	 */
	AbstractImagePrototype closeDoc();
	/**
	 * @return .
	 */
	AbstractImagePrototype newdoc();
	
	/**
	 * @return .
	 */
	AbstractImagePrototype open_template();
	/**
	 * @return .
	 */
	AbstractImagePrototype open_report();
	/**
	 * @return .
	 */
	AbstractImagePrototype ol();
	/**
	 * @return .
	 */
	AbstractImagePrototype removeFormat();
	/**
	 * @return .
	 */
	AbstractImagePrototype removeLink();
	/**
	 * @return .
	 */
	AbstractImagePrototype save();
	/**
	 * @return .
	 */
	AbstractImagePrototype db_save();
	/**
	 * @return .
	 */
	AbstractImagePrototype structureView();
	/**
	 * @return .
	 */
	AbstractImagePrototype strikeThrough();
	/**
	 * @return .
	 */
	AbstractImagePrototype subscript();
	/**
	 * @return .
	 */
	AbstractImagePrototype superscript();
	/**
	 * @return .
	 */
	AbstractImagePrototype ul();
	/**
	 * @return .
	 */
	AbstractImagePrototype underline();
	/**
	 * @return .
	 */
	AbstractImagePrototype foreColors();
}
