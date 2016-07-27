package org.gcube.portlets.user.dataminermanager.client.computations;

import org.gcube.portlets.user.dataminermanager.client.DataMinerManager;
import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationValueImage;

import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ComputationValueImagePanel extends SimpleContainer {
	private ComputationValueImage computationValueImage;

	public ComputationValueImagePanel(
			ComputationValueImage computationValueImage) {
		this.computationValueImage = computationValueImage;
		init();
		create();
	}

	private void init() {
		setBorders(false);
	}

	private void create() {
		VerticalLayoutContainer v = new VerticalLayoutContainer();
		add(v);
		if (computationValueImage == null
				|| computationValueImage.getValue() == null
				|| computationValueImage.getValue().isEmpty())
			v.add(new HtmlLayoutContainer("<i>No image found.</i>"),
					new VerticalLayoutData(-1, -1, new Margins(0)));
		else {
			HtmlLayoutContainer imageName;
			if (computationValueImage.getFileName() == null
					|| computationValueImage.getFileName().isEmpty()) {
				imageName = new HtmlLayoutContainer("");
			} else {
				imageName = new HtmlLayoutContainer(
						computationValueImage.getFileName());
			}

			v.add(imageName, new VerticalLayoutData(-1, -1, new Margins(0)));
			TextButton saveImageBtn = new TextButton("Download Image");
			saveImageBtn.setIcon(DataMinerManager.resources.download());
			saveImageBtn.addSelectHandler(new SelectEvent.SelectHandler() {

				@Override
				public void onSelect(SelectEvent event) {
					com.google.gwt.user.client.Window.open(
							computationValueImage.getValue(),
							computationValueImage.getFileName(), "");
				}
			});

			v.add(saveImageBtn, new VerticalLayoutData(-1, -1, new Margins(0)));
			final Image img = new Image(computationValueImage.getValue());
			img.setPixelSize(640, 480);
			v.add(img, new VerticalLayoutData(1, -1, new Margins(0)));
		}

		forceLayout();

	}

}
