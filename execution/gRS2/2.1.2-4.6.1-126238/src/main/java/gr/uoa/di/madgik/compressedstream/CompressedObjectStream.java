package gr.uoa.di.madgik.compressedstream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressedObjectStream {

//	public static void writeObject(Serializable object, ObjectOutputStream out) throws IOException {
//		writeObject(object, out, false);
//	}
	public static void writeObject(Serializable object, ObjectOutputStream out) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedOutputStream bfos = new BufferedOutputStream(bos);
		GZIPOutputStream gz = new GZIPOutputStream(bfos);
//		DeflaterOutputStream gz = new DeflaterOutputStream(bos, new Deflater(3));
		
		
		ObjectOutputStream oos = new ObjectOutputStream(gz);

		oos.writeObject(object);
		gz.finish();
		oos.flush();

		byte[] compressed = bos.toByteArray();
		out.writeObject(compressed);

		bos.close();
		bfos.close();
		gz.close();
		oos.close();
	}

//	public static Serializable readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//		return readObject(in, false);
//	}
	public static Serializable readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		byte[] compressed = (byte[]) in.readObject();
		Serializable obj = null;

		ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
		BufferedInputStream bfis = new BufferedInputStream(bis);
		GZIPInputStream gz = new GZIPInputStream(bfis);
//		DeflaterInputStream gz = new DeflaterInputStream(bis, new Deflater(3));
		
		
		ObjectInputStream ois = new ObjectInputStream(gz);

		obj = (Serializable) ois.readObject();
		bis.close();
		bfis.close();
		gz.close();
		ois.close();

		return obj;
	}

}
