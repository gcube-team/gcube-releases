package org.gcube.application.aquamaps.aquamapsservice.client.proxies;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Envelope;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.MapsStub;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.AquaMap;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.CalculateEnvelopeFromCellSelectionRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.CalculateEnvelopeRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GetSpeciesByFiltersRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GetSpeciesEnvelopeRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.utils.Storage;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;

public class DefaultMaps implements Maps {

	private final ProxyDelegate<MapsStub> delegate;

	public DefaultMaps(ProxyDelegate<MapsStub> delegate) {
		this.delegate = delegate;
	}
	
	
	//********************** OPERATIONS
	
	@Override
	public Envelope calculateEnvelope(BoundingBox bb, List<Area> areas,
			final String speciesId, boolean useBottom, boolean useBounding,
			boolean useFAO) throws RemoteException, Exception {
		final CalculateEnvelopeRequestType request=new CalculateEnvelopeRequestType();
		request.boundingEast(bb.getE());
		request.boundingNorth(bb.getN());
		request.boundingSouth(bb.getS());
		request.boundingWest(bb.getW());

		StringBuilder areaSelection=new StringBuilder();
		for(Area a: areas) areaSelection.append(a.getCode()+",");
		areaSelection.deleteCharAt(areaSelection.lastIndexOf(","));
		request.faoAreas(areaSelection.toString());

		request.speciesID(speciesId);
		request.useBottomSeaTempAndSalinity(useBottom);
		request.useBounding(useBounding);
		request.useFAO(useFAO);
		
		Call<MapsStub,Envelope> call=new Call<MapsStub, Envelope>() {
			@Override
			public Envelope call(MapsStub pt)
					throws Exception {
				Species s=new Species(speciesId);
				s.getAttributesList().addAll(pt.calculateEnvelope(request).theList());
				return s.extractEnvelope();
			}
		};
		return delegate.make(call);
	}
	
	
	@Override
	public Envelope calculateEnvelopeFromCellSelection(List<String> cellIds,
			final String speciesId) throws RemoteException, Exception {
		
		final CalculateEnvelopeFromCellSelectionRequestType request=new CalculateEnvelopeFromCellSelectionRequestType();
		request.cellIds(new StringArray(cellIds));
		request.speciesID(speciesId);
		
		Call<MapsStub,Envelope> call=new Call<MapsStub, Envelope>() {
			@Override
			public Envelope call(MapsStub pt)
					throws Exception {
				Species s=new Species(speciesId);
				s.getAttributesList().addAll(pt.calculateEnvelopefromCellSelection(request).theList());
				return s.extractEnvelope();
			}
		};
		return delegate.make(call);
	}
	
	@Override
	public int deleteSubmitted(final List<Integer> ids) throws RemoteException,
			Exception {
		Call<MapsStub,Integer> call=new Call<MapsStub, Integer>() {
			@Override
			public Integer call(MapsStub pt)
					throws Exception {
				StringArray array=new StringArray();
				for(int i=0;i<ids.size();i++) 
					array.items().add(String.valueOf(ids.get(i)));
				return pt.deleteSubmitted(array);
			}
		};
		return delegate.make(call);
	}
	
	@Override
	public void markSaved(final List<Integer> submittedIds) throws RemoteException,
			Exception {
		Call<MapsStub, Object> call=new Call<MapsStub, Object>() {
			
			@Override
			public Object call(MapsStub endpoint) throws Exception {
				List<String> ids=new ArrayList<String>();
				for(Integer id:submittedIds)ids.add(String.valueOf(id));
				endpoint.markSaved(new StringArray(ids));
				return null;
			}
		};
		delegate.make(call);
	}
	
	@Override
	public void submitJob(final Job toSubmit) throws RemoteException, Exception {
		Call<MapsStub,Object> call=new Call<MapsStub, Object>() {
			
			@Override
			public Object call(MapsStub endpoint) throws Exception {
				endpoint.submitJob(toSubmit.toStubsVersion());
				return null;
			}
		};
		delegate.make(call);
	}
	
	
	
