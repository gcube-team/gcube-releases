package org.gcube.common.homelibary.model.items;

public enum MetadataProperty {

	WORKSPACE_ID{
		@Override
		public String toString() {
			return  "workspaceID";
		}
	},
	IS_PUBLIC{
		@Override
		public String toString() {
			return  "isPublic";
		}
	};
}
