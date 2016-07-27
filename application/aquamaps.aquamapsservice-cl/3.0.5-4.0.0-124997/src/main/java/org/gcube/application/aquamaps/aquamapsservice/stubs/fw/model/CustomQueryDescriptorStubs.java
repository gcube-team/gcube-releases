package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.CustomQueryDescriptorFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class CustomQueryDescriptorStubs {

	private static Logger logger = LoggerFactory.getLogger(CustomQueryDescriptorStubs.class);
	@XmlElement
	private String user="";
	@XmlElement
	private Long rows=0l;
	@XmlElement
	private FieldArray fields = new FieldArray();
	@XmlElement
	private ExportStatus status=ExportStatus.PENDING;
	@XmlElement
	private String errorMsg="";
	@XmlElement
	private String actualTableName;
	@XmlElement
	private String query="";
	@XmlElement
	private Long creationTime=0l;
	@XmlElement
	private Long lastAccess=0l;
	
	public CustomQueryDescriptorStubs() {
		// TODO Auto-generated constructor stub
	}
	
	
	

	/**
	 * @return the user
	 */
	public String user() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void user(String user) {
		this.user = user;
	}

	/**
	 * @return the rows
	 */
	public Long rows() {
		return rows;
	}

	/**
	 * @param rows the rows to set
	 */
	public void rows(Long rows) {
		this.rows = rows;
	}

	/**
	 * @return the fields
	 */
	public FieldArray fields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void fields(FieldArray fields) {
		this.fields = fields;
	}

	/**
	 * @return the status
	 */
	public ExportStatus status() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void status(ExportStatus status) {
		this.status = status;
	}

	/**
	 * @return the errorMsg
	 */
	public String errorMessage() {
		return errorMsg;
	}

	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void errorMessage(String errorMessage) {
		this.errorMsg = errorMessage;
	}

	/**
	 * @return the actualTableName
	 */
	public String actualTableName() {
		return actualTableName;
	}

	/**
	 * @param actualTableName the actualTableName to set
	 */
	public void actualTableName(String actualTableName) {
		this.actualTableName = actualTableName;
	}

	/**
	 * @return the query
	 */
	public String query() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void query(String query) {
		this.query = query;
	}

	/**
	 * @return the creationTime
	 */
	public Long creationTime() {
		return creationTime;
	}

	/**
	 * @param creationTime the creationTime to set
	 */
	public void creationTime(Long creationTime) {
		this.creationTime = creationTime;
	}

	/**
	 * @return the lastAccess
	 */
	public Long lastAccess() {
		return lastAccess;
	}

	/**
	 * @param lastAccess the lastAccess to set
	 */
	public void lastAccess(Long lastAccess) {
		this.lastAccess = lastAccess;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CustomQueryDescriptorStubs [user=");
		builder.append(user);
		builder.append(", rows=");
		builder.append(rows);
		builder.append(", fields=");
		builder.append(fields);
		builder.append(", status=");
		builder.append(status);
		builder.append(", errorMsg=");
		builder.append(errorMsg);
		builder.append(", actualTableName=");
		builder.append(actualTableName);
		builder.append(", query=");
		builder.append(query);
		builder.append(", creationTime=");
		builder.append(creationTime);
		builder.append(", lastAccess=");
		builder.append(lastAccess);
		builder.append("]");
		return builder.toString();
	}
	public static ArrayList<CustomQueryDescriptorStubs> loadResultSet(ResultSet rs)throws Exception{
		ArrayList<CustomQueryDescriptorStubs> toReturn= new ArrayList<CustomQueryDescriptorStubs>();
		while(rs.next()){
			toReturn.add(new CustomQueryDescriptorStubs(Field.loadRow(rs)));
		}
		return toReturn;
	}
	
	public CustomQueryDescriptorStubs(ResultSet rs)throws Exception{
		this(Field.loadRow(rs));
	}
	public CustomQueryDescriptorStubs(List<Field> row){
		for(Field f: row)	
			try{
				this.setField(f);
			}catch(Exception e){
				//skips wrong fields
			}
	}
	
	
	public boolean setField(Field f) throws JSONException{
		try{
		switch(CustomQueryDescriptorFields.valueOf(f.name().toLowerCase())){
		case count : rows(f.getValueAsLong());
		break;
		case creationtime: creationTime(f.getValueAsLong());
		break;
		case errors: errorMessage(f.value());
		break;
		case fields : fields(((FieldArray) AquaMapsXStream.getXMLInstance().fromXML(f.value())));
		break;
		case lastaccess : lastAccess(f.getValueAsLong());
		break;
		case query : query(f.value());
		break;
		case resulttable : actualTableName(f.value());
		break;
		case status : status(ExportStatus.valueOf(f.value()));
		break;
		case userid : user(f.value());
		default : return false;
		}
	}catch(Exception e){logger.warn("Unable to parse field "+f.toJSONObject(),e);}
		return true;
	}
	
	
	public Field getField(CustomQueryDescriptorFields fieldName){
		switch(fieldName){
		case count : return new Field(fieldName+"",rows()+"",FieldType.LONG);
		case creationtime : return new Field(fieldName+"",creationTime()+"",FieldType.LONG);
		case errors : return new Field(fieldName+"",errorMessage(),FieldType.STRING);
		case fields: return new Field(fieldName+"",AquaMapsXStream.getXMLInstance().toXML(fields()),FieldType.STRING);
		case lastaccess: return new Field(fieldName+"",lastAccess()+"",FieldType.LONG);
		case query: return new Field(fieldName+"",query(),FieldType.STRING);
		case resulttable: return new Field(fieldName+"",actualTableName(),FieldType.STRING);
		case status: return new Field(fieldName+"",status()+"",FieldType.STRING);
		case userid : return new Field(fieldName+"",user(),FieldType.STRING);
		default : return null;
		}
	}
	
	
	public List<Field> toRow() throws JSONException{
		List<Field> toReturn= new ArrayList<Field>();
		for(CustomQueryDescriptorFields f : CustomQueryDescriptorFields.values())
			toReturn.add(getField(f));
		return toReturn;
	}
	
}
