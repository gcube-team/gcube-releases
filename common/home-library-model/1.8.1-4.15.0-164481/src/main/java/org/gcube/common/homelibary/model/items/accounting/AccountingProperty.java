/**
 * 
 */
package org.gcube.common.homelibary.model.items.accounting;



public enum AccountingProperty {

	USER{
		@Override
		public String toString() {
			return  "hl:user";
		}
	},
	DATE{
		@Override
		public String toString() {
			return  "hl:date";
		}
	},
	ITEM_NAME{
		@Override
		public String toString() {
			return  "hl:itemName";
		}
	},
	FROM_PATH{
		@Override
		public String toString() {
			return  "hl:fromPath";
		}
	},
	OLD_ITEM_NAME{
		@Override
		public String toString() {
			return  "hl:oldItemName";
		}
	},
	NEW_ITEM_NAME{
		@Override
		public String toString() {
			return  "hl:newItemName";
		}
	},
	MEMBERS{
		@Override
		public String toString() {
			return  "hl:members";
		}
	},
	FOLDER_ITEM_TYPE{
		@Override
		public String toString() {
			return  "hl:folderItemType";
		}
	},
	MIME_TYPE{
		@Override
		public String toString() {
			return  "hl:mimeType";
		}
	}, 
	ITEM_TYPE{
		@Override
		public String toString() {
			return  "hl:itemType";
		}
	},
	VERSION{
		@Override
		public String toString() {
			return  "hl:version";
		}
	},
}
