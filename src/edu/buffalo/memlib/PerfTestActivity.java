package edu.buffalo.memlib;

import java.util.*;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.jjoe64.graphview.*;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import edu.buffalo.memlib.manager.MemoryUtil;

public class PerfTestActivity extends Activity {
    static Test currentTest;

    // Settings views.
    private View settingsMenu;
    private SeekBar maxHeapSlider, optHeapSlider;
    private TextView maxHeapValue, optHeapValue;
    private NumberPicker sizePicker, countPicker;

    // Policy configured by the views.
    private Policy policy;

    // Graphing stuff
    private static GraphView graphView;
    private static GraphViewSeries heapSeries;

    private int dataPointCount;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perf);

        SwapLib.setPolicy(policy = new Policy());

        // Get the settings widgets.
        settingsMenu = findViewById(R.id.settingsMenu);

        maxHeapSlider = (SeekBar) findViewById(R.id.maxHeapSlider);
        optHeapSlider = (SeekBar) findViewById(R.id.optHeapSlider);

        maxHeapValue = (TextView) findViewById(R.id.maxHeapValue);
        optHeapValue = (TextView) findViewById(R.id.optHeapValue);

        sizePicker = (NumberPicker) findViewById(R.id.sizePicker);
        countPicker = (NumberPicker) findViewById(R.id.countPicker);

        SeekBar.OnSeekBarChangeListener osbcl = new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar == maxHeapSlider) {
                    policy.fgHeapMaxUsage = progress / 100.0;
                    maxHeapValue.setText(progress+"%");
                } else if (seekBar == optHeapSlider) {
                    policy.fgHeapOptUsage = progress / 100.0;
                    optHeapValue.setText(progress+"%");
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) { }
            public void onStopTrackingTouch(SeekBar seekBar) { }
        };
        maxHeapSlider.setOnSeekBarChangeListener(osbcl);
        optHeapSlider.setOnSeekBarChangeListener(osbcl);

        policy.fgHeapMaxUsage = .7;
        policy.fgHeapOptUsage = .6;

        maxHeapSlider.setProgress((int) (100 * policy.fgHeapMaxUsage));
        optHeapSlider.setProgress((int) (100 * policy.fgHeapOptUsage));

        sizePicker.setMinValue(1);
        sizePicker.setMaxValue(20);
        sizePicker.setValue(5);

        countPicker.setMinValue(1);
        countPicker.setMaxValue(300);
        countPicker.setValue(20);

        // Set up graph.
        graphView = new BarGraphView(this , "Iteration Timing");
        graphView.setManualYMinBound(0);

        LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
        layout.addView(graphView);

        clearGraph();
    }

    /** Toggle the settings view. */
    public void toggleSettings(View button) {
        View v = settingsMenu;
        settingsMenu.setVisibility(v.isShown() ? v.GONE : v.VISIBLE);
    }

    private class DataPoint extends GraphViewData {
        int color;

        public DataPoint(double ms, int color) {
            super(dataPointCount++, ms);
            heapSeries.appendData(this, false, dataPointCount-1);
            graphView.addSeries(heapSeries);
            this.color = color;
        }
    }

    /** Add a data point to the graph. */
    public void addDataPoint(final double ms, final int color) {
        runOnUiThread(new Runnable() {
            public void run() { new DataPoint(ms, color); }
        });
    }

    /** Clear the graph. */
    public void clearGraph() {
        dataPointCount = 1;
        GraphViewSeriesStyle seriesStyle = new GraphViewSeriesStyle();
        seriesStyle.setValueDependentColor(new ValueDependentColor() {
          public int get(GraphViewDataInterface data) {
            return ((DataPoint) data).color;
          }
        });
        heapSeries = new GraphViewSeries("aaa", seriesStyle, new GraphViewData[0]);
        graphView.removeAllSeries();
    }

    /** Start the test. If called again, stop test and restart. */
    public void startTest(View v) {
        Button button = (Button) v;
        //button.setTitle("Stop test");

        // Stop current test.
        if (currentTest != null) {
            currentTest.stop();
            clearGraph();
            currentTest = null;
        }

        // Configure policy from settings.
        policy.fgHeapMaxUsage = .01 * maxHeapSlider.getProgress();
        policy.fgHeapOptUsage = .01 * optHeapSlider.getProgress();

        // Get values from settings.
        int size = sizePicker.getValue() << 20;
        int count = countPicker.getValue();

        // Start the test.
        new Thread(currentTest = new Test(size, count) {
            public void onDone() {
                // TODO: Restore button to original state.
            }
        }).start();
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
        Set<Holder> set = new HashSet<Holder>();
        int size;
        int count;
        boolean stopped = false;

        /** Configure opt and max settings for test. */
        Test(int size, int count) {
            this.size = size;
            this.count = count;
        }

        private double time() {
            return System.nanoTime()/1E6;
        }

        public void run() {
            double total;

            // Populate
            total = 0;
            for (int i = 1; i <= count; i++) if (!stopped) {
                double time = time();
                set.add(new Holder(new byte[size]));
                time = time()-time;
                addDataPoint(time, 0xFFFF0000);
                total += time;
            }
            System.out.println("AVERAGE NEW "+(total/count));

            // Iterate
            total = 0;
            int i = 1;
            for (Holder r : set) if (!stopped) {
                double time = time();
                r.get();
                time = time()-time;
                addDataPoint(time, 0xFF0000FF);
                total += time;
            }
            System.out.println("AVERAGE ACCESS "+(total/count));

            stop();
            onDone();
        }

        /** Stop the test. */
        public synchronized void stop() {
            if (stopped)
                return;

            stopped = true;

            // Clean up since library can't...
            for (SwapReference sr : set) sr.clear();
            set = null;
            System.gc();
        }

        /** Override if you want. */
        public void onDone() { }
    }
}
