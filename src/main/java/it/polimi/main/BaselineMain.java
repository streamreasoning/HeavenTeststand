package it.polimi.main;

import it.polimi.main.strategies.AggregationStrategy;
import it.polimi.main.strategies.OneToOneStrategy;
import it.polimi.processing.collector.StartableCollector;
import it.polimi.processing.collector.saver.CSVEventSaver;
import it.polimi.processing.collector.saver.SQLLiteEventSaver;
import it.polimi.processing.collector.saver.TrigEventSaver;
import it.polimi.processing.collector.saver.VoidSaver;
import it.polimi.processing.enums.BuildingStrategy;
import it.polimi.processing.events.RSPEvent;
import it.polimi.processing.events.factory.ConstantEventBuilder;
import it.polimi.processing.events.factory.StepEventBuilder;
import it.polimi.processing.events.factory.abstracts.EventBuilder;
import it.polimi.processing.events.interfaces.EventResult;
import it.polimi.processing.events.interfaces.ExperimentResult;
import it.polimi.processing.rspengine.windowed.RSPEngine;
import it.polimi.processing.rspengine.windowed.jena.JenaEngineGraph;
import it.polimi.processing.rspengine.windowed.jena.JenaEngineStmt;
import it.polimi.processing.rspengine.windowed.jena.JenaEngineTEvent;
import it.polimi.processing.rspengine.windowed.jena.JenaEngineTriple;
import it.polimi.processing.rspengine.windowed.jena.listener.JenaFullListener;
import it.polimi.processing.rspengine.windowed.jena.listener.JenaRhoDFListener;
import it.polimi.processing.rspengine.windowed.jena.listener.JenaSMPLListener;
import it.polimi.processing.streamer.RSPEventStreamer;
import it.polimi.processing.workbench.collector.CollectorEventResult;
import it.polimi.processing.workbench.collector.CollectorExperimentResult;
import it.polimi.processing.workbench.core.RSPTestStand;
import it.polimi.processing.workbench.core.TimeStrategy;
import it.polimi.processing.workbench.streamer.NTStreamer;
import it.polimi.utils.BaseLineInputOrder;
import it.polimi.utils.ExecutionEnvirorment;
import it.polimi.utils.FileUtils;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import com.espertech.esper.client.UpdateListener;

