package org.gcube.portlets.user.td.gwtservice.shared.history;

import java.io.Serializable;

/**
 * History step
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class OpHistory implements Serializable {

	private static final long serialVersionUID = 7597236172277678816L;
	Long historyId;
	String name;
	String description;
	String date;

	public OpHistory() {
	}

	public OpHistory(Long historyId, String name, String description,
			String date) {
		this.historyId = historyId;
		this.name = name;
		this.description = description;
		this.date = date;
	}


	public Long getHistoryId() {
		return historyId;
	}

	public void setHistoryId(Long historyId) {
		this.historyId = historyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "OpHistory [historyId=" + historyId + ", name=" + name
				+ ", description=" + description + ", date=" + date + "]";
	}

}
