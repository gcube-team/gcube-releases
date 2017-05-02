package gr.uoa.di.madgik.grs.record.field.mediation;

import java.io.IOException;
import gr.uoa.di.madgik.grs.record.GRS2RecordMediationException;
import gr.uoa.di.madgik.grs.record.field.Field;

/**
 * Utility class constructing a {@link MediatingInputStream} that accesses the payload of the provided {@link Field}
 * 
 * @author gpapanikos
 *
 */
public class MediationFactory
{
	/**
	 * Creates a {@link MediatingInputStream} over the payload of the provided {@link Field}
	 * 
	 * @param field the field whose payload needs to be accessed
	 * @return the constructed stream
	 * @throws IOException THe payload's input stream could not be created
	 * @throws GRS2RecordMediationException the mediation over the field's payload could not be initialized
	 */
	public static MediatingInputStream getStream(Field field) throws IOException, GRS2RecordMediationException
	{
		return new MediatingInputStream(field);
	}
}
