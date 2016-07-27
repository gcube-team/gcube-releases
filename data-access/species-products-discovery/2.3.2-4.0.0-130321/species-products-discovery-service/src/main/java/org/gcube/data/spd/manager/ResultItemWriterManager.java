package org.gcube.data.spd.manager;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.validator.ValidationError;
import org.gcube.common.validator.ValidatorFactory;
import org.gcube.data.spd.model.products.Product;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.Taxon;
import org.gcube.data.spd.plugin.fwk.util.Util;
import org.gcube.data.spd.plugin.fwk.writers.ResultElementWriterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ResultItemWriterManager extends ResultElementWriterManager<ResultItem> {

	
	private Logger logger = LoggerFactory.getLogger(ResultItemWriterManager.class);
	private Set<String> idsSet;
	
		
	public ResultItemWriterManager(String repositoryProvider) {
		super(repositoryProvider);
		this.idsSet = new HashSet<String>(400);
		Collections.synchronizedSet(this.idsSet);
	}

	@Override
	protected ResultItem _enrich(ResultItem t) {
		t.setProvider(this.provider);
		t.setId(Util.keyEnrichment(this.provider, t.getId()));
		Taxon parent = t.getParent();
		while (parent!=null){
			parent.setId(Util.keyEnrichment(this.provider, parent.getId()));
			parent = parent.getParent();
		}
						
		if (t.getProducts()!=null)
			for (Product prod: t.getProducts())
				prod.setKey(Util.keyEnrichment(this.provider, prod.getKey()));
		return t;
	}

	@Override
	public synchronized boolean filter(ResultItem obj) {

		if (obj ==null){
			logger.trace("("+this.provider+") object null discarded ");
			return false;
		}

		List<ValidationError> errors = ValidatorFactory.validator().validate(obj);

		if (errors.size()>0){
			logger.warn("("+this.provider+") object discarded for the following reasons: "+errors);
			return false;
		}

		String tempId = this.provider+"|"+obj.getId()+"|"+obj.getDataSet().getId()+"|"+obj.getDataSet().getDataProvider().getId();
		if (idsSet.contains(tempId)){
			logger.trace("("+this.provider+") an item with id "+obj.getId()+" already found");
			return false;
		}else{
			idsSet.add(tempId);
			return true;
		}
	}

}
