package jc.com.scanbooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jc.com.scanbooks.adapters.ExpandableListAdapter;
import jc.com.scanbooks.res.Book;
import jc.com.scanbooks.res.DBHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableRow;

public class CollectionListActivity extends Activity {

	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    ImageView btn_add;
    ImageView btn_catalogar;
    RadioButton radiobtnIzq;
    RadioButton radiobtnDrc;
    TableRow tableRow;
    Spinner spinner;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Activity activity;
    DBHandler db ;
    List<Book> Lista;
    
    int REQUEST_CODE = 1;
    boolean push,todos;
    
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.expandable_listview);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        push = false;
        
        activity = this;
        // preparing list data       
      	    	prepareListData();
      	    	//Si la lista sigue vacia despues de entrar en prepareListData, mandamos un aviso.
        		
		        if(Lista.isEmpty()){
		        	ShowDialogo("No tienes libros en tu coleccion, debes agregar alguno");
		        }
		        
		        listAdapter = new ExpandableListAdapter(activity, listDataHeader, listDataChild);		 
		        // setting list adapter
		        expListView.setAdapter(listAdapter);
		     
		        //Create spinner, local method.
		        
		        spinnerCreate();
		        btn_add = (ImageView) findViewById(R.id.btnadd);
		        btn_add.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(CollectionListActivity.this,AddBookActivity.class);
						startActivityForResult(intent,REQUEST_CODE);
					}
				});
		     
		        /////////// BOTON CATALOGAR //////////////
		        /*
		         * Funciona como un boton de ajustes, si presionas el boton aparecen radiogroups para clasificar los 
		         * libros.
		         * Es necesario un modulo 'setvisibility()' para controlar la aparición del los radiobuttons ya que solo es 
		         * posible alterar los elementos gráficos desde el adaptador. 
		         * 
		         * @params: la variable push permite controlar la pulsacion del botón.
		         * 
		         */
		        btn_catalogar = (ImageView) findViewById(R.id.btncatalg);
		        btn_catalogar.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						if (push == false) {
							push = true;
							btn_catalogar.setImageResource(R.drawable.ico_accept);
							spinner.setVisibility(View.GONE);
							
							
							listAdapter.setvisibility(1);       // True: Pedimos al adaptador que nos muestre los radiobuttons
							listAdapter.notifyDataSetChanged(); // Notificamos que ha habido cambios en el adaptador.
						}else if (push) {
							push = false;
							btn_catalogar.setImageResource(R.drawable.ico_categ);
							spinner.setVisibility(View.VISIBLE);
							
							listAdapter.setvisibility(-1);  //False: pedimos que oculte los radiobuttons
							listAdapter.notifyDataSetChanged();
						}				
					}
				});
    }
    /*
     *  Crea el menu de seleccion para clasificar los libros  por : Leidos, Pendientes o Todos.
     */
    
    private void spinnerCreate(){
    	
    	spinner = (Spinner) findViewById(R.id.spinner1);
    	spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
    		
    		//Que hacer cuando el elemento del spinner es seleccionado
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				//recoge la selección del spinner y manda una peticion de 
				if("Leidos".equals(spinner.getSelectedItem())){
					prepareListData("Leido");
					 
					listAdapter = new ExpandableListAdapter(activity, listDataHeader, listDataChild);
					listAdapter.sendStateSpinner("Leido");
				        // setting list adapter
				    expListView.setAdapter(listAdapter);
				    
					listAdapter.notifyDataSetChanged();
					todos = false;
					
				}else if ("Pendientes".equals(spinner.getSelectedItem())) {						
					prepareListData("Pendiente");
					 
					listAdapter = new ExpandableListAdapter(activity, listDataHeader, listDataChild);		
					listAdapter.sendStateSpinner("Pendiente");
				        // setting list adapter
				    expListView.setAdapter(listAdapter);
				    
					listAdapter.notifyDataSetChanged();
					todos = false;

					  }else if ("Todos".equals(spinner.getSelectedItem()) && todos == false) { //Todos: para que no repita dos veces la misma accion
						    prepareListData();
						    listAdapter = new ExpandableListAdapter(activity, listDataHeader, listDataChild);
						    listAdapter.sendStateSpinner("Todos");
					        // setting list adapter
					        expListView.setAdapter(listAdapter);
						    listAdapter.notifyDataSetChanged();
						    todos = true;
					  		}				
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub	
			}	
		});
    }
    
    /*
     * Prepara la lista y las sublistas sacando los libros de la base de datos. IMPORTANTE: Este proceso es muy pesado debe ejecutarse en
     * un hilo aparte.
     */
    private void prepareListData() {
    	
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        
        //Sacamos todos los libros almacenados en la base de datos       
      	db = new DBHandler(activity);
      	Lista = db.getAllBooks();  
            
        //Usamos un iterador para recorrer la lista
        Iterator<Book> it = Lista.iterator();
        
        for(int i=0;it.hasNext();i++){
        	Book Libro = new Book();
        	
        	Libro = it.next(); //Devuelve el elemento siguiente y avanza el iterador.
        	listDataHeader.add(Libro.getTitulo());
        	
        	List<String> Detalles = new ArrayList<String>();
        	Detalles.add("Autor: "+Libro.getAutor());
        	Detalles.add("Editorial: "+Libro.getEditorial());
        	Detalles.add("Categ: "+Libro.getCategoria());
        	Detalles.add("Año: "+Libro.getAño());
        	Detalles.add("ISBN: "+Libro.getISBN());
        	
        	listDataChild.put(listDataHeader.get(i),Detalles);    	
        }  
        todos = true; //Activo que se han listado todos los libros.
    }
    
    
