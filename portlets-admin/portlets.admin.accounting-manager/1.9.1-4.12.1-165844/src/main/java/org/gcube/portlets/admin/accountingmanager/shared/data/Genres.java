package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class Genres implements Serializable {

	private static final long serialVersionUID = -1704075020370137961L;
	private ArrayList<String> genresList;

	public Genres() {
		super();
	}

	public Genres(ArrayList<String> genresList) {
		super();
		this.genresList = genresList;
	}

	public ArrayList<String> getGenresList() {
		return genresList;
	}

	public void setGenresList(ArrayList<String> genresList) {
		this.genresList = genresList;
	}

	@Override
	public String toString() {
		return "Genres [genresList=" + genresList + "]";
	}

}
