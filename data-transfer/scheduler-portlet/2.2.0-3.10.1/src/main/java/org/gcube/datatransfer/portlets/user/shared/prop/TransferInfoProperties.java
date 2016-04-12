package org.gcube.datatransfer.portlets.user.shared.prop;

import java.util.Date;
import java.util.List;

import org.gcube.datatransfer.portlets.user.shared.obj.BaseDto;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;
import org.gcube.datatransfer.portlets.user.shared.obj.TransferInfo;
import org.gcube.datatransfer.portlets.user.shared.obj.TypeOfSchedule;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface TransferInfoProperties extends PropertyAccess<TransferInfo> {

	  @Path("transferId")
	  ModelKeyProvider<TransferInfo> key();
	   
	  @Path("transferId")
	  LabelProvider<TransferInfo> nameLabel();

	ValueProvider<TransferInfo, String> typeOfScheduleString();

	ValueProvider<TransferInfo, String> transferId();

	ValueProvider<TransferInfo, String> submitter();
	ValueProvider<TransferInfo, String> status();
	ValueProvider<TransferInfo, String[]> objectTrasferredIDs();
	ValueProvider<TransferInfo, String[]> objectFailedIDs();
	ValueProvider<TransferInfo, List<String>> transferError();
	ValueProvider<TransferInfo, String> transferIdOfAgent();
	ValueProvider<TransferInfo, String> submittedDate();
	ValueProvider<TransferInfo, Date> submittedDate2();
	ValueProvider<TransferInfo, Double> progress();
	ValueProvider<TransferInfo, Integer> numOfUpdates();
	
}