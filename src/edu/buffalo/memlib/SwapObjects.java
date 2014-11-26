package edu.buffalo.memlib.util;

import java.util.*;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import edu.buffalo.memlib.swap.*;

public class SwapObjects<T> implements InvocationHandler{
	private SwapReference swapReference;

	/** initialization without object. Dont know any useful case for this
	 * for now. Requires extra function(s) to make this useful 
 	*/
	public SwapObjects(){
		swapReference = new SwapReference();
	}

	/** initialization with object. Will initialize SwapReference*/
        public SwapObjects(T object){
                swapReference = new SwapReference(object);
        }


	/**
 		Create proxy object which proxies Object o which implements Class c
	*/
	public static Object getOne(Class c, Object o){
                Object proxy =  Proxy.newProxyInstance(
                                SwapObjects.class.getClassLoader(),
                                new Class[] { c },
                                new SwapObjects(o));
		SwapObjects sds = new SwapObjects (proxy);
                return proxy;
        }

	//Creating an ArrayList proxy
	public static <T> List<T> getArrayList(){
		List proxy = (List) Proxy.newProxyInstance(
				SwapObjects.class.getClassLoader(),
				new Class[] { List.class },
				new SwapObjects(new ArrayList()));
		SwapObjects sds = new SwapObjects (proxy);
		return proxy;
	}	

	//Creating LinkedList proxy
	public static <T> List<T> getLinkedList(){
		List proxy = (List) Proxy.newProxyInstance(
				SwapObjects.class.getClassLoader(),
				new Class[] { List.class },
				new SwapObjects(new LinkedList()));
		SwapObjects sds = new SwapObjects (proxy);
		return proxy;
	}

	//Creating Hashpmap proxy
	public static <K,V> Map<K,V> getHashMap(){
		Map proxy = (Map) Proxy.newProxyInstance(
				SwapObjects.class.getClassLoader(),
				new Class[] { Map.class },
				new SwapObjects(new HashMap()));
		SwapObjects sds = new SwapObjects (proxy);
		return proxy;
	}

	//Creating Set proxy
	public static <T> Set<T> getHashSet(){
		Set proxy = (Set) Proxy.newProxyInstance(
				SwapObjects.class.getClassLoader(),
				new Class[] { Set.class },
				new SwapObjects(new HashSet()));
		SwapObjects sds = new SwapObjects (proxy);
		return proxy;
	}



	/** 
		Any function call will pass through here and 
		swapReference will swap in if the object is swapped out 
	*/	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object object = swapReference.get();
		if(object != null)
			return method.invoke(object, args);
		return null;
	}


	public static void main(String[] args){
		
		Set set = (Set)SwapObjects.getOne(Set.class, new HashSet());
		if(set == null)throw new NullPointerException();
		set.add(5);
		set.add(100);
		System.out.println(set);
                
		List l = (List)SwapObjects.getOne(List.class, new LinkedList());
		
		Map<Integer,String> m = (Map)SwapObjects.getOne(Map.class, new HashMap<Integer,String>());

	}
}
