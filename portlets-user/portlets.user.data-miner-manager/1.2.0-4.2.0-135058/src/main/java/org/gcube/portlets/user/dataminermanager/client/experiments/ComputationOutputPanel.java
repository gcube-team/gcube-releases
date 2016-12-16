/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.experiments;

import java.util.LinkedHashMap;
import java.util.Map;

import org.gcube.portlets.user.dataminermanager.client.common.EventBusProvider;
import org.gcube.portlets.user.dataminermanager.client.events.OutputDataEvent;
import org.gcube.portlets.user.dataminermanager.client.events.OutputDataRequestEvent;
import org.gcube.portlets.user.dataminermanager.client.util.UtilsGXT3;
import org.gcube.portlets.user.dataminermanager.client.widgets.FileViewer;
import org.gcube.portlets.user.dataminermanager.client.widgets.ImageViewer;
import org.gcube.portlets.user.dataminermanager.client.widgets.ResourceViewer;
import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationId;
import org.gcube.portlets.user.dataminermanager.shared.data.output.FileResource;
import org.gcube.portlets.user.dataminermanager.shared.data.output.ImageResource;
import org.gcube.portlets.user.dataminermanager.shared.data.output.MapResource;
import org.gcube.portlets.user.dataminermanager.shared.data.output.ObjectResource;
import org.gcube.portlets.user.dataminermanager.shared.data.output.Resource;
import org.gcube.portlets.user.dataminermanager.shared.data.output.Resource.ResourceType;
import org.gcube.portlets.user.dataminermanager.shared.data.output.TableResource;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ComputationOutputPanel extends SimpleContainer {

	private ComputationId computationId;
	private VerticalLayoutContainer v;

	public ComputationOutputPanel(ComputationId computationId) {
		super();
		this.computationId = computationId;
		Log.debug("ComputationOutputPanel: " + computationId);
		bind();
		init();
		OutputDataRequestEvent event = new OutputDataRequestEvent(computationId);
		EventBusProvider.INSTANCE.fireEvent(event);

	}
	
	private void init() {
		v = new VerticalLayoutContainer();
		add(v);
	}

	private void bind() {
		EventBusProvider.INSTANCE.addHandler(OutputDataEvent.TYPE,
				new OutputDataEvent.OutputDataEventHandler() {

					@Override
					public void onOutput(OutputDataEvent event) {
						Log.debug("Catch OutputDataEvent: " + event);
						if (event!=null&& event.getOutputData()!=null&&
								event.getOutputData().getComputationId()!=null&&
								event.getOutputData().getComputationId().getId()
								.compareTo(computationId.getId()) == 0) {
							manageOutputDataEvent(event);
						}

					}
				});

	}

	/**
	 * 
	 * @param resource
	 */
	private void manageOutputDataEvent(OutputDataEvent event) {
		try {
			if (event == null || event.getOutputData() == null) {
				Log.error("OutputDataEvent is null");
				UtilsGXT3.alert("Error", "Invalid output data!");
				return;
			}
			Resource resource = event.getOutputData().getResource();
			showResource(resource);
		} catch (Throwable e) {
			Log.error("Error in show output info: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void showResource(Resource resource) {
		try {
			Log.info("Show Output Info on: " + resource);
			if (resource == null) {
				Log.debug("Output Data hasn't resource!");
				return;
			}

			ResourceType resourceType = resource.getResourceType();

			switch (resourceType) {

			case FILE:
				FileResource fileResource = (FileResource) resource;
				v.add(getHtmlTitle("The algorithm produced a", "File"),
						new VerticalLayoutData(1, -1, new Margins(0)));
				v.add(getFileResourceOutput(fileResource),
						new VerticalLayoutData(1, -1, new Margins(0)));
				break;

			case TABULAR:
				//TableResource tabResource = (TableResource) resource;
				v.add(getHtmlTitle("The algorithm produced a", "Table"),
						new VerticalLayoutData(1, -1, new Margins(0)));
				// v.add(getTabResourceOutput(tabResource),
				// new VerticalLayoutData(1, -1, new Margins(0)));
				break;

			case IMAGE:
				v.add(getHtmlTitle("The algorithm produced an", "Set of Images"),
						new VerticalLayoutData(1, -1, new Margins(0)));
				final ImageResource imagesResource = (ImageResource) resource;
				v.add(getImagesResourceOutput(imagesResource),
						new VerticalLayoutData(1, -1, new Margins(0)));
				break;

			case MAP:
				v.add(getHtmlTitle("The algorithm produced ",
						"Multiple Results"), new VerticalLayoutData(1, -1,
						new Margins(0)));

				final MapResource mapResource = (MapResource) resource;

				v.add(getMultipleOutput(mapResource), new VerticalLayoutData(1,
						-1, new Margins(0)));
				break;
			case ERROR:
				break;
			case OBJECT:
				break;
			default:
				break;

			}

			forceLayout();
		} catch (Throwable e) {
			Log.error("Error in show resource: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	public interface TitleTemplate extends XTemplates {
		@XTemplate("<div class='computation-output-outputType'><p>{intro} <b>{produced}<b>.</p></div>")
		SafeHtml getTemplate(String intro, String produced);
	}

	/**
	 * @param string
	 * @return
	 */
	private HtmlLayoutContainer getHtmlTitle(String intro, String produced) {
		TitleTemplate templates = GWT.create(TitleTemplate.class);
		HtmlLayoutContainer c = new HtmlLayoutContainer(templates.getTemplate(
				intro, produced));

		return c;
	}

	/**
	 * @param imagesResource
	 * @return
	 */
	private SimpleContainer getImagesResourceOutput(
			ImageResource imagesResource) {
		return new ImageViewer(computationId, imagesResource);
	}

	/**
	 * 
	 * @param fileResource
	 * @return
	 */
	private SimpleContainer getFileResourceOutput(FileResource fileResource) {
		return new FileViewer(computationId, fileResource);
	}

	/**
	 * @param map
	 * @return
	 */
	private SimpleContainer getMultipleOutput(MapResource mapResource) {
		Map<String, Resource> map = mapResource.getMap();
		VerticalLayoutContainer vp = new VerticalLayoutContainer();
		SimpleContainer container = new SimpleContainer();

		Map<String, ObjectResource> mapValues = new LinkedHashMap<>();
		Map<String, FileResource> mapFiles = new LinkedHashMap<>();
		Map<String, TableResource> mapTabs = new LinkedHashMap<>();
		Map<String, ImageResource> mapImages = new LinkedHashMap<>();

		for (String key : map.keySet()) {
			Resource resource = map.get(key);
			ResourceType resourceType = resource.getResourceType();

			switch (resourceType) {
			case OBJECT:
				mapValues.put(key, (ObjectResource) resource);
				break;
			case FILE:
				mapFiles.put(key, (FileResource) resource);
				break;
			case TABULAR:
				mapTabs.put(key, (TableResource) resource);
				break;
			case IMAGE:
				mapImages.put(key, (ImageResource) resource);
				break;
			case MAP:
				break;
			case ERROR:
				break;
			default:
				break;
			}
		}

		if (mapValues.size() > 0) {
			HtmlLayoutContainer html = new HtmlLayoutContainer("Output Values");
			html.setStyleName("computation-output-groupTitle");
			vp.add(html, new VerticalLayoutData(-1, -1, new Margins(0)));

			vp.add((new ResourceViewer(mapValues)).getHtml(),
					new VerticalLayoutData(1, -1, new Margins(0)));

			html = new HtmlLayoutContainer(
					"<div class='computation-output-separator'></div>");
			vp.add(html, new VerticalLayoutData(-1, -1, new Margins(0)));
		}

		if (mapFiles.size() > 0) {
			HtmlLayoutContainer html = new HtmlLayoutContainer("Files");
			html.setStyleName("computation-output-groupTitle");
			vp.add(html, new VerticalLayoutData(-1, -1, new Margins(0)));

			for (String fileKey : mapFiles.keySet()) {
				// vp.add(new Html("<i>"+fileKey+"</i>"));
				vp.add(getFileResourceOutput(mapFiles.get(fileKey)),
						new VerticalLayoutData(1, -1, new Margins(0)));
			}

			html = new HtmlLayoutContainer(
					"<div class='computation-output-separator'></div>");
			vp.add(html, new VerticalLayoutData(-1, -1, new Margins(0)));
		}

		if (mapTabs.size() > 0) {
			HtmlLayoutContainer html = new HtmlLayoutContainer("Tables");
			html.setStyleName("computation-output-groupTitle");
			vp.add(html, new VerticalLayoutData(-1, -1, new Margins(0)));

			//for (String tabKey : mapTabs.keySet()) {
				// vp.add(new Html("<i>"+tabKey+"</i>"));
				// vp.add(getTabResourceOutput(mapTabs.get(tabKey)));
			//}

			html = new HtmlLayoutContainer(
					"<div class='computation-output-separator'></div>");
			vp.add(html, new VerticalLayoutData(-1, -1, new Margins(0)));
		}

		if (mapImages.size() > 0) {
			HtmlLayoutContainer html = new HtmlLayoutContainer("Images");
			html.setStyleName("computation-output-groupTitle");
			vp.add(html, new VerticalLayoutData(-1, -1, new Margins(0)));

			for (String imagesKey : mapImages.keySet()) {
				vp.add(getImagesResourceOutput(mapImages.get(imagesKey)),
						new VerticalLayoutData(1, -1, new Margins(0)));
			}

			html = new HtmlLayoutContainer(
					"<div class='computation-output-separator'></div>");
			vp.add(html, new VerticalLayoutData(-1, -1, new Margins(0)));
		}

		container.add(vp);
		return container;
	}
}
