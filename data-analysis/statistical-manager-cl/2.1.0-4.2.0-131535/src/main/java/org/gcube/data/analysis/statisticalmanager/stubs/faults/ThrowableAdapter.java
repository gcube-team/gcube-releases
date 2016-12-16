package org.gcube.data.analysis.statisticalmanager.stubs.faults;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ThrowableAdapter extends XmlAdapter<String, Throwable> {
    private HexBinaryAdapter hexAdapter = new HexBinaryAdapter();

    @Override
    public String marshal(Throwable v) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(v);
        oos.close();
        byte[] serializedBytes = baos.toByteArray();
        return hexAdapter.marshal(serializedBytes);
    }

    @Override
    public Throwable unmarshal(String v) throws Exception {
        byte[] serializedBytes = hexAdapter.unmarshal(v);
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Throwable result = (Throwable) ois.readObject();
        return result;
    }
}