	//****************************** JSON
	
//	@Override
//	public String getJSONSubmitted(String userName, boolean showObjects,
//			String date, Integer jobId, SubmittedStatus status,
//			ObjectType objType, PagedRequestSettings settings)
//			throws RemoteException, Exception {
//		
//		final GetAquaMapsPerUserRequestType request=new GetAquaMapsPerUserRequestType();
//		request.setUserID(userName);
//		request.setAquamaps(showObjects);
//		request.setDateEnabled(date!=null);
//		if(date!=null)request.setDateValue(date);
//		request.setJobIdEnabled(jobId!=null);
//		if(jobId!=null)request.setJobIdValue(jobId);
//		request.setJobStatusEnabled(false);
//		request.setJobStatusValue(null);
//		request.setObjectStatusEnabled(status!=null);
//		if(status!=null)request.setObjectStatusValue(status.toString());
//		request.setPagedRequestSettings(settings);
//		request.setTypeEnabled(objType!=null);
//		if(objType!=null)request.setTypeValue(objType.toString());
//		
//		Call<MapsStub,String> call=new Call<MapsStub, String>() {
//			@Override
//			public String call(MapsStub pt)
//					throws Exception {
//				return pt.getAquaMapsPerUser(request);
//			}
//		};
//		return delegate.make(call);
//	}
//	
//	@Override
//	public String getJSONOccurrenceCells(String speciesId,
//			PagedRequestSettings settings) throws RemoteException, Exception {
//		final GetOccurrenceCellsRequestType request= new GetOccurrenceCellsRequestType();
//		request.setSpeciesID(speciesId);
//		request.setPagedRequestSettings(settings);
//		
//		Call<MapsStub,String> call=new Call<MapsStub, String>() {
//			@Override
//			public String call(MapsStub pt)
//					throws Exception {
//				return pt.getOccurrenceCells(request);
//			}
//		};
//		return delegate.make(call);
//	}
//	
//	@Override
//	public String getJSONResources(PagedRequestSettings settings,
//			List<Field> filter) throws RemoteException, Exception {
//		final GetResourceListRequestType request=new GetResourceListRequestType();
//		request.setFilters(Field.toStubsVersion(filter));
//		request.setPagedRequestSettings(settings);
//		Call<MapsStub,String> call=new Call<MapsStub, String>() {
//			@Override
//			public String call(MapsStub pt)
//			throws Exception {
//				return pt.getResourceList(request);
//			}
//		};
//		return delegate.make(call);
//	}
	
	
	@Override
	public String getJSONSpecies(int hspenId, List<Filter> genericSearch,
			List<Filter> advancedFilters, PagedRequestSettings settings)
	throws RemoteException, Exception {
		final GetSpeciesByFiltersRequestType request=new GetSpeciesByFiltersRequestType();
		request.genericSearchFilters(Filter.toStubsVersion(genericSearch));
		request.specieficFilters(Filter.toStubsVersion(advancedFilters));
		request.hspen(hspenId);
		request.pagedRequestSettings(settings);
		Call<MapsStub,String> call=new Call<MapsStub, String>() {
			@Override
			public String call(MapsStub pt)
			throws Exception {
				return pt.getSpeciesByFilters(request);
			}
		};
		return delegate.make(call);
	}
	
//	@Override
//	public String getJSONPhilogeny(SpeciesOccursumFields level,
//			ArrayList<Field> filters, PagedRequestSettings settings)
//			throws RemoteException, Exception {
//		final GetPhylogenyRequestType request=new GetPhylogenyRequestType();
//		request.setFieldList(Field.toStubsVersion(filters));
//		request.setPagedRequestSettings(settings);
//		request.setToSelect(new Field(level+"","",FieldType.STRING).toStubsVersion());
//		
//		Call<MapsStub,String> call=new Call<MapsStub, String>() {
//			@Override
//			public String call(MapsStub pt)
//			throws Exception {
//				return pt.getPhylogeny(request);
//			}
//		};
//		return delegate.make(call);
//	}
	
	
	//******************************* LOAD OBJECT
	
	@Override
	public AquaMapsObject loadObject(final int objectId) throws RemoteException,
			Exception {
		
		Call<MapsStub,AquaMapsObject> call=new Call<MapsStub, AquaMapsObject>() {
			
			@Override
			public AquaMapsObject call(MapsStub endpoint)
					throws Exception {
				AquaMap returned=endpoint.getObject(objectId);
				return new AquaMapsObject(returned);
			}
		};
		
		return delegate.make(call);
	}
	

	
	@Override
	public Species loadEnvelope(final String speciesId, int hspenId)
			throws RemoteException, Exception {
		final GetSpeciesEnvelopeRequestType req=new GetSpeciesEnvelopeRequestType(speciesId, hspenId);
		Call<MapsStub,Species> call=new Call<MapsStub, Species>() {
			
			@Override
			public Species call(MapsStub endpoint) throws Exception {
				Species spec=new Species(speciesId);
				spec.getAttributesList().addAll(endpoint.getSpeciesEnvelop(req).theList());
				return spec;
			}
		};
		return delegate.make(call);
	}
	
	@Override
	public Submitted loadSubmittedById(final int id) throws RemoteException,
			Exception {
		Call<MapsStub,Submitted> call=new Call<MapsStub, Submitted>() {
			
			@Override
			public Submitted call(MapsStub endpoint) throws Exception {
				return new Submitted(endpoint.loadSubmittedById(id));
			}
		};
		return delegate.make(call);
	}
	
	
	@Override
	public File getCSVSpecies(int hspenId, List<Filter> genericSearch,
			List<Filter> advancedFilters) throws RemoteException, Exception {
		final GetSpeciesByFiltersRequestType request=new GetSpeciesByFiltersRequestType();
		request.genericSearchFilters(Filter.toStubsVersion(genericSearch));
		request.specieficFilters(Filter.toStubsVersion(advancedFilters));
		request.hspen(hspenId);
		
		Call<MapsStub,File> call= new Call<MapsStub, File>() {
			
			@Override
			public File call(MapsStub pt) throws Exception {
				String locator=pt.getSpeciesByFiltersASCSV(request);						
				return Storage.getFileById(locator, true);
			}
		}; 
		return delegate.make(call);
	}
}
