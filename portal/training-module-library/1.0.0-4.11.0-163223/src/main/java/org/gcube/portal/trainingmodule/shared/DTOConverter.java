package org.gcube.portal.trainingmodule.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.gcube.portal.trainingmodule.dao.ProgressPerUnit;
import org.gcube.portal.trainingmodule.dao.TrainingProject;
import org.gcube.portal.trainingmodule.dao.TrainingUnit;
import org.gcube.portal.trainingmodule.dao.TrainingUnitQuestionnaire;
import org.gcube.portal.trainingmodule.dao.TrainingVideo;

// TODO: Auto-generated Javadoc
/**
 * The Class DTOConverter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 22, 2018
 */
public class DTOConverter {
	
	/** The Constant USERNAME_SEPARATOR. */
	public static final String USERNAME_SEPARATOR = ";";
	
	/**
	 * Gets the shared with.
	 *
	 * @param sharedWith the shared with
	 * @return the shared with
	 */
	public static List<String> fromSharedWith(String sharedWith) {
		
		if(sharedWith==null)
			return null;
		
		String[] out = StringUtils.splitByWholeSeparator(sharedWith, USERNAME_SEPARATOR);
		if(out!=null) {
			return Arrays.asList(out);
		}
		return null;
			
	}

	/**
	 * Sets the shared with.
	 *
	 * @param listOfUsername the list of username
	 * @return the string
	 */
	public static String toSharedWith(List<String> listOfUsername) {
		if(listOfUsername!=null && listOfUsername.size()>0)
			return  StringUtils.join(listOfUsername, USERNAME_SEPARATOR);
		
		return null;
	}
	

	/**
	 * Adds the username to share with.
	 *
	 * @param sharedWith the shared with
	 * @param listOfUsername the list of username
	 * @return the string
	 */
	public static String addUsernameToShareWith(String sharedWith, List<String> listOfUsername) {
		
		if(listOfUsername==null || listOfUsername.size()==0)
			return sharedWith;
		
		List<String> shared = fromSharedWith(sharedWith);
		if(shared==null)
			shared = new ArrayList<String>();
		
		List<String> newShared = new ArrayList<String>();
		for (String login : listOfUsername) {
			if(!shared.contains(login)) {
				newShared.add(login);
			}
		}
		
		newShared.addAll(shared);
		return toSharedWith(newShared);
	}
	

	
	/**
	 * From list of project.
	 *
	 * @param listPrj the list prj
	 * @return the list
	 */
	public static List<TrainingCourseDTO> fromListOfProject(List<TrainingProject> listPrj){
		
		if(listPrj==null)
			return null;
		
		List<TrainingCourseDTO> dto = new ArrayList<>(listPrj.size());
		for (TrainingProject trainingProject : listPrj) {
			dto.add(fromTrainingProject(trainingProject));
		}
		
		return dto;
		
	}

	
	/**
	 * From list of unit.
	 *
	 * @param listUnit the list unit
	 * @return the list
	 */
	public static List<TrainingUnitDTO> fromListOfUnit(List<TrainingUnit> listUnit){
		
		if(listUnit==null)
			return null;
		
		List<TrainingUnitDTO> dto = new ArrayList<>(listUnit.size());
		for (TrainingUnit tu : listUnit) {
			dto.add(fromTrainingUnit(tu));
		}
		
		return dto;
		
	}

	
	/**
	 * From training project.
	 *
	 * @param project the project
	 * @return the training project DTO
	 */
	public static TrainingCourseDTO fromTrainingProject(TrainingProject project) {
		
		if(project==null)
			return null;
		
		return new TrainingCourseDTO(
				project.getInternalId(), 
				project.getTitle(), 
				project.getDescription(), 
				project.getCommitment(), 
				project.getLanguages(), 
				project.getScope(), 
				project.getOwnerLogin(), 
				project.getWorkspaceFolderId(), 
				project.getWorkspaceFolderName(), 
				project.getCreatedBy(), 
				project.isCourseActive(), fromSharedWith(project.getSharedWith()));
		
	}
	
	
	/**
	 * From training unit.
	 *
	 * @param unit the unit
	 * @return the training unit DTO
	 */
	public static TrainingUnitDTO fromTrainingUnit(TrainingUnit unit) {
	
		if(unit==null)
			return null;
		
		return new TrainingUnitDTO(
				unit.getInternalId(), 
				unit.getTitle(), 
				unit.getWorkspaceFolderName(), 
				unit.getDescription(), 
				unit.getWorkspaceFolderId(), 
				unit.getScope(), 
				unit.getOwnerLogin(), 
				fromTrainingProject(unit.getTrainingProjectRef()));
	}
	