private void prepareListData(String query) {
    	
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        
        //Sacamos todos los libros almacenados en la base de datos
      	db = new DBHandler(activity);
      	Lista = db.getAllBooks(query);  
            
        //Usamos un iterador para recorrer la lista
        Iterator<Book> it = Lista.iterator();
        
        for(int i=0;it.hasNext();i++){
        	Book Libro = new Book();
        	
        	Libro = it.next(); //Devuelve el elemento siguiente y avanza el iterador.
        	listDataHeader.add(Libro.getTitulo());
        	
        	List<String> Detalles = new ArrayList<String>();
        	Detalles.add("Autor: "+Libro.getAutor());
        	Detalles.add("Editorial: "+Libro.getEditorial());
        	Detalles.add("Categ: "+Libro.getCategoria());
        	Detalles.add("Año: "+Libro.getAño());
        	Detalles.add("ISBN: "+Libro.getISBN()); 	
        	listDataChild.put(listDataHeader.get(i),Detalles);    	
        }  
        todos = true; //Activo que se han listado todos los libros.
    }
    /*
     * AlertDialog para emitir errores al usuario.
     * @params: msg_error: es el mensaje que vera el usuario.
     */
    public AlertDialog ShowError(String msg_error){
  	  final String DEFAULT_TITLE = "ERROR";
  	  final String DEFAULT_MESSAGE = msg_error;
  	  final String DEFAULT_CLOSE = "Cerrar";
  	  
  	  AlertDialog.Builder errorDialog = new AlertDialog.Builder(activity);
  	  
  	  errorDialog.setTitle(DEFAULT_TITLE);
        errorDialog.setMessage(DEFAULT_MESSAGE);
        
        errorDialog.setNegativeButton(DEFAULT_CLOSE, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
          	
          	}
          });
  	  return errorDialog.show();
    }
    
    /*
     * AlertDialog para emitir avisos al usuario.
     * @params: msg_error: es el mensaje que vera el usuario.
     */
    public AlertDialog ShowDialogo(String aviso){
    	  
    	  final String DEFAULT_MESSAGE = aviso;
    	  final String DEFAULT_CLOSE = "OK";
    	  
    	  AlertDialog.Builder errorDialog = new AlertDialog.Builder(activity);
          errorDialog.setMessage(DEFAULT_MESSAGE);
          
          errorDialog.setNegativeButton(DEFAULT_CLOSE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            	
            	}
            });
    	  return errorDialog.show();
      }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode==RESULT_OK && requestCode==REQUEST_CODE){
    		
    	  Bundle bundle = data.getExtras();
    	  
    	 //Es necesario hacer esto  ya que desde esta actividad no se puede actualizar la lista.
    	  String Titulo = bundle.getString("titulo");
    	  
    	  List<String> list = new ArrayList<String>();
    	  
    	  list.add("Autor: "+bundle.getString("autor"));
    	  list.add("Editorial: "+bundle.getString("edit"));
    	  list.add("Categ: "+bundle.getString("cate"));
    	  list.add("Año: "+bundle.getString("ano"));
    	  list.add("ISBN: "+bundle.getString("isbn"));
    	  
    	  listAdapter.InsertBook(list,Titulo);	
    	}
    	
    }
   
}
	

