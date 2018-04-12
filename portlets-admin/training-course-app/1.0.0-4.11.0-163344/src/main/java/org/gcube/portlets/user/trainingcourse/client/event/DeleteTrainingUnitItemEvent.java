package org.gcube.portlets.user.trainingcourse.client.event;

import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitQuestionnaireDTO;
import org.gcube.portal.trainingmodule.shared.TrainingVideoDTO;

import com.google.gwt.event.shared.GwtEvent;


// TODO: Auto-generated Javadoc
/**
 * The Class DeleteTrainingUnitItemEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 13, 2018
 */
public class DeleteTrainingUnitItemEvent extends GwtEvent<DeleteTrainingUnitItemEventHandler> {
	
	/** The type. */
	public static Type<DeleteTrainingUnitItemEventHandler> TYPE = new Type<DeleteTrainingUnitItemEventHandler>();
	
	/** The video. */
	private TrainingVideoDTO video;

	/** The questionnaire. */
	private TrainingUnitQuestionnaireDTO questionnaire;

	/** The unit. */
	private TrainingUnitDTO unit;

	
	/**
	 * Instantiates a new delete workspace item event.
	 *
	 * @param unit the unit
	 * @param video the video
	 * @param questionnaire the questionnaire
	 */
	public DeleteTrainingUnitItemEvent(TrainingUnitDTO unit, TrainingVideoDTO video, TrainingUnitQuestionnaireDTO questionnaire) {
		this.unit = unit;
		this.video = video;
		this.questionnaire = questionnaire;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<DeleteTrainingUnitItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(DeleteTrainingUnitItemEventHandler handler) {
		handler.onRemoveTrainingUnitItem(this);
	}
	
	/**
	 * Gets the video.
	 *
	 * @return the video
	 */
	public TrainingVideoDTO getVideo() {
		return video;
	}
	
	/**
	 * Gets the questionnaire.
	 *
	 * @return the questionnaire
	 */
	public TrainingUnitQuestionnaireDTO getQuestionnaire() {
		return questionnaire;
	}
	
	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public TrainingUnitDTO getUnit() {
		return unit;
	}

}
