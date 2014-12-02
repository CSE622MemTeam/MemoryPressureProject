package edu.buffalo.memlib;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.buffalo.memlib.manager.FileOperations;
import edu.buffalo.memlib.manager.MemoryUtil;

public class ClassicTesterActivity extends Activity {
    List<byte[]> list = SwapObjects.getArrayList();
    TextView VmUsage;
    
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
        
        setContentView(R.layout.activity_main);
        
        TextView title   = (TextView)findViewById(R.id.title);
       ;
        VmUsage = (TextView)findViewById(R.id.VMUsage);
        
        SimpleDateFormat simpleDate =new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        
        title.setText(R.string.AppInfo);
      
        
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(this, "External SD card not mounted", Toast.LENGTH_LONG).show();
        }
        else {
            FileOperations.createFile();
            
            try {
                FileOperations.write(simpleDate.format(new Date(0))+"\n");
                FileOperations.writeHeaders();
                MemoryUtil.dumpString();
            } catch (IOException e) {
                Log.e("ERROR_622", "Cannot write to file");
                e.printStackTrace();
            }
        }
        
        updateUsage();
        MemoryUtil.buildTable();
        
        Button tenButton = (Button) findViewById(R.id.button_10MB);
        tenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               allocateMemory(10<<20); 
               updateUsage();
            }
        });
        
        Button oneButton = (Button)findViewById(R.id.button_1MB);
        oneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                allocateMemory(1<<20);
                updateUsage();
            }
        });
        
        Button pushButton = (Button)findViewById(R.id.button2);
        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.size();
                updateUsage();
            }
        });
        
        Button popButton = (Button)findViewById(R.id.button1);
        popButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwapReference.swapOutLeastUsed();
                updateUsage();
            }
        });
        
        
        Button updateButton = (Button)findViewById(R.id.button3);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
     
                updateUsage();
            }
        });
    }
    
    private void updateUsage() {
        Runtime info = Runtime.getRuntime(); 
        long max_memory  = info.maxMemory();
        long cur_memory  = (info.totalMemory() - info.freeMemory());
        VmUsage.setText("VM memory: " + 
                        MemoryUtil.bytesToString(cur_memory) +
                        " / " + 
                        MemoryUtil.bytesToString(max_memory));
    }

    /** Will allocate bytes MB memory. */
    private void allocateMemory(int bytes) {
        list.add(new byte[bytes]);
    }
}
