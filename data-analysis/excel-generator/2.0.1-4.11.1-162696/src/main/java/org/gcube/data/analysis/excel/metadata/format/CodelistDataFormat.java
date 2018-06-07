package org.gcube.data.analysis.excel.metadata.format;

import java.util.LinkedList;
import java.util.List;

import org.gcube.data.analysis.excel.data.DataTable;

public class CodelistDataFormat implements DataFormat {

	private CodeList codelist;
	private final String 	CODE_ID = "ID",
							CODE_DESCRIPTION = "DESCRIPTION";
	
	public static class CodeMap {
		private LinkedList<String> ids,
							descriptions;
		
		public CodeMap ()
		{
			this.ids = new LinkedList<>();
			this.descriptions = new LinkedList<>();
		}
		
		public void addElements (List<String> ids, List<String> descriptions)
		{
			if (ids == null || descriptions == null || (ids.size() != descriptions.size())) throw new IllegalArgumentException("IDs and Dimenstions should contain the same number of elements");
			
			this.ids.addAll(ids);
			this.descriptions.addAll(descriptions);
		}
		
		public void addElement (String id, String description)
		{
			this.ids.add(id);
			this.descriptions.add(description);
		}
		
		private List<String> getIds ()
		{
			return this.ids;
		}
		
		private List<String> getDescriptions ()
		{
			return this.descriptions;
		}
	}
	
	public CodelistDataFormat(String reference, CodeMap codeMap ) {

		this.codelist = generateCodelist(reference, codeMap);
	}
	
	public CodelistDataFormat(String reference, List<String> ids, List<String> descriptions) 
	{
		CodeMap codeMap = new CodeMap();
		codeMap.addElements(ids, descriptions);
		this.codelist = generateCodelist(reference, codeMap);
	}
	
	private CodeList generateCodelist (String reference, CodeMap codeMap)
	{
		CodeList codelist = new CodeList(reference);
		codelist.addColumn(CODE_ID, codeMap.getIds(), false);
		codelist.addColumn(CODE_DESCRIPTION, codeMap.getDescriptions(), true);
		return codelist;
	}
	
	@Override
	public String getReference() 
	{
		return codelist.getExcelTableName();
	}

	
	@Override
	public DataTable getDefinitionTable() 
	{
		return this.codelist;
	}

	@Override
	public boolean isCatchValue () 
	{
		return false;
	}

}
