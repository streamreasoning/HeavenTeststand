package it.polimi.processing.rspengine.windowed.jena;

import it.polimi.processing.collector.ResultCollector;
import it.polimi.processing.events.RSPEvent;
import it.polimi.processing.events.TripleContainer;
import it.polimi.processing.events.interfaces.EventResult;
import it.polimi.processing.rspengine.windowed.jena.events.GraphEvent;
import it.polimi.utils.RDFSUtils;

import com.espertech.esper.client.UpdateListener;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * In this example rdfs property of subclass of is exploited by external static
 * functions which can be called form EPL No data or time windows are considered
 * in event consuming, se the related example for that time is externally
 * controlled all event are sent in the samte time interval
 * 
 * the query doesn't include joins
 * 
 * events are pushed, on incoming events, in 3 differents queue which are pulled
 * by refering statements
 * 
 * **/
public class JenaEngineGraph extends JenaEngine {

	Graph abox;

	public JenaEngineGraph(String name, ResultCollector<EventResult> collector, UpdateListener listener) {
		super(name, collector, listener, GraphEvent.class);
	}

	@Override
	protected void handleEvent(RSPEvent e) {
		abox = ModelFactory.createMemModelMaker().createDefaultModel().getGraph();
		for (TripleContainer tc : e.getEventTriples()) {
			String[] t = tc.getTriple();
			abox.add(createTriple(t));
		}
		cepRT.sendEvent(new GraphEvent(abox, cepRT.getCurrentTime(), System.currentTimeMillis()));
	}

	private Triple createTriple(String[] eventTriple) {
		Resource subject = ResourceFactory.createResource(eventTriple[0]);
		Property predicate = (eventTriple[1] != RDFSUtils.TYPE_PROPERTY) ? ResourceFactory.createProperty(eventTriple[1]) : RDF.type;
		RDFNode object = ResourceFactory.createResource(eventTriple[2]);
		return new Triple(subject.asNode(), predicate.asNode(), object.asNode());
	}

}