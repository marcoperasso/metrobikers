package com.ecommuters;

import java.util.LinkedList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;

class TEventHandler<TSender, TArgs> {
	private List<TEvent<TSender, TArgs>> handler_list = new LinkedList<TEvent<TSender, TArgs>>();

	public Boolean addHandler(TEvent<TSender, TArgs> handler) {
		synchronized (handler_list) {
			return handler_list.add(handler);
		}

	}
	public Boolean removeHandler(TEvent<TSender, TArgs> handler) {
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
			for (TEvent<TSender, TArgs> e : handler_list)
				e.fire(sender, args);
		}
	}
}

abstract class TEvent<TSender, TArgs> {
	private Handler handler;
	public TEvent() {
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

abstract class GenericEvent extends TEvent<Object, EventArgs> {
}

class EventArgs {
	public static EventArgs Empty = new EventArgs();
}

class EventHandler extends TEventHandler<Object, EventArgs> {

}