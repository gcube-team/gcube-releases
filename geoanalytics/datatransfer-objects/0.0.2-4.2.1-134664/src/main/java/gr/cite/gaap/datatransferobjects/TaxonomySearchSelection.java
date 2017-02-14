package gr.cite.gaap.datatransferobjects;

import java.util.Collections;
import java.util.List;

public class TaxonomySearchSelection {
	public List<String> taxonomyNames = Collections.emptyList();
	public List<String> termNames = Collections.emptyList();
	public boolean activeTaxonomies;
	public boolean activeTerms;

	public List<String> getTaxonomyNames() {
		return taxonomyNames;
	}

	public void setTaxonomyNames(List<String> taxonomyNames) {
		this.taxonomyNames = taxonomyNames;
	}

	public List<String> getTermNames() {
		return termNames;
	}

	public void setTermNames(List<String> termNames) {
		this.termNames = termNames;
	}

	public boolean isActiveTaxonomies() {
		return activeTaxonomies;
	}

	public void setActiveTaxonomies(boolean activeTaxonomies) {
		this.activeTaxonomies = activeTaxonomies;
	}

	public boolean isActiveTerms() {
		return activeTerms;
	}

	public void setActiveTerms(boolean activeTerms) {
		this.activeTerms = activeTerms;
	}

}
