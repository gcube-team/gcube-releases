package org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.EnvelopeFieldsClient;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;



import com.google.gwt.user.client.rpc.IsSerializable;

public class ClientEnvelope implements IsSerializable{
	private boolean useFaoAreas=true;
	private boolean useBoundingBox=true;
	private boolean useBottomSeaTempAndSalinity=true;
	
	private String speciesId;
	
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
		
		private Map<EnvelopeFieldsClient,Map<SpeciesFields,Double>> parameters=new HashMap<EnvelopeFieldsClient, Map<SpeciesFields,Double>> ();
		
		public ClientEnvelope(){
			Map<SpeciesFields,Double> depth=new HashMap<SpeciesFields, Double>();
			depth.put(SpeciesFields.depthmin, new Double(0));
			depth.put(SpeciesFields.depthmax, new Double(0));
			depth.put(SpeciesFields.depthprefmax, new Double(0));
			depth.put(SpeciesFields.depthprefmin, new Double(0));
			parameters.put(EnvelopeFieldsClient.Depth, depth);
			Map<SpeciesFields,Double> temp=new HashMap<SpeciesFields, Double>();
			temp.put(SpeciesFields.tempmin, new Double(0));
			temp.put(SpeciesFields.tempmax, new Double(0));
			temp.put(SpeciesFields.tempprefmax, new Double(0));
			temp.put(SpeciesFields.tempprefmin, new Double(0));
			parameters.put(EnvelopeFieldsClient.Temperature, temp);
			Map<SpeciesFields,Double> salinity=new HashMap<SpeciesFields, Double>();
			salinity.put(SpeciesFields.salinitymin, new Double(0));
			salinity.put(SpeciesFields.salinitymax, new Double(0));
			salinity.put(SpeciesFields.salinityprefmax, new Double(0));
			salinity.put(SpeciesFields.salinityprefmin, new Double(0));			
			parameters.put(EnvelopeFieldsClient.Salinity, salinity);
			Map<SpeciesFields,Double> primProd=new HashMap<SpeciesFields, Double>();
			primProd.put(SpeciesFields.primprodprefmin,new Double(0));
			primProd.put(SpeciesFields.primprodprefmax,new Double(0));
			primProd.put(SpeciesFields.primprodmin,new Double(0));
			primProd.put(SpeciesFields.primprodmax,new Double(0));
			parameters.put(EnvelopeFieldsClient.PrimaryProduction, primProd);
			Map<SpeciesFields,Double> iceCon=new HashMap<SpeciesFields, Double>();
			iceCon.put(SpeciesFields.iceconmin, new Double(0));
			iceCon.put(SpeciesFields.iceconmax, new Double(0));
			iceCon.put(SpeciesFields.iceconprefmin, new Double(0));
			iceCon.put(SpeciesFields.iceconprefmax, new Double(0));			
			parameters.put(EnvelopeFieldsClient.IceConcentration,iceCon);
			Map<SpeciesFields,Double> land=new HashMap<SpeciesFields, Double>();
			land.put(SpeciesFields.landdistmin, new Double(0));
			land.put(SpeciesFields.landdistmax, new Double(0));
			land.put(SpeciesFields.landdistprefmin, new Double(0));
			land.put(SpeciesFields.landdistprefmax, new Double(0));			
			parameters.put(EnvelopeFieldsClient.LandDistance,land);
		}
		
		public Set<SpeciesFields> getValueNames(EnvelopeFieldsClient parameterName){
			return parameters.get(parameterName).keySet();
		}
		public Double getValue(EnvelopeFieldsClient parameterName,SpeciesFields depthprefmax){
			return parameters.get(parameterName).get(depthprefmax);
		}
		public void setValue(EnvelopeFieldsClient parameterName,SpeciesFields valueName,double d){
			parameters.get(parameterName).put(valueName,new Double(d));
		}
		
		public Double getMinValue(EnvelopeFieldsClient parameter){
			switch(parameter){
			case Depth : return getValue(parameter,SpeciesFields.depthmin);
			case IceConcentration : return getValue(parameter,SpeciesFields.iceconmin);
			case LandDistance: return getValue(parameter,SpeciesFields.landdistmin);
			case PrimaryProduction: return getValue(parameter,SpeciesFields.primprodmin);
			case Salinity : return getValue(parameter,SpeciesFields.salinitymin);
			case Temperature: return getValue(parameter,SpeciesFields.tempmin);
			default : throw new IllegalArgumentException();
			}
		}
		
