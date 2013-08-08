package com.ecommuters;

import java.util.LinkedList;
import java.util.List;

class TEventHandler<TSender, TArgs> {
	private List<TEvent<TSender, TArgs>> handler_list = new LinkedList<TEvent<TSender, TArgs>>();

	public Boolean addHandler(TEvent<TSender, TArgs> handler) {
		return handler_list.add(handler);
	}
	public Boolean removeHandler(TEvent<TSender, TArgs> handler) {
		return handler_list.remove(handler);
	}
	public void fire(TSender sender, TArgs args) {
		for (TEvent<TSender, TArgs> e : handler_list)
			e.fire(sender, args);
	}
}

class EventArgs {
	public static EventArgs Empty = new EventArgs();
}

class EventHandler extends TEventHandler<Object, EventArgs> {


}