package it.polimi.processing.rspengine.windowed.jena.timekeeping.external.snapshot;

import it.polimi.processing.ets.core.EventProcessor;
import it.polimi.processing.events.RSPTripleSet;
import it.polimi.processing.events.TripleContainer;
import it.polimi.processing.events.interfaces.Event;
import it.polimi.processing.rspengine.windowed.jena.WindowUtils;
import it.polimi.processing.rspengine.windowed.jena.engine.abstracts.JenaEngine;
import it.polimi.processing.rspengine.windowed.jena.events.StatementEvent;
import it.polimi.utils.RDFSUtils;
import lombok.extern.log4j.Log4j;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.UpdateListener;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

@Log4j
public class JenaEngineStmt extends JenaEngine {

	public JenaEngineStmt(String name, EventProcessor<Event> collector, UpdateListener listener) {
		super(name, collector, listener, WindowUtils.JENA_INPUT_QUERY_SNAPTSHOT);

		cepConfig = new Configuration();
		cepConfig.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
		log.info("Added [" + StatementEvent.class + "] as TEvent");
		cepConfig.addEventType("TEvent", StatementEvent.class.getName());

	}

	@Override
	protected void handleEvent(RSPTripleSet e) {
		super.handleEvent(e);
		for (TripleContainer tc : e.getEventTriples()) {
			String[] t = tc.getTriple();
			esperEventsNumber++;
			cepRT.sendEvent(new StatementEvent(createStatement(t), cepRT.getCurrentTime(), System.currentTimeMillis()));
		}

	}

	private Statement createStatement(String[] eventTriple) {
		Resource subject = ResourceFactory.createResource(eventTriple[0]);
		Property predicate = (eventTriple[1] != RDFSUtils.TYPE_PROPERTY) ? ResourceFactory.createProperty(eventTriple[1]) : RDF.type;
		RDFNode object = ResourceFactory.createResource(eventTriple[2]);
		return ResourceFactory.createStatement(subject, predicate, object);
	}

}