package org.gcube.application.aquamaps.aquamapsspeciesview.servlet.db;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceStatus;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.SpeciesFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeciesCSVProcessor implements CSVLineProcessor {

	private static final Logger logger = LoggerFactory.getLogger(SpeciesCSVProcessor.class);	

	boolean continueProcess=true;
	DBSession session=null;
	int[] modelCSVFieldsMapping=new int[DBUtil.speciesFields.length];
	public ResourceStatus status=ResourceStatus.Completed;

	private HashMap<SpeciesFields,Integer> taxoindexes=new HashMap<SpeciesFields, Integer>();



//	PreparedStatement psInsert=null;
//	PreparedStatement psUpdate=null;
	HashMap<SpeciesOccursumFields,PreparedStatement> taxonomyInsert=new HashMap<SpeciesOccursumFields, PreparedStatement>();
	HashMap<SpeciesOccursumFields,PreparedStatement> taxonomyUpdate=new HashMap<SpeciesOccursumFields, PreparedStatement>();


	ArrayList<Field> kingdomInsertFields=new ArrayList<Field>();
	ArrayList<Field> phylumInsertFields=new ArrayList<Field>();
	ArrayList<Field> classInsertFields=new ArrayList<Field>();
	ArrayList<Field> orderInsertFields=new ArrayList<Field>();
	ArrayList<Field> familyInsertFields=new ArrayList<Field>();


	ArrayList<Field> phylumKey=new ArrayList<Field>();
	ArrayList<Field> phylumUpdateFields=new ArrayList<Field>();
	ArrayList<Field> classKey=new ArrayList<Field>();
	ArrayList<Field> classUpdateFields=new ArrayList<Field>();
	ArrayList<Field> orderKey=new ArrayList<Field>();
	ArrayList<Field> orderUpdateFields=new ArrayList<Field>();
	ArrayList<Field> familyKey=new ArrayList<Field>();
	ArrayList<Field> familyUpdateFields=new ArrayList<Field>();


	public SpeciesCSVProcessor(DBSession session) throws Exception{
//		psInsert=session.preparedStatement(speciesInsertQuery());
//		psUpdate=session.preparedStatement(speciesUpdateQuery());



		kingdomInsertFields.add(new Field(SpeciesOccursumFields.kingdom+"","",FieldType.STRING));
		taxonomyInsert.put(SpeciesOccursumFields.kingdom, session.getPreparedStatementForInsert(kingdomInsertFields, Tables.kingdom+""));

		phylumInsertFields.add(new Field(SpeciesOccursumFields.kingdom+"","",FieldType.STRING));
		phylumInsertFields.add(new Field(SpeciesOccursumFields.phylum+"","",FieldType.STRING));
		taxonomyInsert.put(SpeciesOccursumFields.phylum, session.getPreparedStatementForInsert(phylumInsertFields, Tables.phylum+""));


		classInsertFields.add(new Field(SpeciesOccursumFields.kingdom+"","",FieldType.STRING));
		classInsertFields.add(new Field(SpeciesOccursumFields.phylum+"","",FieldType.STRING));
		classInsertFields.add(new Field(SpeciesOccursumFields.classcolumn+"","",FieldType.STRING));
		taxonomyInsert.put(SpeciesOccursumFields.classcolumn, session.getPreparedStatementForInsert(classInsertFields, Tables.class_table+""));

		orderInsertFields.add(new Field(SpeciesOccursumFields.kingdom+"","",FieldType.STRING));
		orderInsertFields.add(new Field(SpeciesOccursumFields.phylum+"","",FieldType.STRING));
		orderInsertFields.add(new Field(SpeciesOccursumFields.classcolumn+"","",FieldType.STRING));
		orderInsertFields.add(new Field(SpeciesOccursumFields.ordercolumn+"","",FieldType.STRING));
		taxonomyInsert.put(SpeciesOccursumFields.ordercolumn, session.getPreparedStatementForInsert(orderInsertFields, Tables.order_table+""));


		familyInsertFields.add(new Field(SpeciesOccursumFields.kingdom+"","",FieldType.STRING));
		familyInsertFields.add(new Field(SpeciesOccursumFields.phylum+"","",FieldType.STRING));
		familyInsertFields.add(new Field(SpeciesOccursumFields.classcolumn+"","",FieldType.STRING));
		familyInsertFields.add(new Field(SpeciesOccursumFields.ordercolumn+"","",FieldType.STRING));
		familyInsertFields.add(new Field(SpeciesOccursumFields.familycolumn+"","",FieldType.STRING));
		taxonomyInsert.put(SpeciesOccursumFields.familycolumn, session.getPreparedStatementForInsert(familyInsertFields, Tables.family_table+""));




		phylumKey.add(new Field(SpeciesOccursumFields.phylum+"","",FieldType.STRING));
		phylumUpdateFields.add(new Field(SpeciesOccursumFields.kingdom+"","",FieldType.STRING));
		taxonomyUpdate.put(SpeciesOccursumFields.phylum, session.getPreparedStatementForUpdate(phylumUpdateFields, phylumKey, Tables.phylum+""));

		classKey.add(new Field(SpeciesOccursumFields.classcolumn+"","",FieldType.STRING));
		classUpdateFields.add(new Field(SpeciesOccursumFields.phylum+"","",FieldType.STRING));
		classUpdateFields.add(new Field(SpeciesOccursumFields.kingdom+"","",FieldType.STRING));
		taxonomyUpdate.put(SpeciesOccursumFields.classcolumn, session.getPreparedStatementForUpdate(classUpdateFields, classKey, Tables.class_table+""));


		orderKey.add(new Field(SpeciesOccursumFields.ordercolumn+"","",FieldType.STRING));
		orderUpdateFields.add(new Field(SpeciesOccursumFields.phylum+"","",FieldType.STRING));
		orderUpdateFields.add(new Field(SpeciesOccursumFields.kingdom+"","",FieldType.STRING));
		orderUpdateFields.add(new Field(SpeciesOccursumFields.classcolumn+"","",FieldType.STRING));
		taxonomyUpdate.put(SpeciesOccursumFields.ordercolumn, session.getPreparedStatementForUpdate(orderUpdateFields, orderKey, Tables.order_table+""));

		familyKey.add(new Field(SpeciesOccursumFields.familycolumn+"","",FieldType.STRING));
		familyUpdateFields.add(new Field(SpeciesOccursumFields.phylum+"","",FieldType.STRING));
		familyUpdateFields.add(new Field(SpeciesOccursumFields.kingdom+"","",FieldType.STRING));
		familyUpdateFields.add(new Field(SpeciesOccursumFields.classcolumn+"","",FieldType.STRING));
		familyUpdateFields.add(new Field(SpeciesOccursumFields.ordercolumn+"","",FieldType.STRING));
		taxonomyUpdate.put(SpeciesOccursumFields.familycolumn, session.getPreparedStatementForUpdate(familyUpdateFields, familyKey, Tables.family_table+""));
		this.session=session;
	}



	@Override
	public boolean continueProcessing() {return continueProcess;}

	@Override
	public void processDataLine(int arg0, List<String> arg1) {
		try{
			//*************************************    TAXONOMY
			String kingdomValue=arg1.get(taxoindexes.get(SpeciesFields.kingdom));
			String phylumValue=arg1.get(taxoindexes.get(SpeciesFields.phylum));
			String classValue=arg1.get(taxoindexes.get(SpeciesFields.classcolumn));
			String orderValue=arg1.get(taxoindexes.get(SpeciesFields.ordercolumn));
			String familyValue=arg1.get(taxoindexes.get(SpeciesFields.familycolumn));

			try{
				kingdomInsertFields.get(0).value(kingdomValue);
				session.fillParameters(kingdomInsertFields,0, taxonomyInsert.get(SpeciesOccursumFields.kingdom)).executeUpdate();
			}catch(Exception e){
				//********* NO NEED TO UPDATE EXISTING KINGDOM
			}

			// PHYLUM
			phylumKey.get(0).value(phylumValue);
			phylumUpdateFields.get(0).value(kingdomValue);
			if(session.fillParameters(phylumKey, phylumUpdateFields.size(), 
					session.fillParameters(phylumUpdateFields, 0, taxonomyUpdate.get(SpeciesOccursumFields.phylum))).executeUpdate()==0){
				phylumInsertFields.get(0).value(kingdomValue);
				phylumInsertFields.get(1).value(phylumValue);
				session.fillParameters(phylumInsertFields,0, taxonomyInsert.get(SpeciesOccursumFields.phylum)).executeUpdate();
			}

			// CLASS
			classKey.get(0).value(classValue);
			classUpdateFields.get(0).value(kingdomValue);
			classUpdateFields.get(1).value(phylumValue);
			if(session.fillParameters(classKey, classUpdateFields.size(), 
					session.fillParameters(classUpdateFields, 0, taxonomyUpdate.get(SpeciesOccursumFields.classcolumn))).executeUpdate()==0){
				classInsertFields.get(0).value(kingdomValue);
				classInsertFields.get(1).value(phylumValue);
				classInsertFields.get(2).value(classValue);
				session.fillParameters(classInsertFields,0, taxonomyInsert.get(SpeciesOccursumFields.classcolumn)).executeUpdate();
			}


			// ORDER
			orderKey.get(0).value(orderValue);
			orderUpdateFields.get(0).value(kingdomValue);
			orderUpdateFields.get(1).value(phylumValue);
			orderUpdateFields.get(2).value(classValue);
			if(session.fillParameters(orderKey, orderUpdateFields.size(), 
					session.fillParameters(orderUpdateFields, 0, taxonomyUpdate.get(SpeciesOccursumFields.ordercolumn))).executeUpdate()==0){
				orderInsertFields.get(0).value(kingdomValue);
				orderInsertFields.get(1).value(phylumValue);
				orderInsertFields.get(2).value(classValue);
				orderInsertFields.get(3).value(orderValue);
				session.fillParameters(orderInsertFields,0, taxonomyInsert.get(SpeciesOccursumFields.ordercolumn)).executeUpdate();
			}

			//FAMILY
			familyKey.get(0).value(familyValue);
			familyUpdateFields.get(0).value(kingdomValue);
			familyUpdateFields.get(1).value(phylumValue);
			familyUpdateFields.get(2).value(classValue);
			familyUpdateFields.get(3).value(orderValue);
			if(session.fillParameters(familyKey, familyUpdateFields.size(), 
					session.fillParameters(familyUpdateFields, 0, taxonomyUpdate.get(SpeciesOccursumFields.familycolumn))).executeUpdate()==0){
				familyInsertFields.get(0).value(kingdomValue);
				familyInsertFields.get(1).value(phylumValue);
				familyInsertFields.get(2).value(classValue);
				familyInsertFields.get(3).value(orderValue);
				familyInsertFields.get(4).value(familyValue);
				session.fillParameters(familyInsertFields,0, taxonomyInsert.get(SpeciesOccursumFields.familycolumn)).executeUpdate();
			}

			
			//****************************************** SPECIES **********************
			
//			String speciesId=null;
//			for(int i=0;i<DBUtil.speciesFields.length;i++){
//				String value=arg1.get(modelCSVFieldsMapping[i]);
//				setParameter(i,i+1,value,psInsert);
//				if(DBUtil.speciesFields[i].equals(SpeciesFields.speciesid+""))speciesId=value;
//				else setParameter(i,i,value,psUpdate);
//			}
//			setParameter(0,DBUtil.speciesFields.length,speciesId,psUpdate);
//			
//			if(psUpdate.executeUpdate()==0)
//				psInsert.executeUpdate();
			
		}catch(Exception e){
			logger.error("Unable to insert line ",e);
			System.err.println("Unable to insert line ");
			e.printStackTrace();
			continueProcess=false;
			status=ResourceStatus.Error;
		}
	}

	@Override
	public void processHeaderLine(int arg0, List<String> arg1) {
		try{
			logger.trace("Processing Header..");			
			for(int i=0;i<DBUtil.speciesFields.length;i++){
				modelCSVFieldsMapping[i]=arg1.indexOf(DBUtil.speciesFields[i]);
				if(DBUtil.speciesFields[i].equals(SpeciesFields.kingdom+"")) taxoindexes.put(SpeciesFields.kingdom, modelCSVFieldsMapping[i]);
				if(DBUtil.speciesFields[i].equals(SpeciesFields.classcolumn+"")) taxoindexes.put(SpeciesFields.classcolumn, modelCSVFieldsMapping[i]);
				if(DBUtil.speciesFields[i].equals(SpeciesFields.ordercolumn+"")) taxoindexes.put(SpeciesFields.ordercolumn, modelCSVFieldsMapping[i]);
				if(DBUtil.speciesFields[i].equals(SpeciesFields.phylum+"")) taxoindexes.put(SpeciesFields.phylum, modelCSVFieldsMapping[i]);
				if(DBUtil.speciesFields[i].equals(SpeciesFields.familycolumn+"")) taxoindexes.put(SpeciesFields.familycolumn, modelCSVFieldsMapping[i]);
			}
		}catch(Exception e){
			logger.error("Unable to read header",e);
			System.err.println("Unable to read header");
			e.printStackTrace();
			continueProcess=false;
			status=ResourceStatus.Error;
		}
	}

	
	public void close()throws Exception{
//		if(psInsert!=null)psInsert.close();
//		if(psUpdate!=null)psUpdate.close();
		for(PreparedStatement ps:taxonomyInsert.values())if(ps!=null)ps.close();
		for(PreparedStatement ps:taxonomyUpdate.values())if(ps!=null)ps.close();
	}


//	private String speciesInsertQuery(){		
//		StringBuilder toReturn=new StringBuilder("INSERT into "+Tables.Species+" values (");		
//		for(String fieldName : DBUtil.speciesFields){
//			toReturn.append("?,");
//		}
//		toReturn.deleteCharAt(toReturn.lastIndexOf(","));
//		toReturn.append(")");
//		return toReturn.toString();
//	}
//	private String speciesUpdateQuery(){
//		StringBuilder toReturn=new StringBuilder("UPDATE "+Tables.Species+" set ");		
//		for(int i=1;i<DBUtil.speciesFields.length;i++){
//			String fieldName=DBUtil.speciesFields[i];
//			toReturn.append(fieldName+"=?,");
//		}
//		toReturn.deleteCharAt(toReturn.lastIndexOf(","));
//		toReturn.append(" where "+SpeciesFields.speciesid+"=?");
//		return toReturn.toString();
//	}

	private static void setParameter(int metaIndex,int paramIndex,String value,PreparedStatement ps)throws Exception{
		if(value==null||value.equalsIgnoreCase("")||value.equalsIgnoreCase("null"))
			ps.setNull(paramIndex, DBUtil.speciesFieldsType[metaIndex]);
		else if(DBUtil.speciesFieldsType[metaIndex]==java.sql.Types.VARCHAR)
				ps.setString(paramIndex,value);
		else if(DBUtil.speciesFieldsType[metaIndex]==java.sql.Types.INTEGER||DBUtil.speciesFieldsType[metaIndex]==java.sql.Types.SMALLINT)
			ps.setInt(paramIndex,Integer.parseInt(value));
		else if(DBUtil.speciesFieldsType[metaIndex]==java.sql.Types.DOUBLE)
			ps.setDouble(paramIndex,Double.parseDouble(value));
		else if(DBUtil.speciesFieldsType[metaIndex]==java.sql.Types.TIMESTAMP)
			ps.setLong(paramIndex, Long.parseLong(value));
	}
	
}
