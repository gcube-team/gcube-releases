//package gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Comparator;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.SortedMap;
//import java.util.TreeMap;
//
//public class TreeMultiMap<K,V> extends TreeMap<K,List<V>> {
//
//	private static final long serialVersionUID = 1431824094992490719L;
//	
//	public TreeMultiMap() {
//		super();
//	}
//	
//	public TreeMultiMap(Comparator<? super K> c) {
//		super(c);
//	}
//	
//	public TreeMultiMap(Map<? extends K, ? extends List<V>> m) {
//		super(m);
//	}
//	
//	public TreeMultiMap(SortedMap<K, ? extends List<V>> m) {
//		super(m);
//	}
//	
//	@Override
//	public boolean containsValue(Object value) {
//
//		for(Map.Entry<K, List<V>> entry : super.entrySet())
//			if(entry.getValue().contains(value))
//				return true;
//		return false;
//	}
//	
//	//@Override
//	public List<V> put(K key, List<V> value) {
//		return super.put(key,value);
//	}
//	
//
//	public void put(K key, V value) {
//		List<V> l;
//		if((l = super.get(key)) == null)
//				super.put(key, l = new ArrayList<V>());
//		l.add(value);
//	}
//	
//	@Override
//	public List<V> remove(Object key) {
//		return super.remove(key);
//	}
//	
//	public V removeOne(Object key) {
//		if(!containsKey(key))
//			return null;
//		List<V> l = super.get(key);
//		if(l.size() == 1)
//			return super.remove(key).get(0);
//		return l.remove(0);
//	}
//	
//	public boolean remove(Object key, Object value) {
//		List<V> values = super.get(key);
//		if (values == null)
//			return false;
//		else 
//			return values.remove(value);
//	}
//	
//	@Override
//	public String toString() {
//		StringBuffer buff = new StringBuffer();
//		buff.append("{");
//		Iterator<K> keys = super.keySet().iterator();
//		boolean first = true;
//		while (keys.hasNext()) {
//			if (first) {
//				first = false;
//			} else {
//				buff.append(", ");
//			}
//			Object key = keys.next();
//			Collection<V> values = super.get(key);
//			buff.append("[" + key + ": " + values + "]");
//		}
//		buff.append("}");
//		return buff.toString();
//	}
//
//}
