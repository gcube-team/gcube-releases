package org.gcube.application.aquamaps.aquamapsportlet.servlet.utils;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.EnvelopeFieldsClient;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.LocalObjectFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientFieldType;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientEnvelope;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientField;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Envelope;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.EnvelopeFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HspenFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FileType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ObjectType;

public class ModelTranslation {

	public static ClientObject toClient(AquaMapsObject obj){
		ClientObject toReturn= new ClientObject();
		toReturn.setAuthor(obj.getAuthor());
		toReturn.getBoundingBox().parse(obj.getBoundingBox().toString());
		toReturn.setGis(obj.getGis());
		toReturn.setId(obj.getId());
		toReturn.setName(obj.getName());
		ClientField selectedSpecies=obj.getType().equals(ObjectType.Biodiversity)?new ClientField(LocalObjectFields.species+"",obj.getSelectedSpecies().size()+"",ClientFieldType.INTEGER):
			new ClientField(LocalObjectFields.species+"",obj.getSelectedSpecies().iterator().next().getId(),ClientFieldType.STRING);
		toReturn.setSelectedSpecies(selectedSpecies);
		toReturn.setThreshold(obj.getThreshold());
		toReturn.setType(ClientObjectType.valueOf(obj.getType()+""));
		for(org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.File f: obj.getImages()){
			if(f.getType().equals(FileType.Image)||f.getType().equals(FileType.JPG))
				toReturn.getImages().put(f.getName(),f.getUuri());
		}
		toReturn.setAlgorithmType(obj.getAlgorithmType()+"");		
		return toReturn;
	}


	public static ClientEnvelope toClient(Envelope env,String speciesId){
		ClientEnvelope toReturn= new ClientEnvelope();
		toReturn.getBoundingBox().parse(env.getBoundingBox().toString());
		toReturn.setFaoAreas(env.getFaoAreas());
		toReturn.setPelagic(env.isPelagic());
		toReturn.setUseBottomSeaTempAndSalinity(env.isUseBottomSeaTempAndSalinity());
		toReturn.setUseBoundingBox(env.isUseBoundingBox());
		toReturn.setUseFaoAreas(env.isUseFaoAreas());
		toReturn.setUseMeanDepth(env.isUseMeanDepth());
		for(EnvelopeFields f:EnvelopeFields.values())
			for(HspenFields n:env.getValueNames(f))
				toReturn.setValue(EnvelopeFieldsClient.valueOf(f+""), SpeciesFields.valueOf(n+""), env.getValue(f,n));
		return toReturn;
	}
	
	
}
