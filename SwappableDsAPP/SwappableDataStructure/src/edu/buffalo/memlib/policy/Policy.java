package edu.buffalo.memlib.policy;

import java.util.Collection;

import edu.buffalo.memlib.swap.*;

public abstract class Policy {
	protected Collection<Swappable> collection;

	protected Policy(Collection<Swappable> collection) {
		this.collection = collection;
	}

	public abstract Swappable pop();

	public abstract void push(Swappable swappable);

	public abstract boolean trigger();
}
