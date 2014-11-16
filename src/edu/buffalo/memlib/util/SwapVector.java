package edu.buffalo.memlib.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.Vector;

import edu.buffalo.memlib.swap.SwapReference;
import edu.buffalo.memlib.swap.Swappable;

public class SwapVector<E extends Serializable> extends AbstractList<E> 
												implements Swappable,
														   List<E>,
														   RandomAccess, 
														   Cloneable, 
														   java.io.Serializable {
	private static final long serialVersionUID = 2186400141782442375L;
	private SwapReference<Vector<E>> ref;
	
	public SwapVector() {
		ref = new SwapReference<Vector<E>>(new Vector<E>());
	}
	
	public SwapVector(Collection<? extends E> c) {
		ref = new SwapReference<Vector<E>>(new Vector<E>(c));
	}
	
	public SwapVector(int initialCapacity) {
		ref = new SwapReference<Vector<E>>(new Vector<E>(initialCapacity));
	}
	
	public SwapVector(int initialCapacity, int capacityIncrement) {
		ref = new SwapReference<Vector<E>>(new Vector<E>(initialCapacity, capacityIncrement));
	}
    
    public boolean add(E e) {
    	return ref.get().add(e);
    }
	
    @Override
    public E get(int location) {
    	return ref.get().get(location);
    }

    @Override
    public int size() {
    	return ref.get().size();
    }

    @Override
    public void swapIn() {
    	ref.swapIn();
    }

    @Override
    public void swapOut(boolean internal) {
    	ref.swapOut(internal);
    }
}
