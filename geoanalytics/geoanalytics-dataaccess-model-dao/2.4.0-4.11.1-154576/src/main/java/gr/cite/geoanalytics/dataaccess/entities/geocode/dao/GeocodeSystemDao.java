package gr.cite.geoanalytics.dataaccess.entities.geocode.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;

public interface GeocodeSystemDao extends Dao<GeocodeSystem, UUID>
{
	 public List<GeocodeSystem> findByName(String name);
	 public List<GeocodeSystem> getActive();
	 public List<String> listNames();
	 public List<String> listNamesOfActive();
	 
	 public List<GeocodeSystem> getSiblings(GeocodeSystem t);
	 public List<GeocodeSystem> getInstances(GeocodeSystem t);
	 public List<GeocodeSystem> getInstancesByID(UUID taxonomyID);
	 
	 public List<Geocode> getGeocodes(GeocodeSystem t);
	 public List<Geocode> getActiveGeocodes(GeocodeSystem t);
	 public List<Geocode> getTopmostGeocodes(GeocodeSystem t);
	 public List<Geocode> getBottomGeocodes(GeocodeSystem t);
	 
	 public List<String> listGeocodes(GeocodeSystem t);
	 public List<String> listActiveGeocodes(GeocodeSystem t);	 
}
