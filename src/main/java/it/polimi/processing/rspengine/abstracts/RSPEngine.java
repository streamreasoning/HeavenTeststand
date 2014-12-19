package it.polimi.processing.rspengine.abstracts;

import it.polimi.processing.enums.ExecutionState;
import it.polimi.processing.events.RSPTripleSet;
import it.polimi.processing.events.interfaces.Event;
import it.polimi.processing.workbench.core.EventProcessor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RSPEngine implements EventProcessor<RSPTripleSet> {

	protected ExecutionState status;
	protected EventProcessor<Event> next;
	protected String name;

	@Getter
	protected RSPTripleSet currentEvent = null;
	@Getter
	protected long sentTimestamp;

	public RSPEngine(String name, EventProcessor<Event> next) {
		this.next = next;
		this.name = name;
	}

	public abstract ExecutionState init();

	public abstract ExecutionState close();

	public abstract ExecutionState startProcessing();

	public abstract ExecutionState stopProcessing();

	public abstract int getEventNumber();

	/**
	 * TODO general definition of the API
	 * 
	 * @param i
	 */
	public abstract void progress(int i);

}
