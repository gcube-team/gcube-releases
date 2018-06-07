/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: GenericResourcePlugin.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.shared.plugins;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class GenericResourcePlugin implements Serializable, IsSerializable {
	public enum FieldType implements Serializable, IsSerializable {
		string(),
		number(),
		XML();
	}

	public static class Field implements Serializable, IsSerializable {
		private static final long serialVersionUID = 5921865866801474305L;
		private FieldType type = null;
		private String name = null;
		private boolean required = true;
		private String label = null;
		private String defaultValue = "";

		/**
		 * @deprecated for serialization only
		 */
		public Field() {
		}

		public Field(final String name, final FieldType type) {
			this(name, type, true);
		}

		public Field(final String name, final FieldType type, final boolean required) {
			this(name, null, type, required);
		}

		public Field(final String name, final String label, final FieldType type, final boolean required) {
			this.setName(name);
			this.setLabel(label);
			this.type = type;
			this.setIsRequired(required);
		}

		public final String getDefaultValue() {
			return this.defaultValue;
		}

		public final void setDefaultValue(final String defaultValue) {
			if (defaultValue != null && defaultValue.trim().length() > 0) {
				this.defaultValue = defaultValue;
			}
		}

		public final void setLabel(final String label) {
			if (label != null && label.trim().length() > 0) {
				this.label = label.trim();
			} else {
				this.label = name;
			}
		}

		public final String getLabel() {
			if (this.label == null || this.label.trim().length() == 0) {
				return this.name;
			} else {
				return this.label;
			}
		}

		private void setName(final String name) {
			if (name != null) {
				this.name = name.trim();
			}
		}

		/**
		 * Corresponds to the tag name in the body
		 * @return
		 */
		public final String getName() {
			return this.name;
		}

		public final FieldType getType() {
			return this.type;
		}

		public final void setType(final FieldType type) {
			this.type = type;
		}

		public final boolean isRequired() {
			return this.required;
		}

		public final void setIsRequired(final boolean required) {
			this.required = required;
		}
	}

	private static final long serialVersionUID = 6070331744211410508L;
	private String name = null;
	private String description = null;
	private String type = null;
	private List<Field> params = new Vector<Field>();
//	private String namespace = null;
	private String namespace = "xmlns:ns4=\"http://gcube-system.org/namespaces/data/oaiplugin\"";


	/**
	 * @deprecated for serialization only
	 */
	public GenericResourcePlugin() {
	}


	public GenericResourcePlugin(final String name, final String namespace, final String description, final String type) {
		super();
		this.name = name;
		this.description = description;
		this.type = type;
		if (namespace != null && namespace.trim().length() > 0) {
			this.namespace = "xmlns:ns4=\"" + namespace.trim() + "\"";
		}
	}

	public final void addParam(final Field param) {
		this.params.add(param);
	}


	public final String getName() {
		return name;
	}

	public final String getDescription() {
		return description;
	}


	public final String getType() {
		return type;
	}

	public final String getNamespace() {
		return namespace;
	}

	public final List<Field> getParams() {
		return params;
	}
}
