package gr.cite.geoanalytics.dataaccess.entities.sysconfig.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfig;

public interface SysConfigDao extends Dao<SysConfig, UUID>
{
	public List<SysConfig> findByClass(short configClass);
}
