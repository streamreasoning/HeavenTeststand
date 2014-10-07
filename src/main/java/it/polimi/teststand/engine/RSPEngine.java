package it.polimi.teststand.engine;

import it.polimi.events.Experiment;
import it.polimi.events.StreamingEvent;
import it.polimi.output.result.ResultCollector;
import it.polimi.streamer.EventProcessor;
import it.polimi.teststand.events.TestExperimentResultEvent;
import it.polimi.teststand.events.TestResultEvent;

public abstract class RSPEngine extends EventProcessor<StreamingEvent> {

	protected ResultCollector<TestResultEvent, TestExperimentResultEvent> resultCollector;
	protected TestExperimentResultEvent er;
	protected Experiment experiment;
	protected String name;
	protected static int time = 0;

	public RSPEngine(
			ResultCollector<TestResultEvent, TestExperimentResultEvent> storeSystem) {
		this.setResultCollector(storeSystem);
	}

	public String getName() {
		return name;
	}

	public abstract boolean startProcessing(Experiment e);

	public abstract Experiment stopProcessing();

	public abstract void turnOn();

	public abstract void turnOff();

	public ResultCollector<TestResultEvent, TestExperimentResultEvent> getResultCollector() {
		return resultCollector;
	}

	public void setResultCollector(
			ResultCollector<TestResultEvent, TestExperimentResultEvent> resultCollector) {
		this.resultCollector = resultCollector;
	}

}
