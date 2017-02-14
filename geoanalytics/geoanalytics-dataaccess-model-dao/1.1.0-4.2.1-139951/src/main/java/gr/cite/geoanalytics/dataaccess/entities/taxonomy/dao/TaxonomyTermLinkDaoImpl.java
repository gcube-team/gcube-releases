package gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermLink;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermLinkPK;

@Repository
public class TaxonomyTermLinkDaoImpl extends JpaDao<TaxonomyTermLink, TaxonomyTermLinkPK> implements TaxonomyTermLinkDao
{

	@Override
	public TaxonomyTermLink loadDetails(TaxonomyTermLink ttl) {
		ttl.getCreator().getName();
		ttl.getDestinationTerm().getId();
		ttl.getSourceTerm().getId();
		return ttl;
	}

}
