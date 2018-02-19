
package org.gcube.data.spd.manager;

import java.util.HashSet;
import java.util.List;

import org.gcube.common.validator.ValidationError;
import org.gcube.common.validator.ValidatorFactory;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.plugin.fwk.util.Util;
import org.gcube.data.spd.plugin.fwk.writers.ResultElementWriterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaxonomyItemWriterManager extends ResultElementWriterManager<TaxonomyItem> {

	private static Logger logger = LoggerFactory.getLogger(TaxonomyItemWriterManager.class);
	
	private HashSet<String> idsSet;
	
	public TaxonomyItemWriterManager(String repositoryProvider) {
		super(repositoryProvider);
		this.idsSet = new HashSet<String>(400);
	}
	
	@Override
	protected TaxonomyItem _enrich(TaxonomyItem t) {
		t.setProvider(this.provider);
		t.setId(Util.keyEnrichment(this.provider, t.getId()));
		String refId = t.getStatus().getRefId();
		if (refId!=null)
			t.getStatus().setRefId(Util.keyEnrichment(this.provider, refId));
		
		TaxonomyItem parent = t.getParent();
		while (parent!=null){
			parent.setId(Util.keyEnrichment(this.provider, parent.getId()));
			refId = parent.getStatus().getRefId();
			if (refId!=null)
				parent.getStatus().setRefId(Util.keyEnrichment(this.provider, refId));
			parent = parent.getParent();
		}
		return t;
	}

	@Override
	public boolean filter(TaxonomyItem obj) {
		if (obj ==null){
			logger.trace("("+this.provider+") object null discarded ");
			return false;
		}
		
		List<ValidationError> errors = ValidatorFactory.validator().validate(obj);
		
		if (errors.size()>0){
			logger.warn("("+this.provider+") object discarded for the following reasons: "+errors);
			return false;
		}
		
		if (this.idsSet.contains(obj.getId())){			
			logger.trace("("+this.provider+") an item with id "+obj.getId()+" already found");
			return false;
		}
		else {
			this.idsSet.add(obj.getId());
			return true;
		}
	}

	
	
}
