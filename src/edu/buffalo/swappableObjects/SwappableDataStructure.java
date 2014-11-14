package edu.buffalo.cse.swappableObjects;

import edu.buffalo.cse.memlib.SwapReference;
import java.util.*;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
 
public class SwappableDataStructure implements InvocationHandler{
	private SwapReference swapReference;
	private Object T;
		
	/** initiate with null */
	SwappableDataStructure(){
		swapReference = new SwapReference();
	}
	
	/** Initiate  with an object*/
	SwappableDataStructure(Object O){
		T = O;
                swapReference = new SwapReference(T);
        }

	//Creating an ArrayList proxy
	<T> List<T> getArrayList(){
		List proxy = (List) Proxy.newProxyInstance(
                        SwappableDataStructure.class.getClassLoader(),
                        new Class[] { List.class },
                        new SwappableDataStructure(new ArrayList()));
		return proxy;
	}	

	//Creating LinkedList proxy
	<T> List<T> getLinkedList(){
                List proxy = (List) Proxy.newProxyInstance(
                        SwappableDataStructure.class.getClassLoader(),
                        new Class[] { List.class },
                        new SwappableDataStructure(new LinkedList()));
                return proxy;
        }
	
	//Creating Hashpmap proxy
        <K,V> Map<K,V> getHashMap(){
                Map proxy = (Map) Proxy.newProxyInstance(
                        SwappableDataStructure.class.getClassLoader(),
                        new Class[] { Map.class },
                        new SwappableDataStructure(new HashMap()));
                return proxy;
        }
	
	//Creating Set proxy
	<T> Set<T> getHashSet(){
                Set proxy = (Set) Proxy.newProxyInstance(
                        SwappableDataStructure.class.getClassLoader(),
                        new Class[] { Set.class },
                        new SwappableDataStructure(new HashSet()));
                return proxy;
        }

	/** 
  	    Any function call will pass through here and 
 	    swapReference will swap in if the object is swapped out 
	*/	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//System.out.println("Method "+ method.getName()+" is called");
		swapReference.get();
		if(T != null)
                	return method.invoke(this.T, args);
		return null;
        }


	public static void main(String[] args){
		SwappableDataStructure sds = new SwappableDataStructure();
		List list= sds.getArrayList();

		//Test arrayList
		if(list == null)throw new NullPointerException();			
		list.add(5);
		list.add(6);
		list.get(0);
		for (Object i : list)
			System.out.print((int)i+" ");
		//Test LinkedList
		List list2= sds.getLinkedList();
                if(list2 == null)throw new NullPointerException();                        
                list2.add(5);
                list2.add(6);
                list2.get(0);
                for (Object i : list2)
                        System.out.print((int)i+" ");
		System.out.println();
	
		//Test HashMap
		Map map = sds.getHashMap();
                if(map == null)throw new NullPointerException();
                map.put(5, 10);
                map.put(6,11);
                map.get(5);
                System.out.println((int)map.get(5)+" ");
	
		//Test Set 	
		Set set = sds.getHashSet();
                if(set == null)throw new NullPointerException();
                set.add(5);
                set.add(100);
		System.out.println(set);
	}
}
