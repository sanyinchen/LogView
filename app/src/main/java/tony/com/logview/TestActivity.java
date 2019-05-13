/*
 * Copyright (C) Apache licence, Inc. All Rights Reserved.
 */
package tony.com.logview;

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

import tony.com.logmodel.LogWindow;

public class TestActivity extends AppCompatActivity {
    private LogWindow logWindow;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        logWindow = new LogWindow(this, getApplication()).setregisterLifeCycleInStop(false);
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
        webView.loadUrl("https://developer.android.google.cn/");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }

    }

    @Override
    public void finish() {
        super.finish();
        logWindow.dismiss();
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
