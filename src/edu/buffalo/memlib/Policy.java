package edu.buffalo.memlib;

public class Policy {
	/** Heap utilization that will trigger swap (in foreground). */
	public double fgHeapMaxUsage = 0.9;
	/** Desired heap utilization (in foreground). */
	public double fgHeapOptUsage = 0.8;
	/** Heap utilization that will trigger swap (in background). */
	public double bgHeapMaxUsage = 0.8;
	/** Desired heap utilization (in background). */
	public double bgHeapOptUsage = 0.7;
	/** Time interval (in ms) between swap manager checks. */
	public long heapAnalysisInterval = 1000;
	/** Location of swap directory (can be null). */
	public String swapPath;
}
