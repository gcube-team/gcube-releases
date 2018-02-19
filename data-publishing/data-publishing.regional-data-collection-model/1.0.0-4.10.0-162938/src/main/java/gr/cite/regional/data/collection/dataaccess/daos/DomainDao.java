package gr.cite.regional.data.collection.dataaccess.daos;

import java.util.List;

import gr.cite.regional.data.collection.dataaccess.entities.Domain;

public interface DomainDao extends Dao<Domain, Integer> {
	public List<Domain> getDomainByLabel(String domainName);
}
