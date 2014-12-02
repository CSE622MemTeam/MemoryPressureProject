package edu.buffalo.memlib;

import java.util.*;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import edu.buffalo.memlib.manager.MemoryUtil;

public class PerfTestActivity extends Activity {
    static Thread currentTest;

    // Settings views.
    private View settingsMenu;
    private SeekBar maxHeapSlider, optHeapSlider;
    private TextView maxHeapValue, optHeapValue;

    // Policy configured by the views.
    private Policy policy;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.perf_test_activity);

        SwapLib.setPolicy(policy = new Policy());

        // Get the settings widgets.
        settingsMenu = findViewById(R.id.settingsMenu);

        maxHeapSlider = (SeekBar) findViewById(R.id.maxHeapSlider);
        optHeapSlider = (SeekBar) findViewById(R.id.optHeapSlider);

        maxHeapValue = (TextView) findViewById(R.id.maxHeapValue);
        optHeapValue = (TextView) findViewById(R.id.optHeapValue);
    }

    /** Toggle the settings view. */
    public void toggleSettings(View button) {
        View v = settingsMenu;
        settingsMenu.setVisibility(v.isShown() ? v.GONE : v.VISIBLE);
    }

    /** Start the test. If called again, stop test and restart. */
    public void startTest(View v) {
        Button button = (Button) v;
        //button.setTitle("Stop test");

        // Stop current test.
        if (currentTest != null) {
            currentTest.stop();
            currentTest = null;
        }

        // Configure policy from settings.
        policy.fgHeapMaxUsage = .01 * maxHeapSlider.getProgress();
        policy.fgHeapOptUsage = .01 * optHeapSlider.getProgress();

        // Start the test.
        currentTest = new Thread(new Test(1<<20, 30) {
            public void onDone() {
                // Restore button to original state.
                currentTest = null;
            }
        });
        currentTest.start();
    }

    /** Used to ensure random ordering in hash set. */
    private static class Holder extends SwapReference<Object> {
        static Random rand = new Random();
        int hash = rand.nextInt();

        public Holder(Object o) { super(o); }

        public int hashCode() { return hash; }
    }

    /** The actual test. Will be run on a separate thread. */
    private class Test implements Runnable {
        int size;
        int count;

        /** Configure opt and max settings for test. */
        Test(int size, int count) {
            this.size = size;
            this.count = count;
        }

        private double time() {
            return System.nanoTime()/1E6;
        }

        public void run() {
            Set<Holder> set = new HashSet<Holder>();
            double total;

            // Populate
            total = 0;
            for (int i = 1; i <= count; i++) {
                double time = time();
                set.add(new Holder(new byte[size]));
                time = time()-time;
                total += time;
            }

            // Iterate
            total = 0;
            int i = 1;
            for (Holder r : set) {
                double time = time();
                r.get();
                time = time()-time;
                total += time;
            }

            // Clean up since library can't...
            for (SwapReference sr : set) sr.clear();
            System.gc();

            onDone();
        }

        /** Override if you want. */
        public void onDone() { }
    }
}
