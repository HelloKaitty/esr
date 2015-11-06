package tw.idv.gocar.kn1.esrlive;


import  android.app.Activity ;
import  android.app.AlertDialog;
import android.app.ProgressDialog;
import  android.content.DialogInterface ;

import android.net.Uri;
import  android.os.Bundle ;


import android.util.Log;
import  android.view.KeyEvent ;
import android.view.Window;
import android.webkit.ValueCallback;
import  android.webkit.WebChromeClient ;
import android.webkit.WebSettings;
import  android.webkit.WebView ;
import  android.webkit.WebViewClient ;
import android.content.Intent;
import android.widget.ProgressBar;




public class MainActivity extends Activity {
    WebView mWebview;
    private ValueCallback<Uri> mUploadMessage; // FileUpload
    private final static int FILECHOOSER_RESULTCODE = 1;
    private final String HOMEPAGE = "http://project.gocar.idv.tw/esr/";


    @Override
    public void onCreate(Bundle saveInstanceState){ // 主要WebView
        //final Activity mActivity = this;

        super.onCreate(saveInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        // WebView
        mWebview = (WebView) findViewById(R.id.webview);
        mWebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebview.getSettings().setJavaScriptEnabled(true); // 支援JAVA SCRIPT
        mWebview.getSettings().setSupportMultipleWindows(true);
        mWebview.getSettings().setAllowFileAccess(true); // 檔案
        mWebview.setWebViewClient(new InsideWebViewClient());
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); //  不要cache
        mWebview.setWebChromeClient(new WebviewFileUpload());
        mWebview.loadUrl(HOMEPAGE); // 讀取WEBVIEW
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) { // 回傳所選擇的圖檔
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }
    public class WebviewFileUpload extends WebChromeClient { // 處理檔案上傳
        // For Android 3.0-
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }
        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
        }

        //For Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }
        @SuppressWarnings("all")
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            String acceptTypes[] = fileChooserParams.getAcceptTypes();
            String acceptType = "";
            for (int i = 0; i < acceptTypes.length; ++ i) {
                if (acceptTypes[i] != null && acceptTypes[i].length() != 0)
                    acceptType += acceptTypes[i] + ";";
            }
            if (acceptType.length() == 0)
                acceptType = "image/*";
            final ValueCallback<Uri[]> finalFilePathCallback = filePathCallback;
            ValueCallback<Uri> uploadMsg = new ValueCallback<Uri>() {
                @Override
                public void onReceiveValue(Uri value) {

                    Uri[] result;
                    if (value != null)
                        result = new Uri[]{value};
                    else
                        result = null;
                    finalFilePathCallback.onReceiveValue(result);
                }
            };
            openFileChooser(uploadMsg, acceptType);
            return true;
        }
    }

    public  boolean onKeyDown ( int keyCode, KeyEvent event )  { //捕捉返回鍵
        if  ( ( keyCode ==  KeyEvent . KEYCODE_BACK )  && mWebview. canGoBack ( ) )  {
            mWebview.goBack() ;
            return  true ;
        } else  if ( keyCode ==  KeyEvent . KEYCODE_BACK ) {
            ConfirmExit ( ) ; //按了返回鍵，但已經不能返回，則執行退出確認
            return  true ;
        }
        return  super . onKeyDown ( keyCode, event ) ;
    }
    public  void ConfirmExit ( ) { //退出確認
        AlertDialog. Builder ad = new AlertDialog. Builder (MainActivity. this ) ;
        ad. setTitle ( "離開" ) ;
        ad. setMessage("安安~請問真的要離開嗎?") ;
        ad. setPositiveButton("是", new DialogInterface.OnClickListener() { //退出按鈕
            @Override
            public void onClick(DialogInterface dialog, int i) {
                //   TODO Auto-generated method stub
                MainActivity.this.finish(); //關閉activity
            }
        }) ;
        ad. setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                //不退出不用執行任何操作
            }
        }) ;
        ad. show() ; //顯示對話框
    }
    public class InsideWebViewClient extends WebViewClient {
        ProgressDialog progressDialog;
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { // 按連結不開新視窗
            view.loadUrl(url);
            return true;
        }
        //Show loader on url load
        public void onLoadResource (WebView view, String url) {

            // if url contains string androidexample
            // Then show progress  Dialog
            if (progressDialog == null && url.contains("gocar")) {
                // in standard case YourActivity.this
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("資料讀取中...");
                progressDialog.show();
            }
        }

        // Called when all page resources loaded
        public void onPageFinished(WebView view, String url) {
            try{
                // Close progressDialog
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }catch(Exception exception){
                exception.printStackTrace();
            }

        }

    }

}
