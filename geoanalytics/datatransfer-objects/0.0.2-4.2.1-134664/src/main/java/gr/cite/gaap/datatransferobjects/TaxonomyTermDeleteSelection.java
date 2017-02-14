package gr.cite.gaap.datatransferobjects;

import java.util.Collections;
import java.util.List;

public class TaxonomyTermDeleteSelection {
	public List<TaxonomyTermInfo> terms = Collections.emptyList();

	public List<TaxonomyTermInfo> getTerms() {
		return terms;
	}

	public void setTerms(List<TaxonomyTermInfo> terms) {
		this.terms = terms;
	}
}
