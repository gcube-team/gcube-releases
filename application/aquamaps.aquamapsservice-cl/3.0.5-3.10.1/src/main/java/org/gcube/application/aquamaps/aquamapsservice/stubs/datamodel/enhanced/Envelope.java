package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.EnvelopeFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HspenFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;


public class Envelope extends DataModel{

	private boolean useFaoAreas=true;
	private boolean useBoundingBox=true;
	private boolean useBottomSeaTempAndSalinity=true;
	
	
	/**
	 * @return the useFaoAreas
	 */
	public boolean isUseFaoAreas() {
		return useFaoAreas;
	}
	/**
	 * @param useFaoAreas the useFaoAreas to set
	 */
	public void setUseFaoAreas(boolean useFaoAreas) {
		this.useFaoAreas = useFaoAreas;
	}
	/**
	 * @return the useBoundingBox
	 */
	public boolean isUseBoundingBox() {
		return useBoundingBox;
	}
	/**
	 * @param useBoundingBox the useBoundingBox to set
	 */
	public void setUseBoundingBox(boolean useBoundingBox) {
		this.useBoundingBox = useBoundingBox;
	}
	/**
	 * @return the useBottomSeaTempAndSalinity
	 */
	public boolean isUseBottomSeaTempAndSalinity() {
		return useBottomSeaTempAndSalinity;
	}
	/**
	 * @param useBottomSeaTempAndSalinity the useBottomSeaTempAndSalinity to set
	 */
	public void setUseBottomSeaTempAndSalinity(boolean useBottomSeaTempAndSalinity) {
		this.useBottomSeaTempAndSalinity = useBottomSeaTempAndSalinity;
	}

