package it.polimi.processing.main;

import it.polimi.processing.collector.StartableCollector;
import it.polimi.processing.collector.impl.CollectorEventResult;
import it.polimi.processing.collector.impl.CollectorExperimentResult;
import it.polimi.processing.collector.saver.CSVEventSaver;
import it.polimi.processing.collector.saver.SQLLiteEventSaver;
import it.polimi.processing.collector.saver.TrigEventSaver;
import it.polimi.processing.core.TestStand;
import it.polimi.processing.events.StreamingEvent;
import it.polimi.processing.events.result.ExperimentResultEvent;
import it.polimi.processing.events.result.StreamingEventResult;
import it.polimi.processing.rspengine.RSPEngine;
import it.polimi.processing.rspengine.jena.JenaEngineReducedRules;
import it.polimi.processing.streamer.Streamer;

import java.sql.SQLException;

public class JenaReducedReasoner {

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {
		String[] files = new String[] { "University0_0_clean.nt" };

		TestStand<RSPEngine> testStand = new TestStand<RSPEngine>();

		StartableCollector<StreamingEventResult> streamingEventResultCollector = new CollectorEventResult(
				testStand, new TrigEventSaver(), new CSVEventSaver());
		StartableCollector<ExperimentResultEvent> experimentResultCollector = new CollectorExperimentResult(
				testStand, new SQLLiteEventSaver());
		RSPEngine engine = new JenaEngineReducedRules("jenared", testStand);
		Streamer<StreamingEvent> streamer = new Streamer<StreamingEvent>(
				testStand);

		testStand.build(streamingEventResultCollector,
				experimentResultCollector, engine, streamer);

		testStand.init();
		try {
			for (String f : files) {

				testStand.run(f);
			}
		} catch (Exception e) {
			e.printStackTrace();
			testStand.stop();
		}

		testStand.close();
	}
}