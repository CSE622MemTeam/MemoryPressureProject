package edu.buffalo.memlib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.launcher);

        Button classic = (Button) findViewById(R.id.button1);
        classic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ClassicTesterActivity.class);
                startActivity(intent);
            }
        });
        
        Button debug = (Button)findViewById(R.id.button2);
        debug.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DebugActivity.class);
                startActivity(intent);
            }
        });

        Button perfTest = (Button)findViewById(R.id.button3);
        perfTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PerfTestActivity.class);
                startActivity(intent);
            }
        });
    }
}
