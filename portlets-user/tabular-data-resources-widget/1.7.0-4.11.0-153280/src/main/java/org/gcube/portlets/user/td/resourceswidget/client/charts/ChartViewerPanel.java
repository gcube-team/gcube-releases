package org.gcube.portlets.user.td.resourceswidget.client.charts;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.InternalURITD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDDescriptor;
import org.gcube.portlets.user.td.gwtservice.shared.uriresolver.UriResolverSession;
import org.gcube.portlets.user.td.resourceswidget.client.graphics.Vector2D;
import org.gcube.portlets.user.td.resourceswidget.client.properties.ZoomLevelPropertiesCombo;
import org.gcube.portlets.user.td.resourceswidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.resourceswidget.client.store.ZoomLevelElement;
import org.gcube.portlets.user.td.resourceswidget.client.store.ZoomLevelStore;
import org.gcube.portlets.user.td.resourceswidget.client.store.ZoomLevelType;
import org.gcube.portlets.user.td.resourceswidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.mime.MimeTypeSupport;
import org.gcube.portlets.user.td.widgetcommonevent.shared.uriresolver.ApplicationType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.chart.client.draw.DrawComponent;
import com.sencha.gxt.chart.client.draw.Scaling;
import com.sencha.gxt.chart.client.draw.Translation;
import com.sencha.gxt.chart.client.draw.sprite.ImageSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOutEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOutEvent.SpriteOutHandler;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOverEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOverEvent.SpriteOverHandler;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent.SpriteSelectionHandler;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * ResourcesPanel shows the resources
 * 
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ChartViewerPanel extends FramedPanel {
	private static final String COMBO_ZOOM_LEVEL_WIDTH = "100px";
	private static final int IMAGE_WIDTH = 1024;
	private static final int IMAGE_HEIGHT = 768;
	// private static final String RETRIEVE_CHART_FILE_SERVLET =
	// "RetrieveChartFileServlet";
	// private static final String RETRIEVE_FILE_AND_DISCOVER_MIME_TYPE_SERVLET
	// = "RetrieveFileAndDiscoverMimeTypeServlet";
	// private static final String ATTRIBUTE_STORAGE_URI = "storageURI";

	private static final String WIDTH = "810px";
	private static final String HEIGHT = "440px";
	private static final int DRAW_WIDTH_INT = 780;
	private static final int DRAW_HEIGHT_INT = 380;

	private static final int TOOLBAR_HEIGHT = 30;

	private enum Operation {
		ZOOMIN, ZOOMOUT, MOVE
	};

	private Operation activeOperation;
	private Sprite activeSprite;

	private Vector2D startPosition;
	private Vector2D currentPosition;
	private Vector2D endPosition;
	private Vector2D displacement;

	private ChartViewerDialog parent;
	private EventBus eventBus;
	private ResourceTDDescriptor resourceTDDescriptor;
	private boolean test;

	private InternalURITD internalURITD;
	private String chartLink;

	private ImageResource chartImageResource;
	private ImageResource testImageResource;

	private ImageSprite chartSprite;

	private int MAX_SCALE_FACTOR = 32;
	private int MIN_SCALE_FACTOR = 0;
	private double scaleFactor = 0;

	private Vector2D fitScale;
	private Vector2D chartScale;
	private Vector2D chartDimension;

	private DrawComponent paint;

	private ToggleButton btnZoomIn;
	private ToggleButton btnZoomOut;
	private ToggleGroup buttonGroup;

	private Sprite currentSprite;
	private ToggleButton btnMove;
	private Timer mouseTimer;
	private Vector2D chartTranslation;
	private ComboBox<ZoomLevelElement> comboZoomLevel;
	private TextButton btnOpenInWindow;
	private ChartViewerMessages msgs;
	private CommonMessages msgsCommon;

	public ChartViewerPanel(ChartViewerDialog parent,
			ResourceTDDescriptor resourceTDDescriptor, TRId trId,
			EventBus eventBus) {
		this(parent, resourceTDDescriptor, trId, eventBus, false);
	}

	public ChartViewerPanel(ChartViewerDialog parent,
			ResourceTDDescriptor resourceTDDescriptor, TRId trId,
			EventBus eventBus, boolean test) {
		super();
		initMessages();
		ResourceBundle.INSTANCE.resourceCSS().ensureInjected();
		this.eventBus = eventBus;
		this.parent = parent;
		this.resourceTDDescriptor = resourceTDDescriptor;
		this.test = test;
		forceLayoutOnResize = true;
		mask();
		if (test) {
			testImageResource = ResourceBundle.INSTANCE.testImage();
			create();
		} else {
			retrieveChart();
		}

	}

	protected void initMessages() {
		msgs = GWT.create(ChartViewerMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	public static String encodeUrlDelimiters(String s) {
		if (s == null) {
			return null;
		}
		s = s.replaceAll(";", "%2F");
		s = s.replaceAll("/", "%2F");
		s = s.replaceAll(":", "%3A");
		s = s.replaceAll("\\?", "%3F");
		s = s.replaceAll("&", "%26");
		s = s.replaceAll("\\=", "%3D");
		s = s.replaceAll("\\+", "%2B");
		s = s.replaceAll("\\$", "%24");
		s = s.replaceAll(",", "%2C");
		s = s.replaceAll("#", "%23");

		return s;
	}

	protected void retrieveChart() {
		ResourceTD resource = resourceTDDescriptor.getResourceTD();
		if (resource instanceof InternalURITD) {
			internalURITD = (InternalURITD) resource;
		} else {
			UtilsGXT3.alert(msgsCommon.attention(),
					msgs.errorInvalidInternalURI());
			Log.debug("Attention, this resource does not have valid Internal URI!");
			close();
			return;
		}
		Log.debug("InteranlURI: " + internalURITD);
		retrieveUrlFromResolver();

	}

	protected void retrieveUrlFromResolver() {
		String name = resourceTDDescriptor.getName();
		MimeTypeSupport mts = MimeTypeSupport
				.getMimeTypeSupportFromMimeName(internalURITD.getMimeType());
		if (mts != null) {
			name = name + mts.getExtension();
		}

		if (internalURITD.getId() != null
				&& !internalURITD.getId().isEmpty()
				&& (internalURITD.getId().startsWith("http:") || internalURITD
						.getId().startsWith("https:"))) {
			Log.debug("Use direct link: " + internalURITD.getId());
			chartLink = internalURITD.getId();
			createChartImageResource();
		} else {

			UriResolverSession uriResolverSession = new UriResolverSession(
					internalURITD.getId(), ApplicationType.SMP_ID, name,
					internalURITD.getMimeType());
			Log.debug("UriResolverSession: " + uriResolverSession);
			TDGWTServiceAsync.INSTANCE.getUriFromResolver(uriResolverSession,
					new AsyncCallback<String>() {

						public void onFailure(Throwable caught) {
							if (caught instanceof TDGWTSessionExpiredException) {
								eventBus.fireEvent(new SessionExpiredEvent(
										SessionExpiredType.EXPIREDONSERVER));
							} else {
								Log.error("Error with uri resolver: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert(
										msgsCommon.error(),
										msgs.errorRetrievingUriFromResolverFixed()
												+ caught.getLocalizedMessage());
							}
						}

						public void onSuccess(String link) {
							Log.debug("Retrieved link: " + link);
							chartLink = link;
							createChartImageResource();
						}

					});
		}
	}

	protected void createChartImageResource() {

		/*
		 * final String path = GWT.getModuleBaseURL() +
		 * RETRIEVE_FILE_AND_DISCOVER_MIME_TYPE_SERVLET;
		 */

		chartImageResource = new ImageResource() {

			@Override
			public String getName() {
				return "image";
			}

			@Override
			public int getHeight() {
				return IMAGE_HEIGHT;
			}

			@Override
			public int getLeft() {
				return 0;
			}

			@Override
			public SafeUri getSafeUri() {

				SafeUri uri = UriUtils.fromString(chartLink);

				Log.debug("Image uri:" + uri.asString());
				return uri;
			}

			@Override
			public int getTop() {
				return 0;
			}

			@Override
			public String getURL() {
				return this.getSafeUri().asString();
			}

			@Override
			public int getWidth() {
				return IMAGE_WIDTH;
			}

			@Override
			public boolean isAnimated() {
				return false;
			}

		};

		create();

	}

	protected void create() {
		init();
		createPanel();
		unmask();
		drawInit();
	}

	protected void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
		setResize(true);

	}

	protected void createPanel() {
		VerticalLayoutContainer layout = new VerticalLayoutContainer();
		add(layout);

		// ToolBar
		ToolBar toolBar = new ToolBar();

		btnZoomIn = new ToggleButton();
		btnZoomIn.setValue(false);
		btnZoomIn.setIcon(ResourceBundle.INSTANCE.magnifierZoomIn());
		btnZoomIn.setToolTip(msgs.btnZoomInToolTip());
		btnZoomIn.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				chartSprite.setCursor("zoom-in");
				forceLayout();

			}
		});
		toolBar.add(btnZoomIn);

		btnZoomOut = new ToggleButton();
		btnZoomOut.setValue(false);
		btnZoomOut.setIcon(ResourceBundle.INSTANCE.magnifierZoomOut());
		btnZoomOut.setToolTip(msgs.btnZoomOutToolTip());
		btnZoomOut.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				chartSprite.setCursor("zoom-out");
				forceLayout();

			}
		});
		toolBar.add(btnZoomOut);

		btnMove = new ToggleButton();
		btnMove.setValue(false);
		btnMove.setIcon(ResourceBundle.INSTANCE.move());
		btnMove.setToolTip(msgs.btnMoveToolTip());
		btnMove.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				chartSprite.setCursor("move");

			}
		});
		toolBar.add(btnMove);

		buttonGroup = new ToggleGroup();
		buttonGroup.add(btnZoomIn);
		buttonGroup.add(btnZoomOut);
		buttonGroup.add(btnMove);

		buttonGroup
				.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {

					public void onValueChange(
							ValueChangeEvent<HasValue<Boolean>> event) {

						if (btnZoomIn.getValue()) {
							activeOperation = Operation.ZOOMIN;
							return;
						}

						if (btnZoomOut.getValue()) {
							activeOperation = Operation.ZOOMOUT;
							return;
						}

						if (btnMove.getValue()) {
							activeOperation = Operation.MOVE;
							return;
						}

					}
				});

		ZoomLevelPropertiesCombo props = GWT
				.create(ZoomLevelPropertiesCombo.class);
		ListStore<ZoomLevelElement> storeZoomLevelTypes = new ListStore<ZoomLevelElement>(
				props.id());

		storeZoomLevelTypes.addAll(ZoomLevelStore.getZoomLevelTypes());

		Log.trace("Store created");

		comboZoomLevel = new ComboBox<ZoomLevelElement>(storeZoomLevelTypes,
				props.label());

		Log.trace("Combo created");

		addHandlersForComboZoomLevel(props.label());

		comboZoomLevel.setEmptyText(msgs.comboZoomLevelEmptyText());
		comboZoomLevel.setItemId("ComboZoomLevel");
		comboZoomLevel.setWidth(COMBO_ZOOM_LEVEL_WIDTH);
		comboZoomLevel.setEditable(false);
		comboZoomLevel.setTriggerAction(TriggerAction.ALL);

		toolBar.add(comboZoomLevel);
		//
		btnOpenInWindow = new TextButton();
		btnOpenInWindow.setIcon(ResourceBundle.INSTANCE.application());
		btnOpenInWindow.setToolTip(msgs.btnOpenInWindowToolTip());
		btnOpenInWindow.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				openInNewWindow();
			}
		});
		toolBar.add(btnOpenInWindow);

		// Paint
		paint = new DrawComponent();

		paint.setLayoutData(new VerticalLayoutData(1, 1));

		paint.addSpriteSelectionHandler(new SpriteSelectionHandler() {

			@Override
			public void onSpriteSelect(SpriteSelectionEvent event) {
				activeSprite(event);

			}
		});

		paint.addSpriteOverHandler(new SpriteOverHandler() {

			@Override
			public void onSpriteOver(SpriteOverEvent event) {
				spriteOver(event);

			}
		});

		paint.addSpriteOutHandler(new SpriteOutHandler() {

			@Override
			public void onSpriteLeave(SpriteOutEvent event) {
				spriteLeave(event);

			}
		});

		paint.addDomHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				spriteMouseUp(event);

			}
		}, MouseUpEvent.getType());

		paint.addDomHandler(new MouseWheelHandler() {

			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				spriteMouseWheel(event);

			}
		}, MouseWheelEvent.getType());

		layout.add(toolBar, new VerticalLayoutData(1, TOOLBAR_HEIGHT,
				new Margins(0)));

		layout.add(paint, new VerticalLayoutData(1, 1, new Margins(0)));

	}

	protected void openInNewWindow() {
		String link;
		if (test) {
			link = testImageResource.getSafeUri().asString();
		} else {
			link = chartImageResource.getSafeUri().asString();
		}
		Log.debug("Retrieved link: " + link);
		Window.open(link, resourceTDDescriptor.getName(), "");

	}

	protected void drawInit() {

		if (test) {
			chartSprite = new ImageSprite(testImageResource);
		} else {
			chartSprite = new ImageSprite(chartImageResource);
		}

		chartSprite.setX(0);
		chartSprite.setY(0);

		paint.addSprite(chartSprite);

		if (test) {

			Log.debug("testImageResource width:" + testImageResource.getWidth()
					+ " height:" + testImageResource.getHeight());
			fitScale = new Vector2D(new Double(DRAW_WIDTH_INT)
					/ testImageResource.getWidth(), new Double(DRAW_HEIGHT_INT)
					/ testImageResource.getHeight());
			chartDimension = new Vector2D(testImageResource.getWidth(),
					testImageResource.getHeight());

		} else {
			Log.debug("chartImageResource width:"
					+ chartImageResource.getWidth() + " height:"
					+ chartImageResource.getHeight());
			fitScale = new Vector2D(new Double(DRAW_WIDTH_INT)
					/ chartImageResource.getWidth(),
					new Double(DRAW_HEIGHT_INT)
							/ chartImageResource.getHeight());
			chartDimension = new Vector2D(chartImageResource.getWidth(),
					chartImageResource.getHeight());

		}

		Log.debug("Offset width:" + DRAW_WIDTH_INT + " height:"
				+ DRAW_HEIGHT_INT);
		Log.debug("Fit Scale: " + fitScale);
		Log.debug("Chart Dimension: " + chartDimension);
		chartSprite.setScaling(new Scaling(fitScale.getX(), fitScale.getY()));

		chartScale = fitScale.copy();
		chartTranslation = new Vector2D();
		paint.redrawSurfaceForced();

		// Image image = new Image(chartImageResource.getSafeUri());
		// layout.add(image);

	}

	protected void createMouseTimer() {
		mouseTimer = new Timer() {
			@Override
			public void run() {
				if (activeSprite != null) {
					if (activeSprite == chartSprite) {
						actionOnChartSpriteWhenMouseLeftDown();
					}
				}
			}
		};

		// Schedule the timer to run once in 300 milliseconds.
		mouseTimer.scheduleRepeating(200);
		Log.debug("MouseTimer Start");
	}

	protected void activeSprite(SpriteSelectionEvent event) {
		Event browseEvent = event.getBrowserEvent();
		browseEvent.preventDefault();
		Log.debug("Active Position: " + browseEvent.getClientX() + ", "
				+ browseEvent.getClientY());
		activeSprite = event.getSprite();
		startPosition = new Vector2D(browseEvent.getClientX(),
				browseEvent.getClientY());
		if (activeSprite != null) {
			if (activeSprite == chartSprite) {
				if (activeOperation.compareTo(Operation.ZOOMIN) == 0
						|| activeOperation.compareTo(Operation.ZOOMOUT) == 0) {
					actionOnChartSpriteWhenMouseLeftDown();
					comboZoomLevel.reset();
					comboZoomLevel.redraw();
					createMouseTimer();
				}
			}

		}

	}

	protected void spriteMouseUp(MouseUpEvent event) {
		Log.debug("Deactive Position: " + event.getClientX() + ", "
				+ event.getClientY());
		endPosition = new Vector2D(event.getClientX(), event.getClientY());
		displacement = startPosition.sub(endPosition);
		activeSprite = null;
		if (mouseTimer != null) {
			mouseTimer.cancel();
		}
	}

	protected void spriteLeave(SpriteOutEvent event) {
		Event browseEvent = event.getBrowserEvent();
		Log.debug("Leave Position: " + browseEvent.getClientX() + ", "
				+ browseEvent.getClientY());
		activeSprite = null;
		if (mouseTimer != null) {
			mouseTimer.cancel();
		}
	}

	protected void spriteOver(SpriteOverEvent event) {
		Event browseEvent = event.getBrowserEvent();
		Log.debug("Over Position: " + browseEvent.getClientX() + ", "
				+ browseEvent.getClientY());
		currentPosition = new Vector2D(browseEvent.getClientX(),
				browseEvent.getClientY());
		if (activeSprite != null) {
			currentSprite = event.getSprite();
			if (currentSprite == chartSprite) {
				displacement = startPosition.sub(currentPosition);
				Log.debug("Chart Sprite Selected");
				actionOnChartSpriteWhenMove();
			} else {
				Log.debug("No sprite selected");
			}
		} else {

		}

	}

	protected void spriteMouseWheel(MouseWheelEvent event) {
		int deltaY = event.getDeltaY();
		Log.debug("Wheel: " + deltaY);
		if (deltaY < 0) {
			zoomIn();
		} else {
			zoomOut();
		}
		draw();

	}

	protected void actionOnChartSpriteWhenMove() {
		Log.debug("Operation:" + activeOperation);
		switch (activeOperation) {
		case ZOOMIN:
			break;
		case ZOOMOUT:
			break;
		case MOVE:
			moveChart();
			break;
		default:
			break;
		}

		draw();
	}

	protected void actionOnChartSpriteWhenMouseLeftDown() {
		Log.debug("Operation:" + activeOperation);
		switch (activeOperation) {
		case MOVE:
			break;
		case ZOOMIN:
			zoomIn();
			break;
		case ZOOMOUT:
			zoomOut();
			break;
		default:
			break;

		}
		draw();
	}

	protected void zoomIn() {
		if (scaleFactor < MAX_SCALE_FACTOR) {
			scaleFactor++;
		}

		chartScale = fitScale.multiply(1 + scaleFactor / 10);
		chartSprite
				.setScaling(new Scaling(chartScale.getX(), chartScale.getY()));

	}

	protected void zoomOut() {
		if (scaleFactor > MIN_SCALE_FACTOR) {
			scaleFactor--;
		}

		chartScale = fitScale.multiply(1 + scaleFactor / 10);
		chartSprite
				.setScaling(new Scaling(chartScale.getX(), chartScale.getY()));
	}

	private void moveChart() {
		displacement = displacement.divide(1 + scaleFactor);
		displacement = displacement.negate();

		chartTranslation = chartTranslation.add(displacement);

		chartSprite.setTranslation(chartTranslation.getX(),
				chartTranslation.getY());
	}

	protected void draw() {

		paint.redrawSurfaceForced();

	}

	private void addHandlersForComboZoomLevel(
			final LabelProvider<ZoomLevelElement> label) {
		comboZoomLevel
				.addSelectionHandler(new SelectionHandler<ZoomLevelElement>() {
					public void onSelection(
							SelectionEvent<ZoomLevelElement> event) {
						Log.debug("ComboZoomLevel selected: "
								+ event.getSelectedItem());
						ZoomLevelElement zoomLevelElement = event
								.getSelectedItem();
						updateZoomLevel(zoomLevelElement.getType());
					}

				});

	}

	protected void updateZoomLevel(ZoomLevelType type) {
		switch (type) {
		case P50:
			zoomLevel50();
			break;
		case P75:
			zoomLevel75();
			break;
		case P100:
			zoomLevel100();
			break;
		case P200:
			zoomLevel200();
			break;
		case Fit:
			zoomLevelFit();
			break;
		case MaxZoom:
			zoomLevelMax();
			break;
		default:
			break;

		}
		draw();

	}

	protected void zoomLevel50() {
		/*
		 * Log.debug("Paint :" + paint.getAbsoluteLeft() + ", " +
		 * paint.getAbsoluteTop()); Log.debug("Paint offset:" +
		 * paint.getOffsetWidth(true) + ", " + paint.getOffsetHeight(true));
		 * 
		 * Vector2D center = new Vector2D((paint.getOffsetWidth(true) / 2),
		 * (paint.getOffsetHeight(true) / 2)); Log.debug("Center: " + center);
		 * RectangleSprite rectangleSprite = new RectangleSprite(20, 20,
		 * center.getX(), center.getY()); paint.addSprite(rectangleSprite);
		 */

		chartTranslation = new Vector2D();
		scaleFactor = 2;

		chartScale = fitScale.multiply(1 + scaleFactor / 10);

		chartSprite
				.setScaling(new Scaling(chartScale.getX(), chartScale.getY()));
		chartSprite.setTranslation(new Translation(chartTranslation.getX(),
				chartTranslation.getY()));

	}

	protected void zoomLevel75() {

		chartTranslation = new Vector2D();
		scaleFactor = 9;

		chartScale = fitScale.multiply(1 + scaleFactor / 10);

		chartSprite
				.setScaling(new Scaling(chartScale.getX(), chartScale.getY()));
		chartSprite.setTranslation(new Translation(chartTranslation.getX(),
				chartTranslation.getY()));

	}

	protected void zoomLevel100() {

		chartTranslation = new Vector2D();
		scaleFactor = 15;

		chartScale = fitScale.multiply(1 + scaleFactor / 10);

		chartSprite
				.setScaling(new Scaling(chartScale.getX(), chartScale.getY()));
		chartSprite.setTranslation(new Translation(chartTranslation.getX(),
				chartTranslation.getY()));

	}

	protected void zoomLevel200() {

		chartTranslation = new Vector2D();
		scaleFactor = 30;

		chartScale = fitScale.multiply(1 + scaleFactor / 10);

		chartSprite
				.setScaling(new Scaling(chartScale.getX(), chartScale.getY()));
		chartSprite.setTranslation(new Translation(chartTranslation.getX(),
				chartTranslation.getY()));

	}

	protected void zoomLevelFit() {
		chartTranslation = new Vector2D();
		scaleFactor = MIN_SCALE_FACTOR;

		chartScale = fitScale.multiply(1 + scaleFactor / 10);

		chartSprite
				.setScaling(new Scaling(chartScale.getX(), chartScale.getY()));
		chartSprite.setTranslation(chartTranslation.getX(),
				chartTranslation.getY());

	}

	protected void zoomLevelMax() {
		chartTranslation = new Vector2D();
		scaleFactor = MAX_SCALE_FACTOR;

		chartScale = fitScale.multiply(1 + scaleFactor / 10);
		chartSprite
				.setScaling(new Scaling(chartScale.getX(), chartScale.getY()));
		chartSprite.setTranslation(chartTranslation.getX(),
				chartTranslation.getY());

	}

	protected void close() {
		if (parent != null) {
			parent.close();
		}

	}

}
