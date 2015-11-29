/*
 * Copyright (C) Apache licence, Inc. All Rights Reserved.
 */
package tony.com.logview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import tony.com.logmodel.LogView;
import tony.com.logmodel.LogWindow;

public class MainActivity extends AppCompatActivity {

    int i = 0;
    Button testButton;
    Button startActivityButton;
    Button finishTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testButton = (Button) findViewById(R.id.mybutton);
        startActivityButton = (Button) findViewById(R.id.startActivity);
        finishTestButton = (Button) findViewById(R.id.finishActivity);
        final LogWindow logWindow = new LogWindow(this, getApplication()).setregisterLifeCycleInStop(false);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // createFloatView();
                logWindow.creatLogView();
            }
        });
        startActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // onDestroy();
                startActivity(new Intent(getBaseContext(), TestActivity.class));
            }
        });
        finishTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new Thread() {
            @Override
            public void run() {
                super.run();

                while (true) {
                    System.out.println("test-->" + (++i));
                    Log.e("srcomp", "Just test error work: " + i);
                    Log.d("srcomp", "Just test: " + (++i));
                    Log.i("srcomp", "Just test: info : " + (++i));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("srcomp", "Activity destory");
    }

}