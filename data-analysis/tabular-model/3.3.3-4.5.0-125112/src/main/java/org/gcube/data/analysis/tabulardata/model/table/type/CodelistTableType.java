package org.gcube.data.analysis.tabulardata.model.table.type;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.table.TableType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CodelistTableType extends TableType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7122913430907675469L;

	private static final List<ColumnType> ALLOWED_COLUMN_TYPES=new ArrayList<>();
	
	static{
		ALLOWED_COLUMN_TYPES.add(new AnnotationColumnType());		
		ALLOWED_COLUMN_TYPES.add(new CodeColumnType());
		ALLOWED_COLUMN_TYPES.add(new CodeDescriptionColumnType());
		ALLOWED_COLUMN_TYPES.add(new CodeNameColumnType());		
		ALLOWED_COLUMN_TYPES.add(new ValidationColumnType());
		ALLOWED_COLUMN_TYPES.add(new IdColumnType());
	}
	
	@Override
	public String getCode() {
		return "CODELIST";
	}

	@Override
	public String getName() {
		return "Codelist";
	}

	@Override
	public List<ColumnType> getAllowedColumnTypes() {
		return ALLOWED_COLUMN_TYPES;
	}
}
