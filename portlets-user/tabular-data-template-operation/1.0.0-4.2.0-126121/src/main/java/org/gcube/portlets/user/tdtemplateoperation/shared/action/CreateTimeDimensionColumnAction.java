/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.shared.action;

import java.io.Serializable;
import java.util.Arrays;

import org.gcube.portlets.user.tdtemplateoperation.shared.CreateTimeDimensionOptions;
import org.gcube.portlets.user.tdtemplateoperation.shared.ServerObjectId;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;

/**
 * The Class CreateTimeDimensionColumnAction.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 9, 2014
 */
public class CreateTimeDimensionColumnAction implements TabularDataAction, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4553301976630440150L;

	/**
	 * 
	 */
	public static final String CREATE_TIME_DIMENSION = "Create Time Column";
	
	private ServerObjectId serverObjectId;

	private CreateTimeDimensionOptions option;

	private TdColumnData[] columns;
	
	/**
	 * 
	 */
	public CreateTimeDimensionColumnAction() {
	}

	/**
	 * Instantiates a new creates the time dimension column action.
	 * @param option 
	 * @param yearPeriod 
	 * @param tdColumnData 
	 */
	public CreateTimeDimensionColumnAction(CreateTimeDimensionOptions option, TdColumnData... tdColumnData) {
		this.columns = tdColumnData;
		this.option = option;
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction#getId()
	 */
	@Override
	public String getId() {
		return CreateTimeDimensionColumnAction.class.getSimpleName();
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction#getDescription()
	 */
	@Override
	public String getDescription() {
		return CREATE_TIME_DIMENSION;
	}



	/**
	 * @return the serverObjectId
	 */
	public ServerObjectId getServerObjectId() {
		return serverObjectId;
	}



	/**
	 * @return the option
	 */
	public CreateTimeDimensionOptions getOption() {
		return option;
	}



	/**
	 * @return the columns
	 */
	public TdColumnData[] getColumns() {
		return columns;
	}



	/**
	 * @param serverObjectId the serverObjectId to set
	 */
	public void setServerObjectId(ServerObjectId serverObjectId) {
		this.serverObjectId = serverObjectId;
	}



	/**
	 * @param option the option to set
	 */
	public void setOption(CreateTimeDimensionOptions option) {
		this.option = option;
	}



	/**
	 * @param columns the columns to set
	 */
	public void setColumns(TdColumnData[] columns) {
		this.columns = columns;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CreateTimeDimensionColumnAction [serverObjectId=");
		builder.append(serverObjectId);
		builder.append(", option=");
		builder.append(option);
		builder.append(", columns=");
		builder.append(Arrays.toString(columns));
		builder.append("]");
		return builder.toString();
	}
	
	
}
