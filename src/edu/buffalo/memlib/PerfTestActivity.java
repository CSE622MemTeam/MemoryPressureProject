package edu.buffalo.memlib;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import edu.buffalo.memlib.manager.MemoryUtil;

public class PerfTestActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Only run test if this is the first time starting the view.
        if (savedInstanceState == null)
            new Thread(test).start();
    }

    private static double time() {
        return System.nanoTime()/1E6;
    }

    /** Used to ensure random ordering in hash set. */
    private static class Holder extends SwapReference<Object> {
        static Random rand = new Random();
        int hash = rand.nextInt();

        public Holder(Object o) { super(o); }

        public int hashCode() { return hash; }
    }

    // The actual test. Will be run on a separate thread.
    private Runnable test = new Runnable() {
        public void run() {
            Set<Holder> set = new HashSet<Holder>();
            double total;
            int mb = 35;

            SwapLib.setPolicy(new Policy() {{
                fgHeapOptUsage = 0.7;
                fgHeapMaxUsage = 0.5;
            }});
            System.out.println("Generating test objects...");
            total = 0;
            for (int i = 1; i <= mb/5; i++) {
                double time = time();
//                System.out.println("Creating 100KB... "+i);
                set.add(new Holder(new byte[5<<20]));
                time = time()-time;
//                System.out.println("   time: "+time+"ms");
                total += time;
            }
            System.out.println("Done creating! total = "+total);

            SwapLib.setPolicy(new Policy() {{
                fgHeapOptUsage = 1.0;
                fgHeapMaxUsage = 1.0;
            }});
            System.out.println("Starting iteration...");
            total = 0;
            int i = 1;
            for (Holder r : set) {
                double time = time();
//                System.out.println("Getting 100KB... "+i++);
//                System.out.println("   Swapped? "+r.isSwappedOut());
                r.get();
                time = time()-time;
//                System.out.println("   time: "+time+"ms");
                total += time;
            }
            System.out.println("Done iterating! total = "+total);
            for (SwapReference sr : set) sr.clear();
            System.gc();
            System.out.println(MemoryUtil.bytesToString(MemoryUtil.getUsedHeap()));
            
            /* Control Tests */
            System.out.println("Generating test objects...");
            total = 0;
            for ( i = 1; i <= mb; i++) {
                double time = time();
//                System.out.println("Creating 100KB... "+i);
                set.add(new Holder(new byte[1<<20]));
                time = time()-time;
//                System.out.println("   time: "+time+"ms");
                total += time;
            }
            System.out.println("Done creating! total = "+total);

            System.out.println("Starting iteration...");
            total = 0;
             i = 1;
            for (Holder r : set) {
                double time = time();
//                System.out.println("Getting 100KB... "+i++);
//                System.out.println("   Swapped? "+r.isSwappedOut());
                r.get();
                time = time()-time;
//                System.out.println("   time: "+time+"ms");
                total += time;
            }
            System.out.println("Done iterating! total = "+total);
        }
    };
}
