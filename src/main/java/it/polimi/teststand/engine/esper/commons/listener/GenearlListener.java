package it.polimi.teststand.engine.esper.commons.listener;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public abstract class GenearlListener implements UpdateListener {


	@Override
	public abstract void update(EventBean[] newEvents, EventBean[] oldEvents);

}