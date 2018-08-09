package org.gcube.vremanagement.virtualplatform.image;

import java.net.URLClassLoader;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * An extension of {@link URLClassLoader} adopting the parent-last delegation model.
 * First, it looks for a class in the current class loader, then, if it is not found, in the parent
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
class ParentLastClassLoader extends URLClassLoader {

	static final PermissionCollection allPermissions = new PermissionCollection() {
		private static final long serialVersionUID = 482874725021998286L;
		/** The AllPermission permission */
		Permission allPermission = new AllPermission();

		/** A simple PermissionCollection that only has AllPermission */
		public void add(Permission permission) {
			// do nothing
		}

		public boolean implies(Permission permission) {
			return true;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Enumeration elements() {
			return new Enumeration() {
				int cur = 0;

				public boolean hasMoreElements() {
					return cur < 1;
				}

				public Object nextElement() {
					if (cur == 0) {
						cur = 1;
						return allPermission;
					}
					throw new NoSuchElementException();
				}
			};
		}
	};

	static {
		// do this to ensure the anonymous Enumeration class in
		// allPermissions is pre-loaded
		if (allPermissions.elements() == null)
			throw new IllegalStateException();
	}

	public ParentLastClassLoader(URL[] urls) {
		super(urls);
	}

	public ParentLastClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public ParentLastClassLoader(URL[] urls, ClassLoader parent,
			URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}

	public URL getResource(String name) {
		URL resource = findResource(name);
		if (resource == null) {
			ClassLoader parent = getParent();
			if (parent != null)
				resource = parent.getResource(name);
		}
		return resource;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
        return (loadClass(name, false));	
    }
	
	public synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		try {
		Class<?> clazz = findLoadedClass(name);
		if (clazz == null) {
			try {
				clazz = findClass(name);
			} catch (ClassNotFoundException e) {
				ClassLoader parent = getParent();
				if (parent != null)
					clazz = parent.loadClass(name);
				else
					clazz = getSystemClassLoader().loadClass(name);
			}
		}

		if (resolve)
			resolveClass(clazz);

		return clazz;

		} catch (ClassNotFoundException e) { System.err.println("Class "+ name+" not found");throw e;}
	}

	public PermissionCollection getPermissions(CodeSource codesource) {
		// we want to ensure that the framework has AllPermissions
		return allPermissions;
	}
}
