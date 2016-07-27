package org.gcube.portlets.widgets.imagepreviewerwidget.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.imagepreviewerwidget.client.ui.Carousel;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ImagePreviewer implements EntryPoint {
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// decomment for testing purpose
		//test();

	}

	@SuppressWarnings("unused")
	private void test(){

		// random images for test
		List<EnhancedImage> images = new ArrayList<>();
		images.add(new EnhancedImage("http://data.d4science.org/Smp3eEdCdWppOHVCV2tNdUhsL2VITWNZV04ySTFhZ3RHbWJQNStIS0N6Yz0"));
		images.add(new EnhancedImage("https://i.ytimg.com/vi/ReF6iQ7M5_A/maxresdefault.jpg"));
		images.add(new EnhancedImage("http://www.symphony-solutions.eu/wp-content/uploads/2014/09/GDG-DevFest-Ukraine-Banner.jpg"));
		images.add(new EnhancedImage("http://images6.alphacoders.com/316/316963.jpg"));
		images.add(new EnhancedImage("http://ftp.d4science.org/previews/2771b8d3-7b39-451e-a642-fd7eea1c1baa.jpg"));
		images.add(new EnhancedImage("http://nerdist.com/wp-content/uploads/2014/07/ned-stark-970x545.jpg"));
		images.add(new EnhancedImage("http://vignette2.wikia.nocookie.net/gameofthrones/images/2/25/Eddard's_Head.png/revision/latest?cb=20121205211321"));
		images.add(new EnhancedImage("https://upload.wikimedia.org/wikipedia/it/1/17/Il_grande_Lebowski.jpg"));


		final Carousel c = new Carousel();
		c.updateImages(images);

		// button to show the carousel
		Button b = new Button(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				c.show();

			}
		});
		b.setText("Show preview");

		c.show();

		RootPanel.get("image-previewer-div").add(b);
	}
}
