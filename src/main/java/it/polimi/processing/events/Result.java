package it.polimi.processing.events;

import it.polimi.processing.collector.saver.data.CSV;
import it.polimi.processing.collector.saver.data.CollectableData;
import it.polimi.processing.collector.saver.data.TriG;
import it.polimi.processing.events.interfaces.EventResult;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result implements EventResult {

	private Set<String[]> statements;
	private long from, to;
	private long timestamp;
	private Boolean completeSMPL, soundSMPL, completeRHODF, soundRHODF;;

	@Override
	public CollectableData getTrig() {
		return new TriG("<http://example.org/" + from + "/" + to + ">", statements);
	}

	@Override
	public CollectableData getCSV() {
		String s = "<http://example.org/" + from + "/" + to + ">";
		return new CSV(s);
	}

	public Result(Set<String[]> statements, long from, long to, long timestamp) {
		this.statements = statements;
		this.from = from;
		this.to = to;
		this.timestamp = timestamp;
	}

}