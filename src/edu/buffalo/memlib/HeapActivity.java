package edu.buffalo.memlib;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import edu.buffalo.memlib.manager.MemoryUtil;

public class HeapActivity extends Activity {
    private static ArrayList<Collection<byte[]>> swappable = new ArrayList<Collection<byte[]>>();
    private TextView usage;
    private TextView allocated;
    private TextView arraylist;
    private TextView linkedlist;
    private TextView hashet;
    private static GraphView graphView;
    private static GraphViewSeries heapSeries = new GraphViewSeries(new GraphViewData[] {new GraphViewData(0, 0)});
    private static long total = MemoryUtil.getUsedHeap() >> 20;
    private static int alist = 0;
    private static int llist = 0;
    private static int hset = 0;
    private static int mode = 0;
    
    protected void onStart() {
        super.onStart();
        updateUsage();
    }
    
    protected void onResume() {
        super.onResume();
        updateUsage();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heaper);
        
        usage = (TextView)findViewById(R.id.textView1);
        allocated = (TextView)findViewById(R.id.textView2);
        arraylist = (TextView)findViewById(R.id.textView3);
        linkedlist = (TextView)findViewById(R.id.textView4);
        hashet = (TextView)findViewById(R.id.textView5);
        
        graphView = new LineGraphView(this , "Heap per Total Allocated");
        LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
        layout.addView(graphView);
        graphView.addSeries(heapSeries);
         
        Button tenButton = (Button)findViewById(R.id.button1);
        tenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                allocateMemory(10, 1);
                updateUsage();
            }
        });
        
        Button twentyButton = (Button)findViewById(R.id.button2);
        twentyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                allocateMemory(20, 1);
                updateUsage();
            }
        });
        
        Button fiftyButton = (Button)findViewById(R.id.button3);
        fiftyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                allocateMemory(50, 1);
                updateUsage();
            }
        });

        updateUsage();
    }
    
    /** Will allocate memory in MB in chunks of a given granularity. */
    private void allocateMemory(int mb, int granularity) {
        int bytes = granularity << 20;
        
        for(int i = granularity; i <= mb; i += granularity) {
            Collection<byte[]> collection;
            
            if (mode % 3 == 0) {
                collection = SwapObjects.getArrayList();
                alist += 1;
            }
            else if (mode % 3 == 1) {
                collection = SwapObjects.getHashSet();
                hset += 1;
            }
            else {
                collection = SwapObjects.getLinkedList();
                llist += 1;
            }
            
            collection.add(new byte[bytes]);
            swappable.add(collection);
            mode += 1;
        }
        
        total += mb;
    }
    
    private void updateUsage() {
        usage.setText("Current Heap: " + 
                      MemoryUtil.bytesToString(MemoryUtil.getUsedHeap()) +
                      " / " + 
                      MemoryUtil.bytesToString(MemoryUtil.getMaxHeap()));
        allocated.setText("Total Allocated: " + total + "MB");
        arraylist.setText("ArrayLists: " + alist);
        linkedlist.setText("LinkedLists: " + llist);
        hashet.setText("HashSets: " + hset);
        heapSeries.appendData(new GraphViewData(mode, MemoryUtil.getUsedHeap() >> 20), false, 10);
        graphView.addSeries(heapSeries);
    }
}
