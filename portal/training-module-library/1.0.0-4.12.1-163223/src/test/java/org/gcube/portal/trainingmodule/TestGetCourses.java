package org.gcube.portal.trainingmodule;

import java.util.List;

import org.gcube.portal.trainingmodule.shared.ItemType;
import org.gcube.portal.trainingmodule.shared.TrainingCourseDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitProgressDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitQuestionnaireDTO;
import org.gcube.portal.trainingmodule.shared.TrainingVideoDTO;


// TODO: Auto-generated Javadoc
/**
 * The Class TestGetCourses.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 12, 2018
 */
public class TestGetCourses {
	
	/** The context. */
	//static String context = "/gcube/preprod/Dorne";
	static String context = "/gcube/devsec/devVRE";
	
	/** The owner. */
	static String owner = "francesco.mangiacrapa";
	
	/** The user. */
	static String user = "francesco.mangiacrapa";
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.out.println("Start");
//		getOwnedCourses();
		//getUserCourses();
		//getOwnedCourses();
		//getVideoForUnitId(14, context);
//		getQuestionnaireForUnitId(13, context);
		
//		saveProgressForUnit(22, context, user, "12345", ItemType.FILE, true);
//		getProgressForUnit(22, context, user,"12345",ItemType.FILE);
		//deleteVideoForId(1, context);
		//deleteQuestionnaireForId(4, context);
		