		public static SpeciesFields getMinName(EnvelopeFieldsClient parameter){
			switch(parameter){
			case Depth : return SpeciesFields.depthmin;
			case IceConcentration : return SpeciesFields.iceconmin;
			case LandDistance: return SpeciesFields.landdistmin;
			case PrimaryProduction: return SpeciesFields.primprodmin;
			case Salinity : return SpeciesFields.salinitymin;
			case Temperature: return SpeciesFields.tempmin;
			default : throw new IllegalArgumentException();
			}
		}
		
		public Double getMaxValue(EnvelopeFieldsClient parameter){
			switch(parameter){
			case Depth : return getValue(parameter,SpeciesFields.depthmax);
			case IceConcentration : return getValue(parameter,SpeciesFields.iceconmax);
			case LandDistance: return getValue(parameter,SpeciesFields.landdistmax);
			case PrimaryProduction: return getValue(parameter,SpeciesFields.primprodmax);
			case Salinity : return getValue(parameter,SpeciesFields.salinitymax);
			case Temperature: return getValue(parameter,SpeciesFields.tempmax);
			default : throw new IllegalArgumentException();
			}
		}
		
		public static SpeciesFields getMaxName(EnvelopeFieldsClient parameter){
			switch(parameter){
			case Depth : return SpeciesFields.depthmax;
			case IceConcentration : return SpeciesFields.iceconmax;
			case LandDistance: return SpeciesFields.landdistmax;
			case PrimaryProduction: return SpeciesFields.primprodmax;
			case Salinity : return SpeciesFields.salinitymax;
			case Temperature: return SpeciesFields.tempmax;
			default : throw new IllegalArgumentException();
			}
		}
		
		
		public Double getPrefMinValue(EnvelopeFieldsClient parameter){
			switch(parameter){
			case Depth : return getValue(parameter,SpeciesFields.depthprefmin);
			case IceConcentration : return getValue(parameter,SpeciesFields.iceconprefmin);
			case LandDistance: return getValue(parameter,SpeciesFields.landdistprefmin);
			case PrimaryProduction: return getValue(parameter,SpeciesFields.primprodprefmin);
			case Salinity : return getValue(parameter,SpeciesFields.salinityprefmin);
			case Temperature: return getValue(parameter,SpeciesFields.tempprefmin);
			default : throw new IllegalArgumentException();
			}
		}
		
		public static SpeciesFields getPrefMinName(EnvelopeFieldsClient parameter){
			switch(parameter){
			case Depth : return SpeciesFields.depthprefmin;
			case IceConcentration : return SpeciesFields.iceconprefmin;
			case LandDistance: return SpeciesFields.landdistprefmin;
			case PrimaryProduction: return SpeciesFields.primprodprefmin;
			case Salinity : return SpeciesFields.salinityprefmin;
			case Temperature: return SpeciesFields.tempprefmin;
			default : throw new IllegalArgumentException();
			}
		}
		
		
		public Double getPrefMaxValue(EnvelopeFieldsClient parameter){
			switch(parameter){
			case Depth : return getValue(parameter,SpeciesFields.depthprefmax);
			case IceConcentration : return getValue(parameter,SpeciesFields.iceconprefmax);
			case LandDistance: return getValue(parameter,SpeciesFields.landdistprefmax);
			case PrimaryProduction: return getValue(parameter,SpeciesFields.primprodprefmax);
			case Salinity : return getValue(parameter,SpeciesFields.salinityprefmax);
			case Temperature: return getValue(parameter,SpeciesFields.tempprefmax);
			default : throw new IllegalArgumentException();
			}
		}
		
		public static SpeciesFields getPrefMaxName(EnvelopeFieldsClient parameter){
			switch(parameter){
			case Depth : return SpeciesFields.depthprefmax;
			case IceConcentration : return SpeciesFields.iceconprefmax;
			case LandDistance: return SpeciesFields.landdistprefmax;
			case PrimaryProduction: return SpeciesFields.primprodprefmax;
			case Salinity : return SpeciesFields.salinityprefmax;
			case Temperature: return SpeciesFields.tempprefmax;
			default : throw new IllegalArgumentException();
			}
		}
		public void setUseMeanDepth(boolean useMeanDepth) {
			this.useMeanDepth = useMeanDepth;
		}
		public boolean isUseMeanDepth() {
			return useMeanDepth;
		}
		public void setSpeciesId(String speciesId) {
			this.speciesId = speciesId;
		}
		public String getSpeciesId() {
			return speciesId;
		}
		
		
}