@Log4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BaselineMain {

	// EVENT TYPES
	private static final int JENAPLAIN = 0, JENATRIPLE = 1, JENASTMT = 2, JENAGRAPH = 3;
	// REASONER
	private static final int JENAFULL = 2, JENARHODF = 1, JENASMPL = 0;

	private static int CURRENT_ENGINE;
	private static int EXPERIMENT_NUMBER;

	private static String JENANAME = "jena";
	private static String SMPL = "smpl";
	private static String RHODF = "rhodf";
	private static String FULL = "full";

	private static String[] engineNames = new String[] { JENANAME + SMPL, JENANAME + RHODF, JENANAME + FULL };

	public static final String ONTOLOGYCLASS = "Ontology";

	private static RSPEngine engine;

	private static Date exeperimentDate;
	private static String file, COMMENT;

	private static RSPTestStand testStand;
	private static StartableCollector<EventResult> streamingEventResultCollector;
	private static StartableCollector<ExperimentResult> experimentResultCollector;
	private static UpdateListener listener;

	private static final DateFormat DT = new SimpleDateFormat("yyyy_MM_dd");

	private static int EXPERIMENT_TYPE;
	private static final int RESULT = 0;
	private static final int LATENCY = 1;
	private static final int MEMORY = 2;
	private static final boolean AGGR = false;

	private static int EXECUTION;

	private static CSVEventSaver csv = new CSVEventSaver();
	private static String whereOutput, whereWindow, outputFileName, windowFileName, experimentDescription;
	private static RSPEventStreamer streamer;
	private static int CURRENT_REASONER;
	private static BuildingStrategy EVENT_BUILDER;
	private static String engineName;
	private static String eventBuilderCodeName;
	private static int X;
	private static int Y;
	private static int INITSIZE;
	private static int EVENTS = 1000;

	public static void main(String[] args) throws ClassNotFoundException, SQLException, ParseException {
		int aggreation = 10;
		file = args[BaseLineInputOrder.FILE];// _CLND_UNIV10INDEX0SEED0.nt
		EVENTS = Integer.parseInt(args[BaseLineInputOrder.EVENTS]);
		EXPERIMENT_TYPE = Integer.parseInt(args[BaseLineInputOrder.EXPERIMENT_TYPE]);
		EXPERIMENT_NUMBER = Integer.parseInt(args[BaseLineInputOrder.EXPERIMENTNUMBER]);
		EXECUTION = Integer.parseInt(args[BaseLineInputOrder.EXECUTIONNUMBER]);
		CURRENT_ENGINE = Integer.parseInt(args[BaseLineInputOrder.JENA_CURRENTENGINE]);
		CURRENT_REASONER = Integer.parseInt(args[BaseLineInputOrder.CURRENTREASONER]);
		COMMENT = "n".equalsIgnoreCase(args[BaseLineInputOrder.COMMENTS].toUpperCase()) ? "" : args[BaseLineInputOrder.COMMENTS];
		EVENT_BUILDER = BuildingStrategy.getById(Integer.parseInt(args[BaseLineInputOrder.EVENTBUILDER]));
		INITSIZE = Integer.parseInt(args[BaseLineInputOrder.INITSIZE]);
		if (args.length > BaseLineInputOrder.X)
			X = Integer.parseInt(args[BaseLineInputOrder.X]);
		if (args.length > BaseLineInputOrder.Y)
			Y = Integer.parseInt(args[BaseLineInputOrder.Y]);
		exeperimentDate = FileUtils.d;

		log.info("Experiment [" + EXPERIMENT_NUMBER + "] on [" + file + "] of [" + exeperimentDate + "] Number of Events [" + EVENTS + "]");

		TimeStrategy strategy = (AGGR) ? new OneToOneStrategy() : new AggregationStrategy(aggreation);
		testStand = new RSPTestStand(strategy);

		eventBuilderCodeName = streamerSelection();

		engineName = engineNames[CURRENT_REASONER];

		experimentDescription = "EXPERIMENT_ON_" + file + "_WITH_ENGINE_" + engineName + "EVENT_" + CURRENT_ENGINE;

		FileUtils.createOutputFolder("exp" + EXPERIMENT_NUMBER + "/" + engineName);

		String generalName = "EN" + EXPERIMENT_NUMBER + "_" + "EXE" + EXECUTION + "_" + COMMENT + "_" + DT.format(exeperimentDate) + "_"
				+ file.split("\\.")[0] + "_R" + CURRENT_REASONER + "E" + CURRENT_ENGINE + eventBuilderCodeName;

		outputFileName = EXPERIMENT_TYPE + "Result_" + generalName;
		windowFileName = EXPERIMENT_TYPE + "Window_" + generalName;

		whereOutput = "exp" + EXPERIMENT_NUMBER + "/" + engineName + "/" + outputFileName;
		whereWindow = "exp" + EXPERIMENT_NUMBER + "/" + engineName + "/" + windowFileName;

		log.info("Output file name will be: [" + whereOutput + "]");
		log.info("Window file name will be: [" + whereWindow + "]");

		collectorSelection();

		reasonerSelection();

		engineSelection();

		run(file, COMMENT, EXPERIMENT_NUMBER, exeperimentDate, experimentDescription);

	}

	protected static String streamerSelection() {
		EventBuilder<RSPEvent> eb;

		String code = "_EB";
		switch (EVENT_BUILDER) {
			case CONSTANT:
				log.info("Event Builder Selection: Constant [" + INITSIZE + "] ");
				code += "K" + INITSIZE;
				log.info("CONSTANT Event Builder Initial Size [" + INITSIZE + "]");
				eb = new ConstantEventBuilder(INITSIZE, EXPERIMENT_NUMBER);
				break;
			case STEP:
				log.info("Event Builder Selection: Step [" + INITSIZE + "] Heigh [" + X + "] Width [" + Y + "] ");
				eb = new StepEventBuilder(X, Y, INITSIZE, EXPERIMENT_NUMBER);
				code += "S" + INITSIZE + "H" + X + "W" + Y;
				break;
			default:
				eb = null;

		}

		streamer = new NTStreamer(testStand, eb, EVENTS);
		return code;
	}

	protected static void engineSelection() {
		switch (CURRENT_ENGINE) {
			case JENAPLAIN:
				log.info("Engine Selection: JenaPlain [" + engineName + "] ");
				engine = new JenaEngineTEvent(engineName, testStand, listener);
				break;
			case JENATRIPLE:
				log.info("Engine Selection: JenaTriple [" + engineName + "] ");
				engine = new JenaEngineTriple(engineName, testStand, listener);
				break;
			case JENASTMT:
				log.info("Engine Selection: Jena Stmt [" + engineName + "] ");
				engine = new JenaEngineStmt(engineName, testStand, listener);
				break;
			case JENAGRAPH:
				log.info("Engine Selection: JenaGraph [" + engineName + "] ");
				engine = new JenaEngineGraph(engineName, testStand, listener);
				break;
			default:
				engine = null;

		}
	}

	protected static void reasonerSelection() {
		switch (CURRENT_REASONER) {
			case JENARHODF:
				log.info("Reasoner Selection: RHODF");
				listener = new JenaRhoDFListener(FileUtils.UNIV_BENCH_RHODF_MODIFIED, FileUtils.RHODF_RULE_SET_RUNTIME, testStand);
				break;
			case JENASMPL:
				log.info("Reasoner Selection: SMPL");
				listener = new JenaSMPLListener(FileUtils.UNIV_BENCH_RDFS_MODIFIED, testStand);
				break;
			case JENAFULL:
				log.info("Reasoner Selection: FULL");
				listener = new JenaFullListener(FileUtils.UNIV_BENCH_RHODF_MODIFIED, testStand);
				break;
			default:
				listener = null;
		}
	}

	protected static void collectorSelection() throws SQLException, ClassNotFoundException {
		experimentResultCollector = new CollectorExperimentResult(testStand, new SQLLiteEventSaver());

		TrigEventSaver trig = new TrigEventSaver();
		VoidSaver voids = new VoidSaver();
		switch (EXPERIMENT_TYPE) {
			case MEMORY:
				log.info("Execution of Memory Experiment");
				ExecutionEnvirorment.memoryEnabled = true;
				streamingEventResultCollector = new CollectorEventResult(testStand, voids, csv, engineName + "/");
				break;
			case LATENCY:
				log.info("Execution of Latency Experiment");
				streamingEventResultCollector = new CollectorEventResult(testStand, voids, csv, engineName + "/");
				break;
			case RESULT:
				log.info("Execution of Result C&S Experiment");
				streamingEventResultCollector = new CollectorEventResult(testStand, trig, csv, engineName + "/");
				break;
			default:
				streamingEventResultCollector = null;
		}
	}

	private static void run(String f, String comment, int experimentNumber, Date d, String experimentDescription) {

		testStand.build(streamingEventResultCollector, experimentResultCollector, engine, streamer);

		testStand.init();
		try {
			experimentNumber += testStand.run(f, experimentNumber, comment, outputFileName, windowFileName, experimentDescription);
		} catch (Exception e) {
			log.error(e.getMessage());
			testStand.stop();
		}

		testStand.close();
	}

}
