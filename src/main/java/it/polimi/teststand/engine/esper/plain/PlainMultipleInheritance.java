package it.polimi.teststand.engine.esper.plain;

import it.polimi.events.Experiment;
import it.polimi.events.StreamingEvent;
import it.polimi.output.filesystem.FileManagerImpl;
import it.polimi.output.result.ResultCollector;
import it.polimi.teststand.engine.esper.RSPEsperEngine;
import it.polimi.teststand.engine.esper.commons.listener.ResultCollectorListener;
import it.polimi.teststand.engine.esper.plain.events.Out;
import it.polimi.teststand.engine.esper.plain.events.TEvent;
import it.polimi.teststand.enums.ExecutionStates;
import it.polimi.teststand.events.TestExperimentResultEvent;
import it.polimi.teststand.events.TestResultEvent;

import java.util.Set;

import org.apache.log4j.Logger;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.ConfigurationMethodRef;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.core.service.EPServiceProviderSPI;

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

public class PlainMultipleInheritance extends RSPEsperEngine {

	// TODO useless private static final Ontology ontology = new Ontology();

	public Integer iterationNumber = 0;
	private ResultCollectorListener listener;

	public PlainMultipleInheritance(
			ResultCollector<TestResultEvent, TestExperimentResultEvent> storeSystem) {
		super(storeSystem);
		this.name = "plain";
	}

	public PlainMultipleInheritance() {
		super(null);
		this.name = "plain";
	}

	protected void initQueries() {

		Logger.getRootLogger().debug(Queries.input);
		Logger.getRootLogger().debug(Queries.rdfs3);
		Logger.getRootLogger().debug(Queries.rdfs9);

		cepAdm.createEPL(Queries.input);
		cepAdm.createEPL(Queries.rdfs3);
		cepAdm.createEPL(Queries.rdfs9);

		EPStatement out = cepAdm
				.createEPL("insert into Out select * from QueryOut.win:time_batch(1000 msec)");
		out.addListener(listener = new ResultCollectorListener(resultCollector,
				experiment));
	}

	private static void initEsper() {

		ref = new ConfigurationMethodRef();
		cepConfig = new Configuration();
		cepConfig.addMethodRef(Ontology.class, ref);

		cepConfig.addEventType("TEvent", TEvent.class.getName());
		cepConfig.addEventType("Out", Out.class.getName());

		cepConfig.getEngineDefaults().getThreading()
				.setInternalTimerEnabled(false);

		cep = (EPServiceProviderSPI) EPServiceProviderManager.getProvider(
				PlainMultipleInheritance.class.getName(), cepConfig);
		// We register an EPL statement
		cepAdm = cep.getEPAdministrator();
		cepRT = cep.getEPRuntime();
	}

	@Override
	public ExecutionStates init() {
		initEsper();
		return status = ExecutionStates.READY;
	}

	@Override
	public ExecutionStates startProcessing(Experiment e) {
		if (e != null) {
			this.experiment = e;
			cepRT.sendEvent(new CurrentTimeEvent(time));
			initQueries();
			er = new TestExperimentResultEvent(e.getInputFileName(),
					e.getOutputFileName(), FileManagerImpl.LOG_PATH
							+ e.getTimestamp(), e.getName());

			return status = ExecutionStates.READY;
		} else
			return status = ExecutionStates.ERROR;
	}

	@Override
	public boolean sendEvent(StreamingEvent e) {
		listener.setLineNumber(e.getLineNumber());
		TEvent esperEvent;
		Set<String[]> eventTriples = e.getEventTriples();
		for (String[] eventTriple : eventTriples) {
			Logger.getRootLogger().info("Create New Esper Event");
			esperEvent = new TEvent(new String[] { eventTriple[0] },
					eventTriple[1], new String[] { eventTriple[2] }, "Input",
					cepRT.getCurrentTime());
			cepRT.sendEvent(esperEvent);
		}
		sendTimeEvent();
		return true;
	}

	@Override
	public ExecutionStates stopProcessing(Experiment e) {
		if (e != null) {
			er.setTimestamp_end(System.currentTimeMillis());
			resultCollector.storeExperimentResult(er);
			return status = ExecutionStates.STOP;
		} else
			return status = ExecutionStates.ERROR;
	}

	@Override
	public ExecutionStates close() {
		Logger.getRootLogger().info("Nothing to do...Turing Off");
		return status = ExecutionStates.CLOSED;
	}
}