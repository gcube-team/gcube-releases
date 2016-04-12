package org.gcube.data.analysis.statisticalmanager.test.org.acme;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.junit.Test;

public class AngelasTest {


	
	@Test
	public void one() throws Exception {

		Container c = new Container(new Ext1());
		
		Writer writer = new StringWriter();
		
		JAXBContext ctx = JAXBContext.newInstance(Container.class);
		
		ctx.createMarshaller().marshal(c, writer);
		
		System.out.println(writer.toString());
		
		assertEquals(c,ctx.createUnmarshaller().unmarshal(new StringReader(writer.toString())));
		
	}
	
	
	
	@XmlRootElement
	static class Container {
		
		Container() {}
		
		public Container(Abstract f) {
			this.f=f;
		}
	
		@XmlElement
		Abstract f;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((f == null) ? 0 : f.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Container other = (Container) obj;
			if (f == null) {
				if (other.f != null)
					return false;
			} else if (!f.equals(other.f))
				return false;
			return true;
		}
		
		
		
		
	}
	
	@XmlSeeAlso({Ext1.class,Ext2.class})
	static class Abstract {
		
		@XmlElement
		int a=1;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + a;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Abstract other = (Abstract) obj;
			if (a != other.a)
				return false;
			return true;
		}
		
		
	}
	
	@XmlType
	static class Ext1 extends Abstract {

		@XmlElement
		int b=5;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + b;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Ext1 other = (Ext1) obj;
			if (b != other.b)
				return false;
			return true;
		}
		
		
	}

	@XmlType
	static class Ext2 extends Abstract {
		
		@XmlElement
		int c=10;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + c;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Ext2 other = (Ext2) obj;
			if (c != other.c)
				return false;
			return true;
		}
		
		
	}
}

