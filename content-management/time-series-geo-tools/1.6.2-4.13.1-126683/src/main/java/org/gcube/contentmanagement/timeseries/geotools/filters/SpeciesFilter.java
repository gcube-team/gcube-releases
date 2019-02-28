package org.gcube.contentmanagement.timeseries.geotools.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.finder.GeoAreaFinder;
import org.gcube.contentmanagement.timeseries.geotools.finder.ProbabilityFilter;
import org.gcube.contentmanagement.timeseries.geotools.finder.SpeciesConverter;
import org.gcube.contentmanagement.timeseries.geotools.finder.TimeSeriesAggregator;
import org.gcube.contentmanagement.timeseries.geotools.representations.GISLayer;
import org.gcube.contentmanagement.timeseries.geotools.utils.Couple;

public class SpeciesFilter extends AFilter{

	ProbabilityFilter probabilityFilter;
	SpeciesConverter speciesConverter;
	ConnectionsManager connectionsManager;
	String currentElement;
	Map<String,String> species2FishCode;
	
	public SpeciesFilter(String timeSeriesName, String mainColumn, String optionalColumn, String quantitiesColumn) {
		super(false, timeSeriesName, mainColumn, optionalColumn, quantitiesColumn);
		this.filtername = "species_and_space";
	}
	
	@Override
	public List<GISLayer> filter(GISLayer previousGisLayer, ConnectionsManager connectionsManager, GeoAreaFinder geofind) throws Exception {
		
		if (aggregationColumn == null || informationColumn == null || quantitiesColumn == null || timeSeriesName == null)
			throw new Exception("Inconsistent Input");
		
		this.connectionsManager = connectionsManager;
		initFilter();
		//initialize the returning list
		List<GISLayer> layersList = new ArrayList<GISLayer>();
		
		// get the squares by means of the finder
		if (geofind == null)
			geofinder = new GeoAreaFinder(connectionsManager,ref_country);
		else
			geofinder = geofind;
		
		//aggregate the Time Series
		TimeSeriesAggregator aggregator = new TimeSeriesAggregator(connectionsManager);
		Map<String,List<Couple>> aggregation = aggregator.aggregateTimeSeries(aggregationColumn, informationColumn, quantitiesColumn, timeSeriesName);
		//aggregation result: species1->(place1,quant1),(place2,quant2) ...
		//A Map for recycling the yet transformed squares
		HashMap <String,List<String>> placeToCsquares = new HashMap<String, List<String>>();
		
		long totalNumber = aggregation.size();
		int counter = 0;
		AnalysisLogger.getLogger().trace("SpeciesFilter:->TOTAL NUMBER OF SPECIES :"+totalNumber);
		//build up a series of GIS layers
		//each layer will be for any single species
		for (String species:aggregation.keySet()){
			double max = -Double.MAX_VALUE;
			double min = Double.MAX_VALUE;
			currentElement = species;
			
			AnalysisLogger.getLogger().trace("SpeciesFilter:->ELEMENT UNDER ANALYSIS :"+species);
			
			String layername = "layr_"+(species.substring(0,Math.min(5, species.length()))+ "_" + UUID.randomUUID()).replace("-", "_").replace(" ", "_").replace(",","").replace(".","").replace(":","").replace(";","").replace("<","").replace(">","").replace("=","");
			
			//create a new gis layer with the name of the species
			GISLayer layer = new GISLayer(layername.toLowerCase());
			layer.setLayerTitle(filtername);
			
			//for this GIS layer, take the csquarecodes associated to each place 
			List<Couple> placesQuantities = aggregation.get(species);
			//add csquares and quantities
			for (Couple couple:placesQuantities){
				//check if the csquares have been found previously for this place
				String place = couple.getFirst();
				//get the associated value
				Double value = 0.00;
				try{value = Double.parseDouble(couple.getSecond());}catch(Exception e1){}
				if (value > max)
					max = value;
				if (value < min)
					min = value;
				
				List<String> csquares = placeToCsquares.get(place);
				//otherwise get them by a finder
				if (csquares==null){
					List<String> prevsquares = null;
					if (previousGisLayer != null)
						prevsquares = previousGisLayer.getCsquareCodes();
					
					csquares = findCSquareCodes(place,prevsquares);
					if (csquares!= null)
						placeToCsquares.put(place,csquares);
				}
				if ((csquares!= null) && (csquares.size()>0)){
					//postFilter squares - by using aquamaps probabilities
					csquares = postFilterSquare(csquares);
					//add the csquare codes and value to the layer
					layer.appendListofSquares(csquares, value,species);
				}
				
			}
			
			if (max == min)
				max = max+1;
			
			layer.setMax(max);
			layer.setMin(min);
			layer.setValuesColumnName(valuesColumnName);
			layersList.add(layer);
			
			counter++;
			status = (float)counter/totalNumber;
			overallSpeciesCounter ++;
		}
		status = 100f;
		
		AnalysisLogger.getLogger().trace("SpeciesFilter:->OVERALL PROCESSED SPECIES: "+overallSpeciesCounter);
		
		AnalysisLogger.getLogger().trace("SpeciesFilter:->NO TRANSFORMED SPECIES: "+noCodeCounter+" -- "+(MathFunctions.roundDecimal((double)noCodeCounter*100.00/(double)overallCalculationsCounter,2))+"%");
		AnalysisLogger.getLogger().trace("SpeciesFilter:->NO AREA FOUND IN COUNTRY FOR THAT SPECIES: "+noCsquaresCounter+" -- "+(MathFunctions.roundDecimal((double)noCsquaresCounter*100.00/(double)overallCalculationsCounter,2)+"%"));
		
		return layersList;
	}

