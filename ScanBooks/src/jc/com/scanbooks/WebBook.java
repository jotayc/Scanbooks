package jc.com.scanbooks;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class WebBook extends Activity{
	
	private static final String BS_PACKAGE = "com.google.zxing.client.android";
	public static final int REQUEST_CODE = 0x0000c0de;
	private String scancontent;
	private Activity activity;
	
	
	private WebView myWebView;
	private Button scanbtn;
	private TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.webviewly);
		activity=this;
		myWebView = (WebView) findViewById(R.id.WebView);
		scanbtn = (Button) findViewById(R.id.btnWebView);
		textView = (TextView) findViewById(R.id.WebViewText);
		
		
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.getSettings().setUserAgentString("Android");
		myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		
		
		scanbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
				    Intent intentScan = new Intent(BS_PACKAGE + ".SCAN");
				    intentScan.putExtra("PROMPT_MESSAGE", "Enfoque entre 9 y 11 cm.viendo sólo el código de barras");
				    String targetAppPackage = findTargetAppPackage(intentScan);
				    if (targetAppPackage == null) {
				      showDownloadDialog();
				    } else startActivityForResult(intentScan, REQUEST_CODE);
				}
			});   
		
		
	}
	/*
     * Busca el paquete instalado de BarCode Scan.
     * 
     */
    private String findTargetAppPackage(Intent intent) {
        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> availableApps = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (availableApps != null) {
          for (ResolveInfo availableApp : availableApps) {
            String packageName = availableApp.activityInfo.packageName;
            if (BS_PACKAGE.contains(packageName)) {
              return packageName;
            }
          }
        }
        return null;
      }
    
    /*
     * Se lanza en caso de que no tengamos instalado Barcode Scanner
     */
    private AlertDialog showDownloadDialog() {
    	  final String DEFAULT_TITLE = "Instalar Barcode Scanner?";
    	  final String DEFAULT_MESSAGE =
    	      "Esta aplicacionn necesita Barcode Scanner. ¿Quiere instalarla?";
    	  final String DEFAULT_YES = "Si";
    	  final String DEFAULT_NO = "No";

        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(DEFAULT_TITLE);
        downloadDialog.setMessage(DEFAULT_MESSAGE);
        downloadDialog.setPositiveButton(DEFAULT_YES, new DialogInterface.OnClickListener() {
        	
        	
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            Uri uri = Uri.parse("market://details?id=" + BS_PACKAGE);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            try {
              activity.startActivity(intent);
            } catch (ActivityNotFoundException anfe) {
                // Hmm, market is not installed
            	Toast.makeText(activity, "Android market no esta instalado,no puedo instalar Barcode Scanner", Toast.LENGTH_LONG).show();
            }
          }     
        });
        downloadDialog.setNegativeButton(DEFAULT_NO, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
        	  
        	  Intent intent = new Intent(WebBook.this, ViewflipperMenuActivity.class);
        	  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	  intent.putExtra("EXIT", true);
        	  startActivity(intent);
        	  
          }
        });
        return downloadDialog.show();
      }
	
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (requestCode == REQUEST_CODE) {
	        if (resultCode == Activity.RESULT_OK) {
	        	
	        
	          scancontent = intent.getStringExtra("SCAN_RESULT");
	          String formatName = intent.getStringExtra("SCAN_RESULT_FORMAT");
	          byte[] rawBytes = intent.getByteArrayExtra("SCAN_RESULT_BYTES");
	          int intentOrientation = intent.getIntExtra("SCAN_RESULT_ORIENTATION", Integer.MIN_VALUE);
	          Integer orientation = intentOrientation == Integer.MIN_VALUE ? null : intentOrientation;
	          String errorCorrectionLevel = intent.getStringExtra("SCAN_RESULT_ERROR_CORRECTION_LEVEL");
	          Toast.makeText(this, scancontent, Toast.LENGTH_LONG).show();
	                   
	          /*myWebView.setWebViewClient(new WebViewClient(){
	  			
		  			@Override
		  		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		  		        view.loadUrl("https://www.google.es/search?q="+scancontent+"&hl=es&gl=es&source=lnms&tbm=shop");
		  		        return true;
		  		    }
	  		 	});*/
	  		
	  		 myWebView.loadUrl("https://www.google.es/search?q="+scancontent+"&hl=es&gl=es&source=lnms&tbm=shop");
	  		
	        } 
	        if(scancontent != null){
	           textView.setText("ISBN Buscado: "+scancontent);
	        }
	          
	    }
	           
  }
    
	
	
    
}
