package it.polimi.processing.collector;

import it.polimi.processing.events.interfaces.Event;

import java.io.IOException;

public interface ResultCollector<T extends Event> {

	public boolean process(T r) throws IOException;

	public boolean process(T r, String where) throws IOException;

}
