package it.polimi.processing.rspengine.esper.noinheritanceonevents.nogenerics.ontology.classes.publication;

import it.polimi.processing.rspengine.esper.noinheritanceonevents.nogenerics.rdfs.RDFResource;

public class Publication extends RDFResource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5656848603500876298L;

	public Publication(String object) {
		super(object);
	}

	public Publication() {
		super("http://swat.cse.lehigh.edu/onto/univ-bench.owl#Publication");
	}
}