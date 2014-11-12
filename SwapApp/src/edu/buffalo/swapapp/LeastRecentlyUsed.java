package edu.buffalo.swapapp;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

public class LeastRecentlyUsed extends Policy {
    
    public LeastRecentlyUsed() {
        super(new LinkedHashSet<Swappable>());
    }

    @Override
    public Swappable pop() {
        Iterator<Swappable> iterator = ((LinkedHashSet<Swappable>) collection).iterator();
        try {
            Swappable next = iterator.next();
            iterator.remove();
            return next;
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public void push(Swappable swappable) {
        if (((LinkedHashSet<Swappable>) collection).contains(swappable))
            ((LinkedHashSet<Swappable>) collection).remove(swappable);
        ((LinkedHashSet<Swappable>) collection).add(swappable);
    }

    @Override
    public boolean trigger() {
        if ((MemUtil.getFreeMem() / MemUtil.getTotalMem()) < .1)
            return true;
        if ((MemUtil.getUsedHeap() / MemUtil.getMaxHeap()) > .9)
            return true;
        return false;
    }
}
