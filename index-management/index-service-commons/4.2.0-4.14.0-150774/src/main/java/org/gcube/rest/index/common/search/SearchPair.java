package org.gcube.rest.index.common.search;

public class SearchPair {

	private String searchKey;
	private String searchValue;
	
	public SearchPair(String searchKey, String searchValue) {
		super();
		this.searchKey = searchKey;
		this.searchValue = searchValue;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	@Override
	public String toString() {
		return "SearchPair [searchKey=" + searchKey + ", searchValue=" + searchValue + "]";
	}
	
	
}
