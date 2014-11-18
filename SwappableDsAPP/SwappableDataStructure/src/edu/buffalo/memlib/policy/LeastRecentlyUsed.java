package edu.buffalo.memlib.policy;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

import android.util.Log;

import edu.buffalo.memlib.manager.MemoryUtil;
import edu.buffalo.memlib.swap.*;
import edu.buffalo.memlib.util.*;

public class LeastRecentlyUsed extends Policy {

	public LeastRecentlyUsed() {
		super(new LinkedHashSet<Swappable>());
	}

	public Swappable pop() {
		Iterator<Swappable> iterator = ((LinkedHashSet<Swappable>) collection).iterator();
		try {
			Swappable next = iterator.next();
			iterator.remove();
			
			return next;
		}
		catch (NoSuchElementException e) {
			Log.d("DroidMemTool", "No more data-structures to swap out");
			return null;
		}
	}

	public void push(Swappable swappable) {
		if (((LinkedHashSet<Swappable>) collection).contains(swappable))
			((LinkedHashSet<Swappable>) collection).remove(swappable);
		((LinkedHashSet<Swappable>) collection).add(swappable);
	}

	public boolean trigger() {
		Log.d("DroidMemTool", "Swapping Triggered");
		
		if(MemoryUtil.memory_state == MemoryUtil.MEMORY_CRITICAL)
		{
			Swappable next = pop();
			
			while(null != next)
			{
				next.swapOut(false);
			}
		}
		else if(MemoryUtil.memory_state == MemoryUtil.MEMORY_LOW)
		{
			/**Stage 2 actions*/
		}
		return false;
	}
}
