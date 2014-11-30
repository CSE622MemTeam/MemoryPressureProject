package edu.buffalo.memlib.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.util.Log;

public final class MemoryUtil {

    static Vector<Integer> free_pages = new Vector<Integer>();
    static Vector<Integer> oom_score  = new Vector<Integer>();
    
    @SuppressLint("UseSparseArrays")
    static HashMap<Integer, Integer> oom_map = new HashMap<Integer, Integer>();
    
    /**memory states*/
    static final int MEMORY_OK            = 1;
    static final int MEMORY_LOW      = 2;
    static final int MEMORY_CRITICAL = 3;
    
    public static int memory_state = MEMORY_OK;
    
    /**Parameters written to csv file*/
    static long vmUsage;
    static long memFree;
    static long totMem;
    static long nrFreePages;
    static int  oom_adj;
    static int  pid;
    static long cur_memory;
    static long max_memory;
    
    
    static int warningAt = 1000; /**we can adjust this for early warnings*/
    
    
    
    /**interface for receiving the current memory state*/
    public static int getMemoryState() {
        return memory_state;
    }

    /**Searches for current oom-value and system state*/
    /**We can better predict the chances by monitoring the file pages in the zone than monitoring free*/
    
    public static void updateMemoryStatus() {
        Iterator<Integer> iterator = oom_score.iterator();
        long expectedFreePages=0;
        int key = 0, value=0;
        
        while(iterator.hasNext())
        {
            if(!((value=iterator.next())<oom_adj))
            {
                /**Searching for a match*/
                key = value;
                break;
            }
            
        }
        
        expectedFreePages = oom_map.get(key);
                
        if(nrFreePages< expectedFreePages) {
            memory_state = MEMORY_CRITICAL;
        }
        else if(nrFreePages< (expectedFreePages-warningAt)) {
            memory_state = MEMORY_LOW;
        }
        else {
            memory_state = MEMORY_OK;
        }
    }
    
    /**update and print values to csv file*/
    public static void dumpString() {
        
        pid = android.os.Process.myPid();        
        vmUsage      = scanProcForField("/proc/"+pid+"/status", "VmRSS:"); 
        memFree      = scanProcForField("/proc/meminfo", "MemFree:");
        totMem       = scanProcForField("/proc/meminfo", "MemTotal:");
        nrFreePages  = scanProcForField("/proc/zoneinfo", "nr_file_pages");
        oom_adj      = scanProcForFieldInt("/proc/"+pid+"/oom_adj");
        Runtime info = Runtime.getRuntime(); 
        max_memory   = info.maxMemory();
        cur_memory   = (info.totalMemory() - info.freeMemory());
        
        String dumpData = pid+","+vmUsage + "," + memFree +","+totMem+","+nrFreePages+","+oom_adj+","+cur_memory+","+memory_state+"\n";
        
        
        try {
            FileOperations.write(dumpData);
        } catch (IOException e) {
            /**Error writing*/
            Log.e("cse_622", "Cannot write values to file");
            e.printStackTrace();
        }
        
    }
    
    
    /**scan and build the low memory killer parameter table*/
    public static void buildTable()
    {
        try {
            scanMemoryKiller("/sys/module/lowmemorykiller/parameters/minfree","/sys/module/lowmemorykiller/parameters/adj");
        } catch (IOException e) {
            Log.e("cse_622", "Cannot read oom specification file");
            e.printStackTrace();
            return;
        }
        
        if(free_pages.isEmpty()||oom_score.isEmpty()||(free_pages.size()!=oom_score.size())) {
            Log.e("cse_622", "Something has wen't wrong. OOM table size's mismatch");
            return;
        }
        else
        {
            Iterator<Integer> iterator  = free_pages.iterator();
            Iterator<Integer> iterator1 = oom_score.iterator();
            
            while(iterator.hasNext() && iterator1.hasNext()) {                                
                int key   = iterator1.next();
                int value = iterator.next();
                
                oom_map.put(key, value);
            }
        }
        
    }
    
    /** Maximum heap space allowed for this application. */
    public static long getMaxHeap() {
        return Runtime.getRuntime().maxMemory();
    }

    /** Current allocated heap space. */
    public static long getCurrentHeap() {
        return Runtime.getRuntime().totalMemory();
    }

    /** Current used heap space. */
    public static long getUsedHeap() {
        return getCurrentHeap() - Runtime.getRuntime().freeMemory();
    }
    
    /** Current heap used as percent of the maximum heap space allowed for this application. */
    public static double heapUsage() {
        return getMaxHeap() / getUsedHeap();
    }

    /** Total system memory. */
    public static long getTotalMem() {
        return scanProcForField("/proc/meminfo", "MemTotal");
    }

    /** Available system memory. */
    public static long getFreeMem() {
        return scanProcForField("/proc/meminfo", "MemFree");
    }
    
    
    /**
     * Check if the application is backgrounded.
     * 
     * @return true if backgrounded, otherwise false
     */
    public static boolean isBackgrounded() {
        File file = new File("/proc/" + android.os.Process.myPid() + "/oom_adj");
        long oom_adj = -1;
        
        try {
            Scanner scanner = new Scanner(file);
            oom_adj = scanner.nextLong();
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return oom_adj != 0 ? true : false;
    }

    /** 
     *  The available memory on the system. This number should not be considered absolute: due 
     *  to the nature of the kernel, a significant portion of this memory is actually in use and 
     *  needed for the overall system to run well.
     */
    public static long getAvailableMem(Context context) {
        ActivityManager activityManager =  (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    /** 
     *  The threshold of availMem at which we consider memory to be low and start killing 
     *  background services and other non-extraneous processes. 
     */
    public static long getThreshold(Context context) {
        ActivityManager activityManager =  (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.threshold;
    }

    private static long scanProcForField(String path, String field) {
        File file = new File(path);
        Scanner scanner = null;


        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String token = scanner.next();
                if (token.equals(field))
                    return scanner.nextLong(); 
                scanner.nextLine();
            }
        } catch (Exception e) {
            Log.e("MemoryUtil", "Error scanning " + file + " for " + field, e);
        } finally {
            if (scanner != null)
                scanner.close();
        }
        return -1;
    }
    
    private static int scanProcForFieldInt(String path) {
        File file = new File(path);
        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                return scanner.nextInt(); 
            }
        } catch (Exception e) {
            Log.e("MemUtil", "Error scanning " + file, e);
        } finally {
            if (scanner != null)
                scanner.close();
        }
        return -1;
    }



    private static void scanMemoryKiller(String path_free_pages, String path_score) throws IOException {
        FileReader input  = new FileReader(path_free_pages);
        FileReader input1 = new FileReader(path_score);
    
        BufferedReader buf = new BufferedReader(input);
        String           temp = null;
    
        while((temp = buf.readLine())!=null) {
            String[] stringFreePages = temp.split(",");

            for(int i=0; i<stringFreePages.length; i++) {
                free_pages.add(Integer.parseInt(stringFreePages[i]));
            }
        }
    
        temp = null;
        buf.close();
    
        BufferedReader buf1 = new BufferedReader(input1);
    
        while((temp = buf1.readLine())!=null) {
            String stringScore[] = temp.split(",");
        
            for(int j=0; j<stringScore.length; j++) {
                oom_score.add(Integer.parseInt(stringScore[j]));
            }
        }
    
        buf1.close();
    }
}