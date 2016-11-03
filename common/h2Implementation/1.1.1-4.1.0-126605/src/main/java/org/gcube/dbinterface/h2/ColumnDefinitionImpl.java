package org.gcube.dbinterface.h2;

import org.gcube.common.dbinterface.ColumnDefinition;
import org.gcube.common.dbinterface.Specification;
import org.gcube.common.dbinterface.types.Type;

public class ColumnDefinitionImpl implements ColumnDefinition{
	
	private String name=null;
	private Type type=null;
	private Specification[] specification=null;

	public ColumnDefinitionImpl() {}
	
	public String getDefinition(){
		String tempSpecif="";
		String tempType=null;
		if (this.specification!=null){
			for (Specification spec: this.specification)
				if (spec==Specification.AUTO_INCREMENT)
					tempType="SERIAL";
				else tempSpecif+=spec.getValue()+" ";
		}
		if (tempType==null) tempType=this.type.getTypeDefinition();
		return this.name+" "+tempType+" "+tempSpecif;
	}

	public String getLabel() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public void setLabel(String label) {
		this.name= label;
	}

	public void setSpecification(Specification... specifications) {
		if (this.type!=null){
			this.type.setAutoincrement(false);
			if (specifications!=null){
				for (Specification spec: specifications){
					if (spec==Specification.AUTO_INCREMENT)
						this.type.setAutoincrement(true);
					if (spec==Specification.PRIMARY_KEY)
						this.type.setPrimaryKey(true);
				}
			}
		}
		this.specification= specifications;
	}

	public void setType(Type type) {
		if (this.specification!=null){
			for (Specification spec: this.specification)
				if (spec==Specification.AUTO_INCREMENT)
					type.setAutoincrement(true);
		}
		this.type= type;
	}

	@Override
	public int compareTo(ColumnDefinition col) {
		return this.getLabel().compareTo(col.getLabel());
	}
	
}