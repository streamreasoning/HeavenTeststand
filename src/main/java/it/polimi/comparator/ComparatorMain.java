package it.polimi.comparator;

import it.polimi.comparator.core.CalibrationStand;
import it.polimi.comparator.engine.ComparatorImpl;
import it.polimi.comparator.engine.EngineComparator;
import it.polimi.comparator.events.ComparisonExperimentResult;
import it.polimi.comparator.events.ComparisonResultEvent;
import it.polimi.comparator.output.ResultCollectorComparator;
import it.polimi.output.filesystem.FileManagerImpl;
import it.polimi.output.result.ResultCollector;
import it.polimi.output.sqllite.DatabaseManagerImpl;
import it.polimi.streamer.Streamer;

public class ComparatorMain {

	public static void main(String[] args) throws Exception {

		String[] comparing_files = new String[] { "jena/_Result_file1.trig",
				"plain/_Result_file1.trig" };
		String[] files = new String[] { "file1.txt" };
		ResultCollector<ComparisonResultEvent, ComparisonExperimentResult> resultCollector = new ResultCollectorComparator(
				new FileManagerImpl(), new DatabaseManagerImpl());

		ComparatorImpl engine = new ComparatorImpl(comparing_files, resultCollector);
		
		
		CalibrationStand<EngineComparator> stand = new CalibrationStand<EngineComparator>(
				comparing_files, files, engine, new Streamer<EngineComparator>(engine));

		stand.turnOn();
		stand.run();
		stand.turnOff();

	}
}
