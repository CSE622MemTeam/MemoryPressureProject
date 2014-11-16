package edu.buffalo.memlib.util;

import java.io.Serializable;
import java.util.AbstractList;
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
	private SwapReference<Vector<E>> ref;
	
	public SwapVector() {
		ref = new SwapReference<Vector<E>>(new Vector<E>());
	}
	
    @Override
    public E get(int location) {
    	
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int size() {
	// TODO Auto-generated method stub
	return 0;
    }
    
    public boolean add(E e) {
	
	return true;
    }

    @Override
    public boolean swap() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean unswap() {
	// TODO Auto-generated method stub
	return false;
    }
}
