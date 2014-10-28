package it.polimi.processing.rspengine.esper.noinheritanceonevents.nogenerics.ontology.properties;

import it.polimi.processing.rspengine.esper.noinheritanceonevents.nogenerics.ontology.classes.organization.University;
import it.polimi.processing.rspengine.esper.noinheritanceonevents.nogenerics.ontology.classes.person.Person;
import it.polimi.processing.rspengine.esper.noinheritanceonevents.nogenerics.rdfs.RDFResource;

public class UndergraduateDegreeFrom extends DegreeFrom {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UndergraduateDegreeFrom() {
		super("http://swat.cse.lehigh.edu/onto/univ-bench.owl#undergraduateDegreeFrom", "http://swat.cse.lehigh.edu/onto/univ-bench.owl#Person",
				"http://swat.cse.lehigh.edu/onto/univ-bench.owl#University", Person.class, University.class);
	}

	public UndergraduateDegreeFrom(String property, String domainValue, String rangeValue, Class<? extends RDFResource> domain,
			Class<? extends RDFResource> range) {
		super(property, domainValue, rangeValue, domain, range);
	}
}
