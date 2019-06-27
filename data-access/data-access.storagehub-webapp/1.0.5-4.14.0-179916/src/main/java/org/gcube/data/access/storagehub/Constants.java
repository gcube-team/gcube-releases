package org.gcube.data.access.storagehub;

import java.util.Arrays;
import java.util.List;

public class Constants {

	public static final String VRE_FOLDER_PARENT_NAME = "MySpecialFolders";
	
	public static final String SHARED_FOLDER_PATH = "/Share";
	
	public static final String WORKSPACE_ROOT_FOLDER_NAME ="Workspace";
	
	public static final String TRASH_ROOT_FOLDER_NAME ="Trash";
	
	public static final String QUERY_LANGUAGE ="JCR-SQL2";
	
	public static final String ADMIN_PARAM_NAME ="admin-username";
	
	public static final String ADMIN_PARAM_PWD ="admin-pwd";
	
	public static final List<String> FOLDERS_TO_EXLUDE = Arrays.asList(Constants.VRE_FOLDER_PARENT_NAME, Constants.TRASH_ROOT_FOLDER_NAME);
	
	public static final List<String> WRITE_PROTECTED_FOLDER = Arrays.asList(Constants.VRE_FOLDER_PARENT_NAME, Constants.TRASH_ROOT_FOLDER_NAME);
	
	public static final List<String> PROTECTED_FOLDER = Arrays.asList(Constants.WORKSPACE_ROOT_FOLDER_NAME, Constants.VRE_FOLDER_PARENT_NAME, Constants.TRASH_ROOT_FOLDER_NAME);
}
