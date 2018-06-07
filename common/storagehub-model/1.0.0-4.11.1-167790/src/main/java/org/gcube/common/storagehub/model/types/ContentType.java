package org.gcube.common.storagehub.model.types;

public enum ContentType {
	GENERAL {
		@Override
		public String toString() {
			return "nthl:file";
		}
	},
	IMAGE {
		@Override
		public String toString() {
			return "nthl:image";
		}
	},
	PDF {
		@Override
		public String toString() {
			return "nthl:pdf";
		}
	},
	TS {
		@Override
		public String toString() {
			return "nthl:timeSeriesItemContent";
		}
	}, 
	QUERY {
		@Override
		public String toString() {
			return "nthl:queryItemContent";
		}
	},
	REPORT {
		@Override
		public String toString() {
			return "nthl:reportItemContent";
		}
	},
	REPORT_TEMPLATE {
		@Override
		public String toString() {
			return "nthl:reportTemplateContent";
		}
	}, 
	METADATA{
		@Override
		public String toString() {
			return "nthl:metadataItemContent";
		}
	}, 
	DOCUMENT{
		@Override
		public String toString() {
			return "nthl:documentItemContent";
		}
	},
	SMART{	
		@Override
		public String toString() {
			return "nthl:smartFolderContent";
		}
	}
	
}
