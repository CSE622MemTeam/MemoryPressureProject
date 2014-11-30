package edu.buffalo.memlib;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SwapObjects<T> implements InvocationHandler {
    private SwapReference<T> swapReference;

    /** initialization without object. Don't know any useful case for this
     * for now. Requires extra function(s) to make this useful 
     */
    public SwapObjects() {
        swapReference = new SwapReference<T>();
    }

    /** initialization with object. Will initialize SwapReference */
    public SwapObjects(T object) {
        swapReference = new SwapReference<T>(object);
    }

    /** Create proxy object which proxies Object o which implements Class c */
    @SuppressWarnings("unchecked")
    public static <T> T getOne(Class<?> c, T o) {
        return (T) Proxy.newProxyInstance(SwapObjects.class.getClassLoader(),
                                          new Class[] {c},
                                          new SwapObjects<T>(o));
    }

    // Creating an ArrayList proxy
    public static <T> List<T> getArrayList(){
        return (List<T>) getOne(List.class, new ArrayList<T>());
    }    

    // Creating LinkedList proxy
    public static <T> List<T> getLinkedList(){
        return (List<T>) getOne(List.class, new LinkedList<T>());
    }

    // Creating HashMap proxy
    public static <K,V> Map<K,V> getHashMap(){
        return (Map<K,V>) getOne(Map.class, new HashMap<K,V>());
    }

    // Creating Set proxy
    public static <T> Set<T> getHashSet(){
        return (Set<T>) getOne(Set.class, new HashSet<T>());
    }

    /** 
     * Any function call will pass through here and swapReference will swap in if the 
     * object is swapped out 
     */    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object object = swapReference.get();
        if(object != null)
            return method.invoke(object, args);
        return null;
    }
}
