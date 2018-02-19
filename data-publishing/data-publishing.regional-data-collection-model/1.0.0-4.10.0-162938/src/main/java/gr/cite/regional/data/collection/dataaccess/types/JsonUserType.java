package gr.cite.regional.data.collection.dataaccess.types;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;

public class JsonUserType implements UserType, ParameterizedType, Serializable {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	private static final String CLASS_TYPE = "classType";
	private static final String TYPE = "type";
	
	private static final int[] SQL_TYPES = new int[] { Types.JAVA_OBJECT };
	
	private Class<?> classType;
	private int sqlType = Types.JAVA_OBJECT;
	
	@Override
	public void setParameterValues(Properties params) {
		String classTypeName = params.getProperty(CLASS_TYPE);
		try {
			this.classType = ReflectHelper.classForName(classTypeName, this.getClass());
		} catch (ClassNotFoundException cnfe) {
			throw new HibernateException("classType not found", cnfe);
		}
		String type = params.getProperty(TYPE);
		if (type != null) {
			this.sqlType = Integer.decode(type);
		}
		
	}
	
	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return this.deepCopy(cached);
	}
	
	@Override
	public Object deepCopy(Object value) throws HibernateException {
		if (value != null) {
			try {
				return MAPPER.readValue(MAPPER.writeValueAsString(value), this.classType);
			} catch (IOException e) {
				throw new HibernateException("unable to deep copy object", e);
			}
		}
		return null;
	}
	
	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		try {
			return MAPPER.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new HibernateException("unable to disassemble object", e);
		}
	}
	
	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return Objects.equal(x, y);
	}
	
	@Override
	public int hashCode(Object x) throws HibernateException {
		return Objects.hashCode(x);
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
		String json = rs.getString(names[0]);
		if (json != null && !rs.wasNull()) {
			if (StringUtils.isNotBlank(json)) {
				return JsonHelper.fromJson(json, this.classType);
			}
		}
		
		return null;
		
		
		//Object obj = null;
		//if (this.sqlType == Types.CLOB || this.sqlType == Types.BLOB) {
		//	byte[] bytes = rs.getBytes(names[0]);
		//	if (bytes != null && !rs.wasNull()) {
		//		try {
		//			obj = MAPPER.readValue(bytes, this.classType);
		//		} catch (IOException e) {
		//			throw new HibernateException("unable to read object from result set", e);
		//		}
		//	}
		//} else {
		//	try {
		//		String content = rs.getString(names[0]);
		//		if (content != null && !rs.wasNull()) {
		//			obj = MAPPER.readValue(content, this.classType);
		//		}
		//	} catch (IOException e) {
		//		throw new HibernateException("unable to read object from result set", e);
		//	}
		//}
		//return obj;
	}
	
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, Types.NULL);
		} else {
			//if (value.getClass().equals(this.classType)) {
			if (this.classType.isInstance(value)) {
				String json = JsonHelper.toJson(value);
				PGobject pGobject = new PGobject();
				pGobject.setType("json");
				pGobject.setValue(json);
				st.setObject(index, pGobject, Types.OTHER);
			}
		}
		
		//if (value == null) {
		//	st.setNull(index, this.sqlType);
		//} else {
		//
		//	if (this.sqlType == Types.CLOB || this.sqlType == Types.BLOB) {
		//		try {
		//			st.setBytes(index, MAPPER.writeValueAsBytes(value));
		//		} catch (JsonProcessingException e) {
		//			throw new HibernateException("unable to set object to result set", e);
		//		}
		//	} else {
		//		try {
		//			st.setString(index, MAPPER.writeValueAsString(value));
		//		} catch (JsonProcessingException e) {
		//			throw new HibernateException("unable to set object to result set", e);
		//		}
		//	}
		//}
	}
	
	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return this.deepCopy(original);
	}
	
	@Override
	public Class<?> returnedClass() {
		return this.classType;
	}
	
	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}
}
