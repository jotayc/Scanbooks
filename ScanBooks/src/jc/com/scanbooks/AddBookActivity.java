package jc.com.scanbooks;

import java.util.List;

import jc.com.scanbooks.R;
import jc.com.scanbooks.res.Book;
import jc.com.scanbooks.res.DBHandler;
import jc.com.scanbooks.res.HtmlJsoup;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddBookActivity extends Activity {
    /** Called when the activity is first created. */
	  private static final String BS_PACKAGE = "com.google.zxing.client.android";
	  public static final int REQUEST_CODE = 0x0000c0de;
	  private Book libro = new Book();
	  private Book dbLibro = new Book();
	  
	  private HtmlJsoup html = new HtmlJsoup();
	  private String scancontent;
	  
	  private TextView lbl;
	  private TextView Autor;
	  private TextView Titulo;
	  private TextView Editorial;
	  private TextView Año;
	  private TextView Categoria;
	  private TextView Clasification;
	  private TextView isbn;
	  private Button boton;
	  private Activity activity;
	  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.addbook);
        
	    lbl		  =(TextView) findViewById(R.id.lblCodbar);
	    Autor     =(TextView) findViewById(R.id.autor);
	    Titulo    =(TextView) findViewById(R.id.titulo);
	    Editorial =(TextView) findViewById(R.id.editorial);
	    Año 	  =(TextView) findViewById(R.id.ano);
	    Categoria   =(TextView) findViewById(R.id.Categoria);
	    isbn 	  =(TextView) findViewById(R.id.isbn);
	    boton	  =(Button)findViewById(R.id.btncatalog);
	    
	    
	    
	    boton.setOnClickListener(new OnClickListener() {
			
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
    	      "Esta aplicacionn necesita Barcode Scanner, sino se cerrará. ¿Quiere instalarla?";
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
            	Toast.makeText(AddBookActivity.this, "Android market no esta instalado,no puedo instalar Barcode Scanner", Toast.LENGTH_LONG).show();
            }
          }     
        });
        downloadDialog.setNegativeButton(DEFAULT_NO, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
        	  
        	  Intent intent = new Intent(AddBookActivity.this, ViewflipperMenuActivity.class);
        	  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	  intent.putExtra("EXIT", true);
        	  startActivity(intent);
        	  
          }
        });
        return downloadDialog.show();
      }
    
    /*
     * Descarga la información de los libros de internet la guarda en la base de datos local, recupera los datos y los muestra en los 
     * text views 
     * En caso de error muestra un AlertDialog con su mensaje
     **/
     
      public void GetDatosLibro(){  
    	  
    	  //Conectamos con la pagina, extraemos los datos del libro a través del isbn.
    	  new Thread(new Runnable() {
      	    public void run() {  
      	      
      	      //DBHandler delDB = new DBHandler(getApplicationContext());
      	      //delDB.DeleteTable();
      	      
          	  DBHandler db = new DBHandler(activity);
          	  //db.resetTable();
          	  
      	        //Aquí ejecutamos nuestras tareas costosas
      	      libro = html.ExtraerHTML(scancontent);
 
      	      if(libro.getERROR() == "0"){
      	    	  long error =  db.addbook(scancontent, libro.getTitulo(), libro.getAutor(), libro.getAño(), 
      	    			        libro.getEditorial(), libro.getCategoria());
      	    	  
      	    	  if(error != -1){
      	    		  respuesta(libro);
      	    		  dbLibro = db.getBook(scancontent);
      	    		  
      	    		  if(dbLibro.getERROR() == "0"){
	      	    		  runOnUiThread(new Runnable() {
			      	    	 public void run() {
			      	    	 	 
			      	    	 //Como ya se vincularon los componentes, se podrá utilizar sin problemas.
			      	    	 //En esta parte siempre mostrar los resultados.
			      	    		
			  		    		  Autor.setText(dbLibro.getAutor());
			  		    		  Titulo.setText(dbLibro.getTitulo());
			  		    		  Editorial.setText(dbLibro.getEditorial());
			  		    		  Año.setText(dbLibro.getAño());
			  		    		  Categoria.setText(dbLibro.getCategoria());
			  		    		  isbn.setText(dbLibro.getISBN());
			      	    	 }
	      	    		  });
      	    		  }
      	    	  }else{
      	    		  libro.InsertError("1", "No se ha añadido, puede que el libro ya esté en tu colección");   		  
      	    	  }
  	      	}
		 
    	     runOnUiThread(new Runnable() {
				public void run() {
					
					if(libro.getERROR()=="1"){ 
			    		  showDialogo(libro.getMsg());
			    	  }  	  
			    	  if(dbLibro.getERROR() == "1"){	  
			    		  showDialogo(dbLibro.getMsg());
			    	  }
			    	  if(dbLibro.getERROR() == "0" && libro.getERROR()== "0"){
			    		  ClasificationDialog(scancontent,"¿Desea clasificar el libro ahora?");
			    		  Toast.makeText(getApplicationContext(),"El libro se ha añadido correctamente a tu colección", Toast.LENGTH_SHORT).show();
			    		  
			    	  }
				}
			});
		    	  
      	  }
      	}).start();
    	  
      }
      /*
       * Muestra el dialogo de petición de clasificación del libro añadido
       */
      public AlertDialog ClasificationDialog(final String isbn, String msg){
    	  
    	  final String DEFAULT_MESSAGE = msg; 
    	  
    	  AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

          dialog.setMessage(DEFAULT_MESSAGE);
          dialog.setCancelable(false);
          dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				SelectClasfDialog(isbn,"Clasificar libro como: Leido o Pendiente");
				
			}
		});
          dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            	 dialogInterface.cancel();
            	}
            });
    	  return dialog.show();
      }
      
      /*
       * Muestra la elección de clasificación y realiza la inserción en la BD
       * params:Isbn: contiene el isbn del libro a mostrar,msg: contiene el mensaje a mostrar en el dialogo. 
       */
      public AlertDialog SelectClasfDialog(final String Isbn,String msg){
    	  
    	  final String DEFAULT_MESSAGE = msg;
    	  final DBHandler db = new DBHandler(activity);
    	  
    	  AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

          dialog.setMessage(DEFAULT_MESSAGE);
          dialog.setCancelable(false);
          dialog.setPositiveButton("Leido", new DialogInterface.OnClickListener() {
  			
  			@Override
  			public void onClick(DialogInterface dialog, int which) {
  				// TODO Auto-generated method stub
  				
  				 db.addClassification(Isbn, "Leido");
  			}
          });        
          dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            	 dialogInterface.cancel();
            	}
            });
          dialog.setNeutralButton("Pendiente", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				 db.addClassification(Isbn, "Pendiente");	
			}
		});
    	  return dialog.show();
      }
      
      /*
       * Se lanza para mostrar avisos al usuario, normalmente con cualquier error
       * @params:msq_error: es el mensaje que vera el usuario.
       */
      
      public AlertDialog showDialogo(String msg_error){
    	  
    	  final String DEFAULT_MESSAGE = msg_error;
    	  final String DEFAULT_CLOSE = "OK";
    	  
    	  AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

          dialog.setMessage(DEFAULT_MESSAGE);
          dialog.setCancelable(false);
          dialog.setNegativeButton(DEFAULT_CLOSE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            	 dialogInterface.cancel();
            	}
            });
    	  return dialog.show();
      }
      
      
      /*
       * Se utiliza para mandar a la actividad anterior que se ha añadido correctamente y se actualice la lista ya que 
       * desde CollectionListActivity no actualiza la lista despues de agregar uno.
       */
      public void respuesta(Book book){
    	  Intent intent = getIntent();
    	  
    	  
    	  intent.putExtra("titulo", book.getTitulo());
    	  intent.putExtra("autor", book.getAutor());
    	  intent.putExtra("edit",book.getEditorial());
    	  intent.putExtra("cate",book.getCategoria());
    	  intent.putExtra("ano", book.getAño());
    	  intent.putExtra("isbn", scancontent); //TODO Error solucionado para v.1.1
    	  setResult(RESULT_OK, intent);
    	  
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
		          
		                   
		          GetDatosLibro();
		          lbl.setText("Este libro es...");
		          
		          
		          
		        }
		      }     
	  }
}