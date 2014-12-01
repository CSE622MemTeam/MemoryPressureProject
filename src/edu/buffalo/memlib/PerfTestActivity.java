package edu.buffalo.memlib;

import java.util.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PerfTestActivity extends Activity {
    static {
        SwapLib.setPolicy(new Policy() {{
            fgHeapOptUsage = 0.5;
            fgHeapMaxUsage = 0.5;
            bgHeapOptUsage = 0.5;
            bgHeapMaxUsage = 0.5;
        }});
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Set<SwapReference> set = new HashSet<SwapReference>();

        // Fill with 35MB of stuff.
        for (int i = 0; i < 35; i++) {
            System.out.println("Creating 1MB... "+i);
            set.add(new SwapReference(new byte[1<<20]));
        }
        System.out.println("Done creating!");

        // Iterate over set.
        double total = 0;
        for (SwapReference r : set) {
            double time = System.nanoTime();
            System.out.println("Getting 1MB...");
            System.out.println("Swapped: "+r.isSwappedOut());
            r.get();
            time = (System.nanoTime() - time)/1000/1000;
            System.out.println("get() time: "+time);
            total += time;
        }

        System.out.println("total time: "+total);
    }
}
