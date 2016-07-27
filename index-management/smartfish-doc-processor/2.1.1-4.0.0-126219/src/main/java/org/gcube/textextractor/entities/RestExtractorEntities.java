package org.gcube.textextractor.entities;

import java.io.Serializable;
import java.util.List;

public class RestExtractorEntities {

	public static class CountryObj implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public String id;
		public String label;
                String name;
		
		public CountryObj() {
		}
                
		@Override
		public String toString() {
			return "CountryObj [id=" + id + ", label=" + label + "]";
		}
		
		
	}
	
	
	
	public static class TableDataObj implements Serializable {
		private static final long serialVersionUID = 1L;
		
		String tableId;
		String tableName;
		String tableSQLName;
		public List<IntermediateCriterionObj> criteria;
		public List<IntermediateStatisticObj> statistics;
		
		@Override
		public String toString() {
			return "TableDataObj [tableID=" + tableId + ", tableName="
					+ tableName + ", tableSQLName=" + tableSQLName
					+ ", criteria=" + criteria + "]";
		}
		
		
	}
	
	
	
        
	public static class IntermediateCriterionObj implements Serializable {
		private static final long serialVersionUID = 1L;
		
		Integer id;
		String name;
		public Integer criterionId;
		Integer nomenclatureId;
		public String criterionName;
		String criterionColumnName;
		String frLabel;
		String enLabel;
		
		
		public IntermediateCriterionObj() {
		}


		@Override
		public String toString() {
			return "IntermediateCriterionObj [countryID=" + id + ", countryName="
					+ name + ", criterionId=" + criterionId
					+ ", nomenclatureId=" + nomenclatureId + ", criterionName="
					+ criterionName + ", criterionColumnName="
					+ criterionColumnName + ", frLabel=" + frLabel
					+ ", enLabel=" + enLabel + "]";
		}
		
	}
        
        
	public static class IntermediateStatisticObj implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public String id;
		public String name;
		public String enLabel;
		public String frLabel;
		
		public IntermediateStatisticObj() {
		}


		@Override
		public String toString() {
			return "IntermediateStatisticObj [id=" + id + ", name="
					+ name + "en_label=" + enLabel +"]";
		}
	}
	
	public static class CriterionObj implements Serializable {
		private static final long serialVersionUID = 1L;
		
		Integer tableId;
		String tableName;
		Integer criterionId;
		String criterionName;

		public List<ValueObj> values;
		
		public CriterionObj() {
		}

		
	}
	
	public static class ValueObj implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public Integer id;
		String name;
		public String frLabel;
		public String enLabel;
		public String frDescription;
		public String enDescription;

		public ValueObj() {
		}

		@Override
		public String toString() {
			return "ValueObj [id=" + id + ", name=" + name + ", frLabel="
					+ frLabel + ", enLabel=" + enLabel + ", frDescription="
					+ frDescription + ", enDescription=" + enDescription + "]";
		}

		
	}

}
