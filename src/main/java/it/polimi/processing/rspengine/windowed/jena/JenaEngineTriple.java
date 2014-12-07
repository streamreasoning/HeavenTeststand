package it.polimi.processing.rspengine.windowed.jena;

import it.polimi.processing.collector.ResultCollector;
import it.polimi.processing.events.RSPEvent;
import it.polimi.processing.events.TripleContainer;
import it.polimi.processing.events.interfaces.EventResult;
import it.polimi.processing.rspengine.windowed.jena.events.StatementEvent;
import it.polimi.processing.rspengine.windowed.jena.events.TripleEvent;
import it.polimi.utils.RDFSUtils;

import com.espertech.esper.client.UpdateListener;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public class JenaEngineTriple extends JenaEngine {

	public JenaEngineTriple(String name, ResultCollector<EventResult> collector, UpdateListener listener) {
		super(name, collector, listener, StatementEvent.class);
	}

	@Override
	protected void handleEvent(RSPEvent e) {
		for (TripleContainer tc : e.getEventTriples()) {
			String[] t = tc.getTriple();
			Resource subject = ResourceFactory.createResource(t[0]);
			Property predicate = (t[1] != RDFSUtils.TYPE_PROPERTY) ? ResourceFactory.createProperty(t[1]) : RDF.type;
			RDFNode object = ResourceFactory.createResource(t[2]);
			cepRT.sendEvent(new TripleEvent(subject, predicate, object, cepRT.getCurrentTime(), System.currentTimeMillis()));
		}
	}

}