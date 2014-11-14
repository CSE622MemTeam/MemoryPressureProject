package edu.buffalo.memlib;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;
import java.util.Vector;

import android.content.Context;

public class SwapVector<E extends Serializable> extends AbstractList<E> 
                                                implements Swappable,
                                                           List<E>,
                                                           RandomAccess, 
                                                           Cloneable, 
                                                           java.io.Serializable {
    protected int capacityIncrement;
    protected Context context;
    protected boolean internal = true;
    protected Vector<E> vector;
    private static final long serialVersionUID = -8062241491045229885L;
    
    public SwapVector(int initialCapacity, int capacityIncrement, Context context) {
        super();
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
        
        this.capacityIncrement = capacityIncrement;
        this.context = context;
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
