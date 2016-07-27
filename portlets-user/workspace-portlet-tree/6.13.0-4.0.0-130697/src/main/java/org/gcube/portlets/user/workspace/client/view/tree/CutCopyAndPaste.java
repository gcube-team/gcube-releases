package org.gcube.portlets.user.workspace.client.view.tree;

import java.util.List;


public class CutCopyAndPaste {
	
	private static List<String> idsFilesModel = null;
	
	public enum OperationType {CUT, COPY};
	
	private static OperationType operationType;
	
	public static void copy(List<String> idsFileModel, OperationType operationType){	
		CutCopyAndPaste.idsFilesModel = idsFileModel;
		CutCopyAndPaste.operationType = operationType;
	}

	public static List<String> getCopiedIdsFilesModel() {
		return idsFilesModel;
	}

	public static void setCopiedIdsFileModels(List<String> idsFileModel) {
		CutCopyAndPaste.idsFilesModel = idsFileModel;
	}

	public static OperationType getOperationType() {
		return operationType;
	}

	public static void setOperationType(OperationType operationType) {
		CutCopyAndPaste.operationType = operationType;
	}
	
}
