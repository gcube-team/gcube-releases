package org.gcube.portlets.admin.accountingmanager.client.carousel;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.client.graphics.Vector2D;
import org.gcube.portlets.admin.accountingmanager.client.resource.AccountingManagerResources;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.sencha.gxt.chart.client.draw.DrawComponent;
import com.sencha.gxt.chart.client.draw.sprite.ImageSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOutEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOutEvent.SpriteOutHandler;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOverEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOverEvent.SpriteOverHandler;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent.SpriteSelectionHandler;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class CarouselPanel extends FramedPanel {
	private static final int WIDTH = 1024;
	private static final int HEIGHT = 768;
	private DrawComponent paint;
	private ArrayList<ImageSprite> sprites;
	private Timer mouseTimer;
	private Sprite currentSprite;
	private Sprite activeSprite;

	private Vector2D startPosition;
	private Vector2D currentPosition;
	private Vector2D endPosition;
    @SuppressWarnings("unused")
	private Vector2D displacement;

	public CarouselPanel() {
		init();
		create();
		unmask();
		drawInit();
	}

	private void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
		setResize(true);

	}

	private void create() {
		VerticalLayoutContainer layout = new VerticalLayoutContainer();
		add(layout);

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

		layout.add(paint, new VerticalLayoutData(1, 1, new Margins(0)));

	}

	protected void drawInit() {

		addSprites();

		// chartSprite.setX(0);
		// chartSprite.setY(0);

		/*
		 * Log.debug("chartImageResource width:" + chartImageResource.getWidth()
		 * + " height:" + chartImageResource.getHeight()); fitScale = new
		 * Vector2D(new Double(DRAW_WIDTH_INT) / chartImageResource.getWidth(),
		 * new Double(DRAW_HEIGHT_INT) / chartImageResource.getHeight());
		 * chartDimension = new Vector2D(chartImageResource.getWidth(),
		 * chartImageResource.getHeight());
		 * 
		 * Log.debug("Offset width:" + DRAW_WIDTH_INT + " height:" +
		 * DRAW_HEIGHT_INT); Log.debug("Fit Scale: " + fitScale);
		 * Log.debug("Chart Dimension: " + chartDimension);
		 * chartSprite.setScaling(new Scaling(fitScale.getX(),
		 * fitScale.getY()));
		 * 
		 * chartScale = fitScale.copy(); chartTranslation = new Vector2D();
		 */
		paint.redrawSurfaceForced();

	}

	private void addSprites() {
		sprites = new ArrayList<ImageSprite>();
		sprites.add(new ImageSprite(AccountingManagerResources.INSTANCE
				.accountingStorage128()));

		for (ImageSprite sprite : sprites) {
			paint.addSprite(sprite);
		}

	}

	protected void createMouseTimer() {
		mouseTimer = new Timer() {
			@Override
			public void run() {
				// if (activeSprite != null) {
				// if (activeSprite == chartSprite) {
				// actionOnChartSpriteWhenMouseLeftDown();
				// }
				// }
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
			for (ImageSprite sprite : sprites) {
				if (activeSprite == sprite) {
					/*
					 * if (activeOperation.compareTo(Operation.ZOOMIN) == 0 ||
					 * activeOperation.compareTo(Operation.ZOOMOUT) == 0) {
					 * actionOnChartSpriteWhenMouseLeftDown();
					 * comboZoomLevel.reset(); comboZoomLevel.redraw();
					 * createMouseTimer(); }
					 */
					break;
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
			for (ImageSprite sprite : sprites) {
				if (currentSprite == sprite) {
					displacement = startPosition.sub(currentPosition);
					Log.debug("Chart Sprite Selected");
					actionOnChartSpriteWhenMove();
					break;
				}
			}
			
		} else {

		}

	}

	protected void spriteMouseWheel(MouseWheelEvent event) {
		int deltaY = event.getDeltaY();
		Log.debug("Wheel: " + deltaY);
		/*
		 * if (deltaY < 0) { zoomIn(); } else { zoomOut(); }
		 */
		draw();

	}

	protected void actionOnChartSpriteWhenMove() {
		/*
		 * Log.debug("Operation:" + activeOperation); switch (activeOperation) {
		 * case ZOOMIN: break; case ZOOMOUT: break; case MOVE: moveChart();
		 * break; default: break; }
		 */
		draw();
	}

	protected void actionOnChartSpriteWhenMouseLeftDown() {
		/*
		 * Log.debug("Operation:" + activeOperation); switch (activeOperation) {
		 * case MOVE: break; case ZOOMIN: zoomIn(); break; case ZOOMOUT:
		 * zoomOut(); break; default: break;
		 * 
		 * }
		 */
		draw();
	}

	protected void draw() {

		paint.redrawSurfaceForced();

	}
}
