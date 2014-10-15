package it.polimi.teststand.main;

import it.polimi.collector.StartableCollector;
import it.polimi.collector.impl.CollectorEventResult;
import it.polimi.collector.impl.CollectorExperimentResult;
import it.polimi.collector.saver.CSVEventSaver;
import it.polimi.collector.saver.SQLLiteEventSaver;
import it.polimi.collector.saver.TrigEventSaver;
import it.polimi.events.result.ExperimentResultEvent;
import it.polimi.events.result.StreamingEventResult;
import it.polimi.rspengine.RSPEngine;
import it.polimi.rspengine.esper.plain.PlainMultipleInheritance;
import it.polimi.streamer.Streamer;
import it.polimi.teststand.core.TestStand;

import java.sql.SQLException;

public class TSModelPlain {

	public static final String INPUT_FILE_PATH = "src/main/resource/data/input/";
	public static final String OUTPUT_FILE_PATH = "src/main/resource/data/output/";

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, InterruptedException {

		String[] files = new String[] { "file1.txt" };

		TestStand<RSPEngine> testStand = new TestStand<RSPEngine>();

		StartableCollector<StreamingEventResult> streamingEventResultCollector = new CollectorEventResult(
				testStand, new TrigEventSaver(), new CSVEventSaver());
		StartableCollector<ExperimentResultEvent> experimentResultCollector = new CollectorExperimentResult(
				testStand, new SQLLiteEventSaver());
		RSPEngine engine = new PlainMultipleInheritance(testStand);
		Streamer streamer = new Streamer(testStand);

		testStand.build(streamingEventResultCollector,
				experimentResultCollector, engine, streamer);

		testStand.init();
		try {
			for (String f : files) {

				testStand.run(
						"EXPERIMENT_ON_" + f + "_WITH_ENGINE_"
								+ engine.getName(),
						INPUT_FILE_PATH + f,
						engine.getName() + "/_Result_"
								+ f.substring(0, f.length() - 3));
			}
		} catch (Exception e) {
			e.printStackTrace();
			testStand.stop();
		}

		testStand.close();

	}
}
