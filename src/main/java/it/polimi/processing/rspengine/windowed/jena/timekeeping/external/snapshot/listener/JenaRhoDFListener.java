package it.polimi.processing.rspengine.windowed.jena.timekeeping.external.snapshot.listener;

import it.polimi.processing.ets.core.EventProcessor;
import it.polimi.processing.events.interfaces.Event;
import it.polimi.processing.rspengine.windowed.jena.timekeeping.external.snapshot.listener.abstracts.JenaNaiveListener;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

public final class JenaRhoDFListener extends JenaNaiveListener {

	private final String aBoxRuleset;

	public JenaRhoDFListener(Model tbox, String aBoxRuleset, EventProcessor<Event> collector) {
		super(tbox, collector);
		this.aBoxRuleset = aBoxRuleset;
	}

	@Override
	protected Reasoner getReasoner() {
		return new GenericRuleReasoner(Rule.rulesFromURL(aBoxRuleset));
	}
}