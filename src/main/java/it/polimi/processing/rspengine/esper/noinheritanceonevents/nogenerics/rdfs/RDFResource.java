package it.polimi.processing.rspengine.esper.noinheritanceonevents.nogenerics.rdfs;

import java.io.Serializable;

public class RDFResource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String value;

	public RDFResource(String object) {
		this.setValue(object);
	}

	@Override
	public String toString() {
		return "RDFResource [value=" + value + "]";
	}

	// TODO perche' non e' possibile inserire il parametro?
	public RDFClass getSuper() {
		System.out.println("Res " + this.getClass().getSuperclass());
		return new RDFClass(this.getClass());
	};

	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else if (!(obj instanceof RDFResource))
			return false;
		else {
			RDFResource other = (RDFResource) obj;
			return getValue().equals(other.getValue());
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