	/**
	 * From training unit.
	 *
	 * @param unit the unit
	 * @return the training unit DTO
	 */
	public static TrainingUnit toTrainingUnit(TrainingUnitDTO unit) {
	
		if(unit==null)
			return null;
		
		return new TrainingUnit(
				unit.getInternalId(), 
				unit.getTitle(), 
				unit.getWorkspaceFolderName(), 
				unit.getDescription(), 
				unit.getWorkspaceFolderId(), 
				unit.getScope(), 
				unit.getOwnerLogin(), 
				toTrainingProject(unit.getTrainingProjectRef()));
	}
	
	
	/**
	 * To progress unit.
	 *
	 * @param dto the dto
	 * @return the progress per unit
	 */
	public static ProgressPerUnit toProgressUnit(TrainingUnitProgressDTO dto){
		
		if(dto==null)
			return null;
		
		return new ProgressPerUnit(dto.getInternalId(), dto.getUnitId(), dto.getUsername(), dto.getType(), dto.getItemId(), dto.isRead());
		
	}
	
	
	/**
	 * From progress unit.
	 *
	 * @param dao the dao
	 * @return the training unit progress DTO
	 */
	public static TrainingUnitProgressDTO fromProgressUnit(ProgressPerUnit dao){
		if(dao==null)
			return null;
		
		return new TrainingUnitProgressDTO(dao.getInternalId(), dao.getUnitId(), dao.getUsername(), dao.getType(), dao.getItemId(), dao.isRead());
	}
	
	
	/**
	 * To training project.
	 *
	 * @param project the project
	 * @return the training project
	 */
	public static TrainingProject toTrainingProject(TrainingCourseDTO project){
		
		if(project==null)
			return null;
		
		return new TrainingProject(
				project.getInternalId(), 
				project.getTitle(), 
				project.getDescription(), 
				project.getCommitment(), 
				project.getLanguages(), 
				project.getScope(), 
				project.getOwnerLogin(), 
				project.getWorkspaceFolderId(), 
				project.getWorkspaceFolderName(), 
				project.getCreatedBy(), 
				project.isCourseActive(), toSharedWith(project.getSharedWith()));
		
	}
	
	
	/**
	 * To training unit questionnaire.
	 *
	 * @param questDTO the quest DTO
	 * @return the training unit questionnaire
	 */
	public static TrainingUnitQuestionnaire toTrainingUnitQuestionnaire(TrainingUnitQuestionnaireDTO questDTO) {
		
		if(questDTO==null)
			return null;
		
		return new TrainingUnitQuestionnaire(questDTO.getInternalId(), questDTO.getTitle(), questDTO.getDescription(), questDTO.getQuestionnaireId(), questDTO.getQuestionnaireURL());
	}
	
	
	/**
	 * To training unit questionnaire.
	 *
	 * @param quest the quest
	 * @return the training unit questionnaire
	 */
	public static TrainingUnitQuestionnaireDTO fromTrainingUnitQuestionnaire(TrainingUnitQuestionnaire quest) {
		
		if(quest==null)
			return null;
		
		return new TrainingUnitQuestionnaireDTO(quest.getInternalId(), quest.getTitle(), quest.getDescription(), quest.getQuestionnaireId(), quest.getQuestionnaireURL());
	}

	
	/**
	 * To training video.
	 *
	 * @param videoDTO the video DTO
	 * @return the training video
	 */
	public static TrainingVideo toTrainingVideo(TrainingVideoDTO videoDTO) {
		if(videoDTO==null)
			return null;
		
		return new TrainingVideo(videoDTO.getInternalId(), videoDTO.getTitle(), videoDTO.getDescription(), videoDTO.getUrl());
	}
	
	

	/**
	 * From training video.
	 *
	 * @param video the video
	 * @return the training video DTO
	 */
	public static TrainingVideoDTO fromTrainingVideo(TrainingVideo video) {
		if(video==null)
			return null;
		
		return new TrainingVideoDTO(video.getInternalId(), video.getTitle(), video.getDescription(), video.getUrl());
	}


}
