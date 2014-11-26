package edu.buffalo.memlib.util;

import java.util.*;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import edu.buffalo.memlib.swap.*;

public class SwappableDataStructure implements InvocationHandler{
	private SwapReference swapReference;

	/** initiate with null */
	public SwappableDataStructure(){
		swapReference = new SwapReference();
	}

	/**
 		Create proxy object which proxies Object o which implements Class c
	*/
	public Object getSwappableObject(Class c, Object o){
                Object proxy =  Proxy.newProxyInstance(
                                SwappableDataStructure.class.getClassLoader(),
                                new Class[] { c },
                                new SwappableDataStructure(o));
                return proxy;
        }

	/** 
		Any function call will pass through here and 
		swapReference will swap in if the object is swapped out 
	*/	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//System.out.println("Method "+ method.getName()+" is called");
		swapReference.get();
		if(T != null)
			return method.invoke(this.T, args);
		return null;
	}


	public static void main(String[] args){
		SwappableDataStructure sds = new SwappableDataStructure();
		
		Set set = (Set)sds.getData(Set.class, new HashSet());
		if(set == null)throw new NullPointerException();
		set.add(5);
		set.add(100);
		System.out.println(set);
                
		List l = (List)sds.getData(List.class, new LinkedList());
		
		Map<Integer,String> m = (Map)sds.getData(Map.class, new HashMap<Integer,String>());

	}
}
