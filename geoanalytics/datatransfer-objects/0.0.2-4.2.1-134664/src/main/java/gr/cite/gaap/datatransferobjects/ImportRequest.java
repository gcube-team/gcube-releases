package gr.cite.gaap.datatransferobjects;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ImportRequest {
	private String crs = null;
	private String token = null;
	private String dbfCharset = null;
	private boolean forceLonLat = false;
	private String taxonomyTermTaxonomy = null;
	private String taxonomyTerm = null;
	private String boundaryTermTaxonomy = null;
	private String boundaryTerm = null;
	private boolean replace = false;
	private boolean merge = false;
	private List<AttributeInfo> attributeConfig = Collections.emptyList();
	private String geographyTaxonomy = null;

	public String getCrs() {
		return crs;
	}

	public void setCrs(String crs) {
		this.crs = crs;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDbfCharset() {
		return dbfCharset;
	}

	public void setDbfCharset(String dbfCharset) {
		this.dbfCharset = dbfCharset;
	}

	public boolean getForceLonLat() {
		return forceLonLat;
	}

	public void setForceLonLat(boolean forceLonLat) {
		this.forceLonLat = forceLonLat;
	}

	public String getTaxonomyTermTaxonomy() {
		return taxonomyTermTaxonomy;
	}

	public void setTaxonomyTermTaxonomy(String taxonomyTermTaxonomy) {
		this.taxonomyTermTaxonomy = taxonomyTermTaxonomy;
	}

	public String getTaxonomyTerm() {
		return taxonomyTerm;
	}

	public void setTaxonomyTerm(String taxonomyTerm) {
		this.taxonomyTerm = taxonomyTerm;
	}

	public String getBoundaryTermTaxonomy() {
		return boundaryTermTaxonomy;
	}

	public void setBoundaryTermTaxonomy(String boundaryTermTaxonomy) {
		this.boundaryTermTaxonomy = boundaryTermTaxonomy;
	}

	public String getBoundaryTerm() {
		return boundaryTerm;
	}

	public void setBoundaryTerm(String boundaryTerm) {
		this.boundaryTerm = boundaryTerm;
	}

	public List<AttributeInfo> getAttributeConfig() {
		return attributeConfig;
	}

	public void setAttributeConfig(List<AttributeInfo> attributeConfig) {
		this.attributeConfig = attributeConfig;
	}

	public boolean isReplace() {
		return replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}

	public boolean isMerge() {
		return merge;
	}

	public void setMerge(boolean merge) {
		this.merge = merge;
	}

	public String getGeographyTaxonomy() {
		return geographyTaxonomy;
	}

	public void setGeographyTaxonomy(String geographyTaxonomy) {
		this.geographyTaxonomy = geographyTaxonomy;
	}
}