	@Override
	public void initFilter() {
		probabilityFilter = new ProbabilityFilter(connectionsManager);
		speciesConverter = new SpeciesConverter(connectionsManager,ref_species);
		species2FishCode = new HashMap<String, String>();
	}

	
	int noCodeCounter;
	int noCsquaresCounter;
	int CsquaresFiltersCounter;
	int overallSpeciesCounter;
	int overallCalculationsCounter;
	@Override
	public List<String> postFilterSquare(List<String> csquares) {
		overallCalculationsCounter++;
		String fishcode = "";
		try{
			String cachedfishcode = species2FishCode.get(currentElement);
			if (cachedfishcode!=null){
				fishcode = cachedfishcode;
//				AnalysisLogger.getLogger().warn("\tpostFilterSquare-> Fish "+currentElement+" yet converted to "+fishcode);
			}
			else{
				fishcode = speciesConverter.speciesName2FishCode(currentElement);
				AnalysisLogger.getLogger().trace("\tpostFilterSquare-> Fish "+currentElement+" converted to "+fishcode);
				species2FishCode.put(currentElement,fishcode);
			}
			
		}catch(Exception e){
			
//			e.printStackTrace();
			species2FishCode.put(currentElement,fishcode);
			AnalysisLogger.getLogger().trace("\tpostFilterSquare-> ERROR - Impossible to convert fish name "+currentElement);
		}

		
		List<String> squares = null;
		if (fishcode.length()>0){
			squares = probabilityFilter.FilterOnProbability(csquares, fishcode);
			
		}
		else{
			noCodeCounter++;
		}
		
		if ((squares!=null) && (squares.size()>0)){
			
			AnalysisLogger.getLogger().trace("\tpostFilterSquare-> FILTER HAS BEEN CORRECTLY APPLIED TO "+fishcode);
			return squares;
		}
		else{
			noCsquaresCounter++;
			AnalysisLogger.getLogger().trace("\tpostFilterSquare-> PROBABILITIES FILTER HAS NOT BEEN APPLIED!");
			return csquares;
		}
	}

	
	
	
}
