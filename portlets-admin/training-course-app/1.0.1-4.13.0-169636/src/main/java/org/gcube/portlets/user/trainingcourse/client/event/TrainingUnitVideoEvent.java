package org.gcube.portlets.user.trainingcourse.client.event;

import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;

import com.google.gwt.event.shared.GwtEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class TrainingUnitVideoEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Feb 1, 2018
 */
public class TrainingUnitVideoEvent extends GwtEvent<TrainingUnitVideoEventHandler> {

	/** The type. */
	public static Type<TrainingUnitVideoEventHandler> TYPE = new Type<TrainingUnitVideoEventHandler>();

	/** The unit. */
	private TrainingUnitDTO unit;

	/** The event type. */
	private VIDEO_EVENT_TYPE eventType;

	/**
	 * The Enum QUESTIONNAIRE_EVENT_TYPE.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Feb 1, 2018
	 */
	public static enum VIDEO_EVENT_TYPE {
		CREATED, ASSOCIATED
	}

	/**
	 * Instantiates a new training unit questionnaire event.
	 *
	 * @param unit
	 *            the unit
	 * @param type
	 *            the type
	 */
	public TrainingUnitVideoEvent(TrainingUnitDTO unit, VIDEO_EVENT_TYPE type) {
		this.unit = unit;
		this.eventType = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<TrainingUnitVideoEventHandler> getAssociatedType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.
	 * EventHandler)
	 */
	@Override
	protected void dispatch(TrainingUnitVideoEventHandler handler) {
		handler.onVideoEvent(this);
	}

	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public TrainingUnitDTO getUnit() {
		return unit;
	}

	/**
	 * Gets the event type.
	 *
	 * @return the event type
	 */
	public VIDEO_EVENT_TYPE getEventType() {
		return eventType;
	}

}
