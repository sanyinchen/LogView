/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package baidu.com.logview;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import baidu.com.logmodel.LogWindow;

public class TestActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        LogWindow logWindow = new LogWindow(this, getApplication()).setregisterLifeCycleInStop(false);
        logWindow.creatLogView();
        Button button = (Button) findViewById(R.id.test_finish);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        WebView webView = (WebView) findViewById(R.id.test_web);
        webView.setWebChromeClient(new DefaultWebChromeClient());
        webView.setWebViewClient(new DefaultWebViewClient());
        // webView.loadUrl("http://www.sanyinchenblog.com/");
        webView.loadUrl("https://plus.google.com/u/0/100465464266192894461/posts");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }

    }

    private class DefaultWebViewClient extends WebViewClient {

    }

    private class DefaultWebChromeClient extends WebChromeClient {

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

            Log.d("srcomp", "[console]" + consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);
        }
    }

}