		deleteTrainingUnit(20, context);
		System.out.println("End");
	}
	
	public static int deleteTrainingUnit(long unitId, String context) {
		TrainingModuleManager mng = TrainingModuleManager.getInstance();
		try {

			int deleted = mng.deleteTrainingUnitForId(unitId, context);
			System.out.println("Delete? "+deleted);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
		
	}
	
	
	
	/**
	 * Delete questionnaire for id.
	 *
	 * @param qId the q id
	 * @param context2 the context 2
	 */
	private static void deleteQuestionnaireForId(int qId, String context2) {
		TrainingModuleManager mng = TrainingModuleManager.getInstance();
		
		try {
			mng.deleteQuestionnaireForId(qId, context2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Delete video for id.
	 *
	 * @param videoId the video id
	 * @param context2 the context 2
	 */
	private static void deleteVideoForId(int videoId, String context2) {
		TrainingModuleManager mng = TrainingModuleManager.getInstance();
		
		try {
			mng.deleteVideoForId(videoId, context2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the video for unit id.
	 *
	 * @param trainingUnitId the training unit id
	 * @param context the context
	 * @return the video for unit id
	 */
	private static void getVideoForUnitId(int trainingUnitId, String context) {
		TrainingModuleManager mng = TrainingModuleManager.getInstance();
		
		List<TrainingVideoDTO> videos;
		try {
			videos = mng.getListOfVideoForTrainingUnit(trainingUnitId, context);
		
		
		System.out.println("Found videos: "+videos.size()+ " for unit: "+trainingUnitId);
		for (TrainingVideoDTO trainingVideoDTO : videos) {
			System.out.println(trainingVideoDTO);
		}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Save progress for unit.
	 *
	 * @param unitId the unit id
	 * @param context the context
	 * @param username the username
	 * @param itemId the item id
	 * @param itemType the item type
	 * @param read the read
	 * @return the training unit progress DTO
	 */
	private static TrainingUnitProgressDTO saveProgressForUnit(int unitId, String context, String username, String itemId, ItemType itemType, boolean read) {
		TrainingModuleManager mng = TrainingModuleManager.getInstance();
		
		try {

			return mng.setProgressForUnit(unitId, context, username, itemId, itemType, read);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * Gets the progress for unit.
	 *
	 * @param unitId the unit id
	 * @param context the context
	 * @param username the username
	 * @param itemId the item id
	 * @param itemType the item type
	 * @return the progress for unit
	 */
	private static List<TrainingUnitProgressDTO> getProgressForUnit(int unitId, String context, String username, String itemId, ItemType itemType) {
		TrainingModuleManager mng = TrainingModuleManager.getInstance();
		

		try {

			List<TrainingUnitProgressDTO> progresses = mng.getProgressesForUnit(unitId, context, username, itemId, itemType);
			
			int i = 0;
			for (TrainingUnitProgressDTO trainingUnitProgressDTO : progresses) {
				System.out.println(++i +") "+trainingUnitProgressDTO);
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	/**
	 * Gets the questionnaire for unit id.
	 *
	 * @param trainingUnitId the training unit id
	 * @param context the context
	 * @return the questionnaire for unit id
	 */
	private static void getQuestionnaireForUnitId(int trainingUnitId, String context) {
		TrainingModuleManager mng = TrainingModuleManager.getInstance();
		
		List<TrainingUnitQuestionnaireDTO> trauq;
		try {
			trauq = mng.getListOfQuestionnaireForTrainingUnit(trainingUnitId, context);
		
		
		System.out.println("Found questionnaire: "+trauq.size()+ " for unit: "+trainingUnitId);
		for (TrainingUnitQuestionnaireDTO trainingUnitQuestionnaireDTO : trauq) {
			System.out.println(trainingUnitQuestionnaireDTO);
		}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Gets the user courses.
	 *
	 * @return the user courses
	 */
	private static void getUserCourses() {
		
		TrainingModuleManager mng = TrainingModuleManager.getInstance();
		try {
			List<TrainingCourseDTO> courses = mng.getUserCourses(user, context);
			
			
			System.out.println("\n\nFound "+courses.size()+" course/s using parameters [username: "+user+", context: "+context+"]");
			int i = 0;
			for (TrainingCourseDTO trainingProject : courses) {
				System.out.println(++i +") "+trainingProject.getInternalId()+ ", "+trainingProject.getTitle() +", "+trainingProject.getSharedWith());
				
			}
			
			if(courses==null || courses.size()==0) {
				System.out.println("No Courses!!!!");
				return;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Gets the owned courses.
	 *
	 * @return the owned courses
	 */
	public static void getOwnedCourses() {
		

		TrainingModuleManager mng = TrainingModuleManager.getInstance();
		
	
		try {
			List<TrainingCourseDTO> courses = mng.getOwnedCourses(owner, context);
			
			
			System.out.println("\n\n Found "+courses.size()+" course/s using parameters [username: "+owner+", context: "+context+"]");
			for (TrainingCourseDTO trainingProject : courses) {
				System.out.println(trainingProject);
				
			}
			
			if(courses==null || courses.size()==0) {
				System.out.println("No Courses!!!!");
				return;
			}
				
			
			long projectID = courses.get(0).getInternalId();
			System.out.println("Checking project: "+projectID);
			List<TrainingUnitDTO> listOfUnits = mng.getListOfTrainingUnitFor(courses.get(0).getInternalId(), context);
			System.out.println(listOfUnits);

			//ADD QUESTIONNAIRE
//			if(listOfUnits.size()>0) {
//				
//				TrainingUnitDTO unit = listOfUnits.get(0);
//				System.out.println("Adding questionnaire to unit "+unit);
//				TrainingUnitQuestionnaireDTO questionnaireDTO = new TrainingUnitQuestionnaireDTO("title 2", "description 2", "this is an questionnaireId 2 ", "this is a questionnaireURL 2");
//				TrainingUnitQuestionnaireDTO updateDTO = addTrainingUnitQuestionnaire(unit.getInternalId(), questionnaireDTO, context, owner);
//				System.out.println("updateDTO "+updateDTO);
//				
//			}
//			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		

	}
	
	/**
	 * Adds the training unit questionnaire.
	 *
	 * @param trainingUnitId the training unit id
	 * @param questionnaireDTO the questionnaire DTO
	 * @param context the context
	 * @param owner the owner
	 * @return the training unit questionnaire DTO
	 * @throws Exception the exception
	 */
	public static TrainingUnitQuestionnaireDTO addTrainingUnitQuestionnaire(long trainingUnitId, TrainingUnitQuestionnaireDTO questionnaireDTO, String context, String owner) throws Exception {
		
		TrainingModuleManager mng = TrainingModuleManager.getInstance();
		
		return mng.addQuestionnaireToTrainingUnit(trainingUnitId, questionnaireDTO, context, owner);
	}

}
