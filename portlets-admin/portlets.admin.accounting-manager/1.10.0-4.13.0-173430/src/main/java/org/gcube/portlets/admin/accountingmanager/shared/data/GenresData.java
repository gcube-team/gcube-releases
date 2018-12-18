package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class GenresData implements Serializable {

	private static final long serialVersionUID = 719740085818609829L;
	private String genre;

	public GenresData() {
		super();
	}

	public GenresData(String genre) {
		super();
		this.genre = genre;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getLabel() {
		return genre;
	}

	public void setLabel(String genre) {
		this.genre = genre;
	}

	@Override
	public String toString() {
		return "GenresData [genre=" + genre + "]";
	}

}
