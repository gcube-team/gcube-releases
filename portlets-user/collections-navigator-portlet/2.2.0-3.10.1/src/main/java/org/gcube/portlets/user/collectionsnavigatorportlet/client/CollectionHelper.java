package org.gcube.portlets.user.collectionsnavigatorportlet.client;

public class CollectionHelper {
	
	public static native String getCollectionHierarchy(int i, int j)/*-{
	return $wnd.collectionHierarchy[i][j];
	}-*/;
	
	public static native int getCollectionHierarchyLenght()/*-{
	return $wnd.collectionHierarchy.length;
	}-*/;
	
	public static native int getCollectionHierarchySubLength(int i)/*-{
	return $wnd.collectionHierarchy[i].length;
	}-*/;
	
	public static native String getCollections(int i, int j)/*-{
	return $wnd.collections[i][j];
	}-*/;
	
	public static native int getCollectionsLength()/*-{
	return $wnd.collections.length;
	}-*/;
	
	public static native int getCollectionsSubLength(int i)/*-{
	return $wnd.collections[i].length;
	}-*/;
	
	public static native String getRootTitle()/*-{
	return $wnd.rootTitle;
	}-*/;	
	
	public static native String getCurrentSchema()/*-{
	return $wnd.currentSchema;
	}-*/;	
		
	public static native String getAllCollectionsSelected()/*-{
	return $wnd.allCollections;
	}-*/;

}
