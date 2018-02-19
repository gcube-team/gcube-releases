package gr.cite.regional.data.collection.dataaccess.types;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.compare.EqualsHelper;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class JSONType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.JAVA_OBJECT};
    }

    @Override
    public Class returnedClass() {
        return Map.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return EqualsHelper.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value == null ? null : new HashMap((Map) value);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

	@Override
	public Object nullSafeGet(ResultSet arg0, String[] arg1, SharedSessionContractImplementor arg2, Object arg3) throws HibernateException, SQLException {
        String json = arg0.getString(arg1[0]);

        if (StringUtils.isNotBlank(json)) {
            return JsonHelper.fromJson(json, Map.class);
        }

        return null;
    }

	@Override
	public void nullSafeSet(PreparedStatement arg0, Object arg1, int arg2, SharedSessionContractImplementor arg3) throws HibernateException, SQLException {
        if (arg1 != null) {
            if (arg1 instanceof Map && !((Map) arg1).isEmpty()) {
                String json = JsonHelper.toJson(arg1);
                PGobject pGobject = new PGobject();
                pGobject.setType("json");
                pGobject.setValue(json);
                arg0.setObject(arg2, pGobject, Types.OTHER);
                return;
            }
        }

        arg0.setObject(arg2, null);
    }
}