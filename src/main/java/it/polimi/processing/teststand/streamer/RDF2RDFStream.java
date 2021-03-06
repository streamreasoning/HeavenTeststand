package it.polimi.processing.teststand.streamer;

import it.polimi.processing.EventProcessor;
import it.polimi.processing.enums.ExecutionState;
import it.polimi.processing.events.CTEvent;
import it.polimi.processing.events.Experiment;
import it.polimi.processing.events.TripleContainer;
import it.polimi.processing.events.profiler.abstracts.FlowRateProfiler;
import it.polimi.processing.exceptions.WrongStatusTransitionException;
import it.polimi.processing.streamer.Parser;
import it.polimi.services.FileService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Getter
/**
 * Specific implementation of the TSStreamer, responsible to build an RDF Stream starting from a file containing RDF Triples in nt format
 * The Modules must be initialized trough the method init() and  finalized trough the method close()
 * The last event is maintained and can be accessed to a getter method
 * The current experiment can be available until finalization.
 */
@Log4j
public final class RDF2RDFStream extends TSStreamer {

	private CTEvent lastEvent;
	private String line;
	private int streamedEvents;
	private int triples;
	private Experiment currentExperiment;
	protected FlowRateProfiler<CTEvent> profiler;

	public RDF2RDFStream(EventProcessor<CTEvent> processor, FlowRateProfiler<CTEvent> profiler, int eventLimit) {
		super(processor, ExecutionState.CLOSED, eventLimit);
		this.profiler = profiler;
	}

	public RDF2RDFStream(EventProcessor<CTEvent> processor, FlowRateProfiler<CTEvent> profiler) {
		super(processor, ExecutionState.CLOSED, 1000);
		this.profiler = profiler;
	}

	/**
	 * 
	 * This method process an Experiment
	 * 
	 **/
	@Override
	public boolean process(Experiment e) {
		if (!ExecutionState.READY.equals(status)) {
			throw new WrongStatusTransitionException("Can't Start in Status [" + status + "] must be [" + ExecutionState.READY + "]");
		} else {
			this.currentExperiment = e;
			try {
				log.info("Start Streaming");
				FileReader in = FileService.getFileReader(currentExperiment.getInputFileName());
				BufferedReader br = FileService.getBuffer(in);
				while (streamedEvents <= eventLimit - 1) {

					line = br.readLine();

					status = ExecutionState.RUNNING;
					profiler.append(new TripleContainer(Parser.parseTriple(line)));
					triples++;

					if (profiler.isReady()) {
						streamedEvents += next.process(lastEvent = profiler.getEvent()) ? 1 : 0;
						log.debug("Send Event [" + streamedEvents + "] of size [" + lastEvent.size() + "]");
						log.debug("Streamed [" + triples + "] triples");
						if (streamedEvents % 1000 == 0) {
							log.info("Process Complete [" + (double) streamedEvents * 100 / eventLimit + "%]");
						}
					} else {
						log.debug("Still Processing [" + line + "]");
					}
					status = ExecutionState.READY;
				}

				log.info("End Streaming: Triples: [" + triples + "] " + "RSPEvents: [" + streamedEvents + "]");
				br.close();
			} catch (IOException ex) {
				status = ExecutionState.ERROR;
				log.error(ex.getMessage());
			}
			return ExecutionState.READY.equals(status);
		}
	}

	@Override
	public ExecutionState init() {
		status = ExecutionState.READY;
		streamedEvents = 0;
		triples = 0;
		return status;
	}

	@Override
	public ExecutionState close() {
		status = ExecutionState.CLOSED;
		currentExperiment = null;
		lastEvent = null;
		return status;
	}

}