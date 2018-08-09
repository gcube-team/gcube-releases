package org.gcube.portlets.user.trainingcourse.client.event;

import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;

import com.google.gwt.event.shared.GwtEvent;


// TODO: Auto-generated Javadoc

/**
 * The Class TrainingUnitQuestionnaireEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 25, 2018
 */
public class TrainingUnitQuestionnaireEvent extends GwtEvent<TrainingUnitQuestionnaireEventHandler> {
	
	/** The type. */
	public static Type<TrainingUnitQuestionnaireEventHandler> TYPE = new Type<TrainingUnitQuestionnaireEventHandler>();

	private TrainingUnitDTO unit;

	private QUESTIONNAIRE_EVENT_TYPE eventType;
	
	public static enum QUESTIONNAIRE_EVENT_TYPE {CREATED, ASSOCIATED}

	
	/**
	 * Instantiates a new training unit questionnaire event.
	 *
	 * @param project the project
	 */
	public TrainingUnitQuestionnaireEvent(TrainingUnitDTO unit, QUESTIONNAIRE_EVENT_TYPE type) {
		this.unit = unit;
		this.eventType = type;
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<TrainingUnitQuestionnaireEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(TrainingUnitQuestionnaireEventHandler handler) {
		handler.onQuestionnaireEvent(this);
	}

	public TrainingUnitDTO getUnit() {
		return unit;
	}

	public QUESTIONNAIRE_EVENT_TYPE getEventType() {
		return eventType;
	}

	

}
