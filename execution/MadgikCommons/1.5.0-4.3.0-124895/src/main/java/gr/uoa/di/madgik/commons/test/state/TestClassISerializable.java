package gr.uoa.di.madgik.commons.test.state;

import gr.uoa.di.madgik.commons.state.store.data.ISerializable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 *
 * @author gpapanikos
 */
public class TestClassISerializable implements ISerializable
{

	private static final long serialVersionUID = 4;
	private String field1 = "hello world from Serializable class";
	private int field2 = 5;
	private String field3 = TestClassISerializable.class.getName();

	/**
	 *
	 */
	public TestClassISerializable()
	{
	}

	@Override
	public String toString()
	{
		return field1 + " " + field2 + " " + field3;
	}

	/**
	 * 
	 * @return rgf
	 * @throws java.lang.Exception sdgf
	 */
	public byte[] Serialize() throws Exception
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		dout.writeUTF(field1);
		dout.writeInt(field2);
		dout.writeUTF(field3);
		dout.flush();
		dout.close();
		bout.close();
		return bout.toByteArray();
	}

	/**
	 *
	 * @param array re
	 * @throws java.lang.Exception
	 */
	public void Deserialize(byte[] array) throws Exception
	{
		DataInputStream din = new DataInputStream(new ByteArrayInputStream(array));
		this.field1 = din.readUTF();
		this.field2 = din.readInt();
		this.field3 = din.readUTF();
		din.close();
	}
}
