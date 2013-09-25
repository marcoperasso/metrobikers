package com.ecommuters;

import java.util.LinkedList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;

class TEvent<TSender, TArgs> {
	private List<TEventItem<TSender, TArgs>> handler_list = new LinkedList<TEventItem<TSender, TArgs>>();

	public Boolean addHandler(TEventItem<TSender, TArgs> handler) {
		synchronized (handler_list) {
			return handler_list.add(handler);
		}

	}
	public Boolean removeHandler(TEventItem<TSender, TArgs> handler) {
		synchronized (handler_list) {
			return handler_list.remove(handler);
		}
	}
	public void clearHandlers() {
		synchronized (handler_list) {
			handler_list.clear();
		}
	}
	public void fire(TSender sender, TArgs args) {
		synchronized (handler_list) {
			for (TEventItem<TSender, TArgs> e : handler_list)
				e.fire(sender, args);
		}
	}
}

abstract class TEventItem<TSender, TArgs> {
	private Handler handler;
	public TEventItem() {
		handler = new Handler(Looper.myLooper());
	}
	public void fire(final TSender sender, final TArgs args) {
		handler.post(new Runnable() {
			public void run() {
				onEvent(sender, args);
			}
		});
	}
	public abstract void onEvent(TSender sender, TArgs args);
}

abstract class GenericEvent extends TEventItem<Object, EventArgs> {
}

class EventArgs {
	public static EventArgs Empty = new EventArgs();
}

class Event extends TEvent<Object, EventArgs> {

}