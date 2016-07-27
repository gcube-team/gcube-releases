package org.gcube.data.spd.asfis.dbconnection;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 *
 */

public class AsfisRecord {
	
	public String three_alpha_code;
	public String name_en;
	public String name_fr;
	public String name_es;
	public String images;
	public String scientific_name;
	public String family;
	public String personal_author;
	public String year;
	public String diagnostic_features;
	public String area_text;
	public String habitat_bio;
	public String interest_fisheries;
	public String local_names;
	public String factsheet_url;
	public String factsheet_id;
	
	public AsfisRecord(ResultSet rs) throws SQLException {
		
		this.three_alpha_code = rs.getString(1);
		this.name_en = rs.getString(2);
		this.name_fr = rs.getString(3);
		this.name_es = rs.getString(4);
		this.images = rs.getString(5);
		this.scientific_name = rs.getString(6);
		this.family = rs.getString(7);
		this.personal_author = rs.getString(8);
		this.year = rs.getString(9);
		this.diagnostic_features = rs.getString(10);
		this.area_text = rs.getString(11);
		this.habitat_bio = rs.getString(12);
		this.interest_fisheries = rs.getString(13);
		this.local_names = rs.getString(14);
		this.factsheet_url = rs.getString(15);
		this.factsheet_id = rs.getString(16);
		
	}

	/**
	 * Returns a three alpha code
	 * @return the three alpha code
	 */
	public String getThree_alpha_cod(){
		return this.three_alpha_code;	
	}
	
	/**
	 * Returns the english name
	 * @return english common name
	 */
	public String getName_en(){
		return this.name_en;	
	}
	
	/**
	 * Returns the french name
	 * @return french common name
	 */
	public String getName_fr(){
		return this.name_fr;	
	}
	
	/**
	 * Returns the spanish name
	 * @return spanish common name
	 */
	public String getName_esd(){
		return this.name_es;	
	}
	
	/**
	 * Returns url images
	 * @return images
	 */
	public String getImages(){
		return this.images;	
	}
	
	/**
	 * Returns scientific name
	 * @return the scientific name
	 */
	public String getScientific_name(){
		return this.scientific_name;	
	}
	
	/**
	 * Returns scientific name
	 * @return the scientific name
	 */
	public String getFamily(){
		return this.family;	
	}
	
	/**
	 * Returns the author
	 * @return the author
	 */
	public String getPersonal_author(){
		return this.personal_author;	
	}
	
	/**
	 * Returns year
	 * @return year
	 */
	public String getYear(){
		return this.year;	
	}
	
	/**
	 * Returns diagnostic_features
	 * @return the diagnostic_features
	 */
	public String getDiagnostic_features(){
		return this.diagnostic_features;	
	}	
	
	/**
	 * Returns area_text
	 * @return the area_text
	 */
	public String getArea_text(){
		return this.area_text;	
	}
	
	/**
	 * Returns habitat_bio
	 * @return habitat_bio
	 */
	public String getHabitat_bio(){
		return this.habitat_bio;	
	}
	
	/**
	 * Returns interest_fisheries
	 * @return interest_fisheries
	 */
	public String getInterest_fisheries(){
		return this.interest_fisheries;	
	}
	
	/**
	 * Returns local_names
	 * @return the local_names
	 */
	public String getLocal_names(){
		return this.local_names;	
	}
	
	/**
	 * Returns factsheet_url
	 * @return the factsheet_url
	 */
	public String getFactsheet_url(){
		return this.factsheet_url;	
	}
	
	/**
	 * Returns factsheet_id
	 * @return the factsheet_id
	 */
	public String getFactsheet_id(){
		return this.factsheet_id;	
	}	
}