	private String FaoAreas="";
	/**
	 * @return the faoAreas
	 */
	public String getFaoAreas() {
		return FaoAreas;
	}
	/**
	 * @param faoAreas the faoAreas to set
	 */
	public void setFaoAreas(String faoAreas) {
		FaoAreas = faoAreas;
	}
	/**
	 * @return the boundingBox
	 */
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
	/**
	 * @param boundingBox the boundingBox to set
	 */
	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}
	/**
	 * @return the pelagic
	 */
	public boolean isPelagic() {
		return pelagic;
	}
	/**
	 * @param pelagic the pelagic to set
	 */
	public void setPelagic(boolean pelagic) {
		this.pelagic = pelagic;
	}

	private BoundingBox boundingBox=new BoundingBox();
	private boolean pelagic;
	private boolean useMeanDepth;
	
	
		/**
		 * Map 
		 * 		String parameterName -> (String valueName-->Float value);
		 * 		eg:
		 * 		Depth -> min-> 100
		 */
		
		private Map<EnvelopeFields,Map<HspenFields,Double>> parameters=new HashMap<EnvelopeFields, Map<HspenFields,Double>> ();
		
		public Envelope(){
			Map<HspenFields,Double> depth=new HashMap<HspenFields, Double>();
			depth.put(HspenFields.depthmin, new Double(0));
			depth.put(HspenFields.depthmax, new Double(0));
			depth.put(HspenFields.depthprefmax, new Double(0));
			depth.put(HspenFields.depthprefmin, new Double(0));
			parameters.put(EnvelopeFields.Depth, depth);
			Map<HspenFields,Double> temp=new HashMap<HspenFields, Double>();
			temp.put(HspenFields.tempmin, new Double(0));
			temp.put(HspenFields.tempmax, new Double(0));
			temp.put(HspenFields.tempprefmax, new Double(0));
			temp.put(HspenFields.tempprefmin, new Double(0));
			parameters.put(EnvelopeFields.Temperature, temp);
			Map<HspenFields,Double> salinity=new HashMap<HspenFields, Double>();
			salinity.put(HspenFields.salinitymin, new Double(0));
			salinity.put(HspenFields.salinitymax, new Double(0));
			salinity.put(HspenFields.salinityprefmax, new Double(0));
			salinity.put(HspenFields.salinityprefmin, new Double(0));			
			parameters.put(EnvelopeFields.Salinity, salinity);
			Map<HspenFields,Double> primProd=new HashMap<HspenFields, Double>();
			primProd.put(HspenFields.primprodprefmin,new Double(0));
			primProd.put(HspenFields.primprodprefmax,new Double(0));
			primProd.put(HspenFields.primprodmin,new Double(0));
			primProd.put(HspenFields.primprodmax,new Double(0));
			parameters.put(EnvelopeFields.PrimaryProduction, primProd);
			Map<HspenFields,Double> iceCon=new HashMap<HspenFields, Double>();
			iceCon.put(HspenFields.iceconmin, new Double(0));
			iceCon.put(HspenFields.iceconmax, new Double(0));
			iceCon.put(HspenFields.iceconprefmin, new Double(0));
			iceCon.put(HspenFields.iceconprefmax, new Double(0));			
			parameters.put(EnvelopeFields.IceConcentration,iceCon);
			Map<HspenFields,Double> land=new HashMap<HspenFields, Double>();
			land.put(HspenFields.landdistmin, new Double(0));
			land.put(HspenFields.landdistmax, new Double(0));
			land.put(HspenFields.landdistprefmin, new Double(0));
			land.put(HspenFields.landdistprefmax, new Double(0));			
			parameters.put(EnvelopeFields.LandDistance,land);
		}
		
		public Set<HspenFields> getValueNames(EnvelopeFields parameterName){
			return parameters.get(parameterName).keySet();
		}
		public Double getValue(EnvelopeFields parameterName,HspenFields depthprefmax){
			return parameters.get(parameterName).get(depthprefmax);
		}
		public void setValue(EnvelopeFields parameterName,HspenFields valueName,double d){
			parameters.get(parameterName).put(valueName,new Double(d));
		}
		
		public Double getMinValue(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return getValue(parameter,HspenFields.depthmin);
			case IceConcentration : return getValue(parameter,HspenFields.iceconmin);
			case LandDistance: return getValue(parameter,HspenFields.landdistmin);
			case PrimaryProduction: return getValue(parameter,HspenFields.primprodmin);
			case Salinity : return getValue(parameter,HspenFields.salinitymin);
			case Temperature: return getValue(parameter,HspenFields.tempmin);
			default : throw new IllegalArgumentException();
			}
		}
		
		public static HspenFields getMinName(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return HspenFields.depthmin;
			case IceConcentration : return HspenFields.iceconmin;
			case LandDistance: return HspenFields.landdistmin;
			case PrimaryProduction: return HspenFields.primprodmin;
			case Salinity : return HspenFields.salinitymin;
			case Temperature: return HspenFields.tempmin;
			default : throw new IllegalArgumentException();
			}
		}
		
		public Double getMaxValue(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return getValue(parameter,HspenFields.depthmax);
			case IceConcentration : return getValue(parameter,HspenFields.iceconmax);
			case LandDistance: return getValue(parameter,HspenFields.landdistmax);
			case PrimaryProduction: return getValue(parameter,HspenFields.primprodmax);
			case Salinity : return getValue(parameter,HspenFields.salinitymax);
			case Temperature: return getValue(parameter,HspenFields.tempmax);
			default : throw new IllegalArgumentException();
			}
		}
		
		public static HspenFields getMaxName(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return HspenFields.depthmax;
			case IceConcentration : return HspenFields.iceconmax;
			case LandDistance: return HspenFields.landdistmax;
			case PrimaryProduction: return HspenFields.primprodmax;
			case Salinity : return HspenFields.salinitymax;
			case Temperature: return HspenFields.tempmax;
			default : throw new IllegalArgumentException();
			}
		}
		
		
		public Double getPrefMinValue(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return getValue(parameter,HspenFields.depthprefmin);
			case IceConcentration : return getValue(parameter,HspenFields.iceconprefmin);
			case LandDistance: return getValue(parameter,HspenFields.landdistprefmin);
			case PrimaryProduction: return getValue(parameter,HspenFields.primprodprefmin);
			case Salinity : return getValue(parameter,HspenFields.salinityprefmin);
			case Temperature: return getValue(parameter,HspenFields.tempprefmin);
			default : throw new IllegalArgumentException();
			}
		}
		
		public static HspenFields getPrefMinName(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return HspenFields.depthprefmin;
			case IceConcentration : return HspenFields.iceconprefmin;
			case LandDistance: return HspenFields.landdistprefmin;
			case PrimaryProduction: return HspenFields.primprodprefmin;
			case Salinity : return HspenFields.salinityprefmin;
			case Temperature: return HspenFields.tempprefmin;
			default : throw new IllegalArgumentException();
			}
		}
		
		
		public Double getPrefMaxValue(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return getValue(parameter,HspenFields.depthprefmax);
			case IceConcentration : return getValue(parameter,HspenFields.iceconprefmax);
			case LandDistance: return getValue(parameter,HspenFields.landdistprefmax);
			case PrimaryProduction: return getValue(parameter,HspenFields.primprodprefmax);
			case Salinity : return getValue(parameter,HspenFields.salinityprefmax);
			case Temperature: return getValue(parameter,HspenFields.tempprefmax);
			default : throw new IllegalArgumentException();
			}
		}
		
		public static HspenFields getPrefMaxName(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return HspenFields.depthprefmax;
			case IceConcentration : return HspenFields.iceconprefmax;
			case LandDistance: return HspenFields.landdistprefmax;
			case PrimaryProduction: return HspenFields.primprodprefmax;
			case Salinity : return HspenFields.salinityprefmax;
			case Temperature: return HspenFields.tempprefmax;
			default : throw new IllegalArgumentException();
			}
		}
		public void setUseMeanDepth(boolean useMeanDepth) {
			this.useMeanDepth = useMeanDepth;
		}
		public boolean isUseMeanDepth() {
			return useMeanDepth;
		}
		
		public FieldArray toFieldArray(){
			List<Field> fields=new ArrayList<Field>();
			for(EnvelopeFields envF:EnvelopeFields.values())
				for(HspenFields paramName:this.getValueNames(envF)){
					Field f= new Field();
					f.name(paramName.toString());
					f.type(FieldType.DOUBLE);
					f.value(String.valueOf(this.getValue(envF, paramName)));
					fields.add(f);
				}
			return new FieldArray(fields);
		}
		
		
		
}
