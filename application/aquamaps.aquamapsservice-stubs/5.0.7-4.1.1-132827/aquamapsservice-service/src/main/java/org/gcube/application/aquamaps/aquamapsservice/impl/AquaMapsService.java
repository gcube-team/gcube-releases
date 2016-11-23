package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.util.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AquaMapsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.CellManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.JobExecutionManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.SimpleGenerator;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.SimpleGeneratorI;
import org.gcube.application.aquamaps.aquamapsservice.stubs.AquaMapsServicePortType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.CalculateEnvelopeRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.CalculateEnvelopefromCellSelectionRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetPhylogenyRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetSpeciesByFiltersRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetSpeciesEnvelopeRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Cell;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AreaType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HspenFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.utils.Storage;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.types.StringArray;
import org.gcube.common.core.types.VOID;
import org.gcube_system.namespaces.application.aquamaps.types.AquaMap;
import org.gcube_system.namespaces.application.aquamaps.types.FieldArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AquaMapsService extends GCUBEPortType implements AquaMapsServicePortType{


	private static Logger logger = LoggerFactory.getLogger(AquaMapsService.class);



	protected GCUBEServiceContext getServiceContext() {		
		return ServiceContext.getContext();
	}

	@Override
	public String getPhylogeny(GetPhylogenyRequestType req) throws GCUBEFault{
		try{
			Field toSelect= PortTypeTranslations.fromStubs(req.getToSelect());
			return SpeciesManager.getJSONTaxonomy(toSelect, PortTypeTranslations.fromStubs(req.getFieldList()), req.getPagedRequestSettings());
		}catch(Exception e){
			logger.error("Unable to get Taxonomy ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
	@Override
	public int deleteSubmitted(StringArray submittedIds)throws GCUBEFault{
		int toReturn=0;
		if((submittedIds!=null)&&(submittedIds.getItems()!=null))
			for(String id:submittedIds.getItems()){
				try{
				int toDelete=Integer.parseInt(id);
				toReturn+=SubmittedManager.delete(toDelete);
				}catch(Exception e){
					logger.error("cannot delete "+id, e);
				}
			}
			return toReturn;
	}



	@Override
	public FieldArray calculateEnvelope(CalculateEnvelopeRequestType req)throws GCUBEFault{
		logger.trace("Serving calculateEnvelope");
		try{
			
			
			BoundingBox bb= new BoundingBox();
			if(req.isUseBounding()){
				bb.setE(req.getBoundingEast());
				bb.setW(req.getBoundingWest());
				bb.setN(req.getBoundingNorth());
				bb.setS(req.getBoundingSouth());
			}
			List<Area> areas= new ArrayList<Area>();
			if(req.isUseFAO()){
				for(String code:req.getFaoAreas().split(","))
					areas.add(new Area(AreaType.FAO, code.trim()));
			}
			
			Set<Cell> foundCells= CellManager.calculateGoodCells(bb,areas,req.getSpeciesID(),SourceManager.getDefaultId(ResourceType.HCAF));
 			logger.trace("found "+foundCells.size()+" cells");

 			Species species=SpeciesManager.getSpeciesById(true,true,req.getSpeciesID(),SourceManager.getDefaultId(ResourceType.HSPEN));
 			
			if(req.isUseBottomSeaTempAndSalinity())
				species.getFieldbyName(HspenFields.layer+"").value("b");
			else species.getFieldbyName(HspenFields.layer+"").value("u");
 			
			
			
			
//			SpEnvelope envelope=new SpEnvelope();
//			envelope.reCalculate(species, foundCells);
			
			SimpleGeneratorI generator=new SimpleGenerator(ServiceContext.getContext().getEcoligicalConfigDir().getAbsolutePath()+File.separator);
			
			
			for(Field f:generator.getEnvelope(species, foundCells))species.addField(f);
			
			
			return PortTypeTranslations.toFieldArray(species.extractEnvelope().toFieldArray().theList());
		} catch (Exception e){
			logger.error("General Exception, unable to serve request",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	@Override
	public FieldArray calculateEnvelopefromCellSelection(CalculateEnvelopefromCellSelectionRequestType request)throws GCUBEFault{
		logger.trace("Serving calculateEnvelopefromCellSelection for speciesID : "+request.getSpeciesID());
		try{
			
			
			Set<Cell> selected=CellManager.getCellsByIds(true,request.getSpeciesID(),true,SourceManager.getDefaultId(ResourceType.HCAF),
					request.getCellIds().getItems());
			Species spec=SpeciesManager.getSpeciesById(true,true,request.getSpeciesID(),SourceManager.getDefaultId(ResourceType.HSPEN));
//			SpEnvelope envelope=new SpEnvelope();
//			envelope.reCalculate(spec, selected);
			
			SimpleGeneratorI generator=new SimpleGenerator(ServiceContext.getContext().getEcoligicalConfigDir().getAbsolutePath()+File.separator);
			
			
			for(Field f:generator.getEnvelope(spec, selected))spec.addField(f);
			
			return PortTypeTranslations.toFieldArray(spec.extractEnvelope().toFieldArray().theList());
			
		} catch (Exception e){
			logger.error("General Exception, unable to serve request",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

//	@Override
//	public String getOccurrenceCells(GetOccurrenceCellsRequestType request)throws GCUBEFault{
//		try{
//			return CellManager.getJSONOccurrenceCells(request.getSpeciesID(),request.getPagedRequestSettings()); 
//		} catch (Exception e){
//			logger.error("General Exception, unable to serve request",e);
//			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
//		}
//	}
	
	
	@Override	
	public String submitJob(org.gcube_system.namespaces.application.aquamaps.types.Job req)throws GCUBEFault{
		try{
			logger.trace("Serving submit job "+req.getName());
			logger.trace("Forcing group enabling if layers requested");
			Job job= PortTypeTranslations.fromStubs(req);
			boolean enableGis=false;
			for(AquaMapsObject obj : job.getAquaMapsObjectList())
				if(obj.getGis()) {
					enableGis=true;
					break;
				}
			
			job.setIsGis(enableGis);
			
			return JobExecutionManager.insertJobExecutionRequest(job,false)+"";
			
			
		}catch(Exception e){
			logger.error("Unable to execute Job "+req.getName(), e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}






	@Override
	public FieldArray getSpeciesEnvelop(GetSpeciesEnvelopeRequestType arg0)throws GCUBEFault{
		logger.trace("serving get Species envelope");
		try{
			Species selected=SpeciesManager.getSpeciesById(true,true,arg0.getSpeciesId(),arg0.getHspenId());
			return PortTypeTranslations.toFieldArray(selected.getAttributesList());
			
		} catch (Exception e){
			logger.error("General Exception, unable to serve request",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	/**
	 * return a list of species filtered by 3 groups of filters (species characteristics OR species names OR species codes) 
	 * 
	 * @param req
	 * @return
	 * @throws GCUBEFault
	 */


	@Override
	public String getSpeciesByFilters(GetSpeciesByFiltersRequestType req) throws GCUBEFault{
		logger.trace("Serving getSpecies by filters");
		
		try{
			return SpeciesManager.getJSONList(req.getPagedRequestSettings(),
					PortTypeTranslations.fromStubs(req.getGenericSearchFilters()), PortTypeTranslations.fromStubs(req.getSpecieficFilters()), req.getHspen());
			
		} catch (Exception e){
			logger.error("General Exception, unable to serve request",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}

	}




	
	@Override
	public VOID markSaved(StringArray ids)throws GCUBEFault{
		try{
			if((ids!=null)&&(ids.getItems()!=null))
				for(String id:ids.getItems())SubmittedManager.markSaved(Integer.parseInt(id));
			return new VOID();
		}catch(Exception e){
			logger.error("",e);
			throw new GCUBEFault();
		}
		
	}

	@Override
	public org.gcube_system.namespaces.application.aquamaps.types.Submitted loadSubmittedById(int arg0) throws RemoteException,
			GCUBEFault {
		try{
			logger.trace("Loading submitted id : "+arg0);
			List<Field> conditions=new ArrayList<Field>();
			conditions.add(new Field(SubmittedFields.searchid+"", arg0+"", FieldType.INTEGER));
			return PortTypeTranslations.toStubs(SubmittedManager.getList(conditions).get(0));			
		}catch(Exception e){
			logger.error("",e);
			throw new GCUBEFault("Impossible to load submitted : "+e.getMessage());
		}
	}

	
	@Override
	public AquaMap getObject(int arg0) throws RemoteException, GCUBEFault {
		try{
			AquaMapsObject obj=AquaMapsManager.loadObject(arg0,true,true);
//			logger.info("Object IS "+AquaMapsXStream.getXMLInstance().toXML(obj));
			return PortTypeTranslations.toStubs(obj);
			
		}catch(Exception e){
			logger.error("",e);
			throw new GCUBEFault("Impossible to load Object from Publisher : "+e.getMessage());
		}
	}

	@Override
	public String getSpeciesByFiltersASCSV(GetSpeciesByFiltersRequestType arg0)
			throws RemoteException, GCUBEFault {
		logger.trace("Serving getSpecies by filters");
		File toExport=null;
		try{
			toExport=SpeciesManager.getCSVList(PortTypeTranslations.fromStubs(arg0.getGenericSearchFilters()), PortTypeTranslations.fromStubs(arg0.getSpecieficFilters()), arg0.getHspen());
			GCUBEScope scope=ServiceContext.getContext().getScope();
			logger.trace("Caller scope is "+scope);
			String id=Storage.storeFile(toExport.getAbsolutePath(), false,"Aquamaps service,SpeciesByFilter CSV");			
			logger.trace("Storage id is : "+id);
			return id;
		} catch (Exception e){
			logger.error("General Exception, unable to serve request",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}finally{
			if(toExport!=null)FileUtils.delete(toExport);
		}
	}


	
}
