package org.gcube.application.perform.service;

public class TableCreator {






	private String path;
	private String tableName;
	private String farmIdField;
	private String routineIdField;

//	public static void main(String[] args) throws IOException {
//		TableCreator creator=new TableCreator(
//				"/home/fabio/Documents/work files/Perform/Grow_out_Aggregated_Batch_Data_Entry_KPI_aggregated.csv",
//				"dummy", "farmid", "routineid");
//		System.out.println(creator.getCreateStatement());
//
//		int maxFieldLength=0;
//		for(String path:Paths.get("/home/fabio/Documents/work files/Perform/").toFile().list()) {
//			int current=getMaxFieldLength("/home/fabio/Documents/work files/Perform/"+path);
//			maxFieldLength=current>maxFieldLength?current:maxFieldLength;
//		}
//		System.out.println("Max field Length is "+maxFieldLength);
//	}
//
//	public TableCreator(String path, String tableName, String routineIdField) {
//		super();
//		this.path = path;
//		this.tableName = tableName;		
//		this.routineIdField = routineIdField;
//	}
//
//
//	public String getCreateStatement() throws IOException {
//
//		String fieldDefinitions=getFieldDefinitions(path);
//
//		String standardDefinitions=
//				String.format( "%1$s bigint,"						
//						+ "FOREIGN KEY (%1$s) REFERENCES "+ImportRoutine.TABLE+"("+ImportRoutine.ID+")",routineIdField);
//
//		return String.format("CREATE TABLE IF NOT EXISTS %1$s (%2$s, %3$s)",
//				tableName,fieldDefinitions,standardDefinitions);
//	}
//
//
//	private static final String getFieldDefinitions(String path) throws IOException {
//
//		Reader in = new FileReader(path);
//		CSVParser parser= CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
//		Map<String,Integer> headers=parser.getHeaderMap();
//
//		CSVRecord record=parser.getRecords().get(0);
//
//		StringBuilder toReturn=new StringBuilder();
//
//		for(Entry<String,Integer> header:headers.entrySet()) {
//			String value=record.get(header.getKey());
//			String type=null;
//			if(value.matches(FLOAT_REGEX)) type="real";
//			else if(value.matches(INTEGER_REGEX)) type="bigint";
//			else type="text";
//
//			String fieldName=header.getKey().toLowerCase().replaceAll(" ", "_");
//
//
//			toReturn.append(String.format("%1$s %2$s,", fieldName, type));
//		}
//		toReturn.deleteCharAt(toReturn.lastIndexOf(","));
//
//		return toReturn.toString();
//	}
//
//
//	private static final int getMaxFieldLength(String path) throws IOException {
//		Reader in = new FileReader(path);
//		CSVParser parser= CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
//		Map<String,Integer> headers=parser.getHeaderMap();
//		int toReturn=0;
//		for(String field:headers.keySet()) {
//			toReturn=field.length()>toReturn?field.length():toReturn;
//		}
//		return toReturn;
//	}
}
