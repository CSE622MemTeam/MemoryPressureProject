package edu.buffalo.memlib.policy;

import java.util.Collection;

import edu.buffalo.memlib.swap.*;

public abstract class Policy {
	protected Collection<Swappable> collection;

	protected Policy(Collection<Swappable> collection) {
		this.collection = collection;
	}

	abstract Swappable pop();

	abstract void push(Swappable swappable);

	abstract boolean trigger();
}
