package gr.cite.geoanalytics.dataaccess.typedefinition;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

/**
 * Currently not used
 *
 */
public class UUIDType implements UserType 
{
    private final int[] sqlTypesSupported = new int[] { Types.NUMERIC };
    private final String CAST_EXCEPTION_TEXT = " cannot be cast to a java.util.UUID"; 

    public int[] sqlTypes() 
    {
        return sqlTypesSupported;
    }

    @SuppressWarnings("rawtypes")
    public Class returnedClass() 
    {
        return UUID.class;
    }

    public boolean equals(Object x, Object y) throws HibernateException 
    {
        if (x == null) return y == null;
        else return x.equals(y);
    }

    public int hashCode(Object x) throws HibernateException 
    {
        return x == null ? null : x.hashCode();
    }    

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException 
    {
        assert(names.length == 1);
        Object value = rs.getObject(names[0]);
        if (value == null) return null;

        UUID uuid = UUID.fromString( rs.getString( names[0] ) );
        return rs.wasNull() ? null : uuid;
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException 
    {
        if (value == null) 
        {
            st.setNull(index, Types.NULL);
            return;
        } 

        if (!UUID.class.isAssignableFrom(value.getClass()))
            throw new HibernateException(value.getClass().toString() + CAST_EXCEPTION_TEXT);    

        UUID uuid = (UUID) value;
        st.setObject(index, uuid, Types.OTHER);
    }

    public Object deepCopy(Object value) throws HibernateException 
    {
        if (value == null) return null;
        UUID uuid = (UUID) value;
        return new UUID( uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() );
    }

    public boolean isMutable() 
    {
        return false;
    }

    public Serializable disassemble(Object value) throws HibernateException 
    {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException 
    {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException 
    {
        return original;
    }

//	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) 
//			throws HibernateException, SQLException 
//	{
//		return nullSafeGet(rs, names, owner);
//	}
//
//	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) 
//			throws HibernateException, SQLException 
//	{
//		nullSafeSet(st, value, index);
//		
//	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws HibernateException, SQLException {
		return nullSafeGet(rs, names, owner);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
			throws HibernateException, SQLException {
		nullSafeSet(st, value, index);
	}
}