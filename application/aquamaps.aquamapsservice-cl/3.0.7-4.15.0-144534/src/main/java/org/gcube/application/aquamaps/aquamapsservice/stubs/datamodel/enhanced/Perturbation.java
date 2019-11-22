package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.PerturbationType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.PerturbationArray;

public class Perturbation extends DataModel{

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((perturbationValue == null) ? 0 : perturbationValue
						.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Perturbation other = (Perturbation) obj;
		if (perturbationValue == null) {
			if (other.perturbationValue != null)
				return false;
		} else if (!perturbationValue.equals(other.perturbationValue))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	private PerturbationType type=PerturbationType.ASSIGN;
	private String perturbationValue;


	public Perturbation() {
		this(PerturbationType.ASSIGN, "0");
	}
	public Perturbation(PerturbationType toSetType,String perturbation) {
		type=toSetType;
		perturbationValue=perturbation;
	}
	public PerturbationType getType() {
		return type;
	}
	public String getPerturbationValue() {
		return perturbationValue;
	}

	public Perturbation (org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Perturbation toLoad){
		this.setPerturbationValue(toLoad.value());
		this.setType(PerturbationType.valueOf(toLoad.type()));
	}

	public List<Perturbation> load(PerturbationArray toLoad){
		List<Perturbation> toReturn=new ArrayList<Perturbation>();
		if((toLoad!=null)&&(toLoad.theList()!=null))
			for(org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Perturbation p:toLoad.theList())
				toReturn.add(new Perturbation(p));
		return toReturn;
	}

	@Deprecated
	public org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Perturbation toStubsVersion(){
		org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Perturbation toReturn= new org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Perturbation();
		toReturn.value(this.getPerturbationValue());
		toReturn.type(this.getType().toString());
		return toReturn;
	}

	public static PerturbationArray toStubsVersion(List<Perturbation> toConvert){
		List<org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Perturbation> list=new ArrayList<org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Perturbation>();
		if(toConvert!=null)
			for(Perturbation obj:toConvert)
				list.add(obj.toStubsVersion());
		return new PerturbationArray(list);
	}


	public void setType(PerturbationType type) {
		this.type = type;
	}
	public void setPerturbationValue(String perturbationValue) {
		this.perturbationValue = perturbationValue;
	}


}
