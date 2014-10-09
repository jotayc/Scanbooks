package jc.com.scanbooks.adapters;

import java.util.HashMap;
import java.util.List;

import jc.com.scanbooks.R;
import jc.com.scanbooks.res.Book;
import jc.com.scanbooks.res.DBHandler;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter{
		
	private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private DBHandler db;
	private int visible = 0; 
	List<Book> Lista;
	String spiner="";
	
	
	public ExpandableListAdapter(Context context, List<String> listDataHeader,
		  HashMap<String, List<String>> listChildData) {
		
	        this._context = context;
	        this._listDataHeader = listDataHeader;
	        this._listDataChild = listChildData;
	  
		}
	
	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		 return this._listDataChild.get(this._listDataHeader.get(groupPosition))
	                .get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
		
		final String childText = (String) getChild(groupPosition, childPosition);
		 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
 
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
 
        txtListChild.setText(childText);
        return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		 return this._listDataChild.get(this._listDataHeader.get(groupPosition))
	                .size();
	}

	@Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
        	//LayoutInflater carga el xml de cualquier layout así puedes acceder desde cualquier vista a sus elementos
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);  // Carga el layout que se necesita
        }
        
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        
       
        //*********************BOTON BORRAR*************************//
        ImageView delete = (ImageView) convertView.findViewById(R.id.ic_delete);
        delete.setOnClickListener(new OnClickListener() {
        	 
            public void onClick(View v) {
            	AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                builder.setMessage("¿Estas seguro de que desea borrar este libro de su coleccion?");
                builder.setCancelable(false);
                builder.setPositiveButton("Si",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
	                             String Tit =_listDataHeader.get(groupPosition);
	                             db = new DBHandler(_context);
	                             int res = db.DeleteBook(Tit);
	                             if(res>0){
	                            	 _listDataHeader.remove(groupPosition);	                            	 
	                            	 notifyDataSetChanged();
	                            	 AlertDialog.Builder builder = new AlertDialog.Builder(_context);
	                                 builder.setMessage("El libro se ha eliminado correctamente de tu colección");
	                                 builder.setCancelable(false);
	                                 builder.setNegativeButton("OK",
	                                         new DialogInterface.OnClickListener() {
	                                             public void onClick(DialogInterface dialog, int id) {
	                                                 dialog.cancel();
	                                             }
	                                         });
	                                 AlertDialog alertDialog = builder.create();
	                                 alertDialog.show();
	                             }  
                            }
                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                
                
                AlertDialog alertDialog = builder.create();
                alertDialog.show();   
            }
        });
        ////////// CATALOG BUTTON /////////
        /*
         * Se muestran u ocultan los radiobuttons para entrar o salir del modo clasificacion de libros
         * 
         * @params:'visible' controla la pulsación que viene de CollectionActivity.java
         */
        
        final RadioButton RadI = (RadioButton) convertView.findViewById(R.id.radiobtnI);
        final RadioButton RadD = (RadioButton) convertView.findViewById(R.id.radiobtnD);
        final RadioGroup RadGr = (RadioGroup) convertView.findViewById(R.id.radioGr);
        
        DBHandler dba = new DBHandler(_context);
        String Tit =_listDataHeader.get(groupPosition);
        String clasif = ""; 				//clasificacion del libro en el grupo
    	clasif = dba.getClassification(Tit);
    	Log.i("GUARDADATOS",Tit);			
    	int OK = 0;							
    	
        Log.i("GUARDADATOS", "Visibilidad Radbtns "+String.valueOf(RadI.getVisibility() == RadioButton.VISIBLE && RadD.getVisibility() == RadioButton.VISIBLE)+"; visible "+Integer.toString(visible));
        
        if(this.visible == 1){
        	
        	Log.i("CARGADATOS",  Tit+" -> "+clasif);
        	
        	if("Leido".equals(clasif) && !RadI.isChecked() && !RadD.isChecked()){
        		RadI.setChecked(true);
        		Log.i("CARGADATOS",  Tit+" -> "+clasif+" RadI Leido activado");
        	}
        	else if ("Pendiente".equals(clasif) && !RadI.isChecked() && !RadD.isChecked()){ 
        		RadD.setChecked(true);
        		Log.i("CARGADATOS",  Tit+" -> "+clasif+" RadD Pendiente activado");
        	}
        	
        	if(RadGr.getVisibility() == View.GONE){
	        	RadI.setVisibility(View.VISIBLE);
	            RadD.setVisibility(View.VISIBLE);
	    	    RadGr.setVisibility(View.VISIBLE);
	    	    delete.setVisibility(View.GONE);
        	}
    	    
        }else if (this.visible == -1 && RadGr.getVisibility() == View.VISIBLE) { // Entra si he presionado el botón para ocultar y si los radiobuttons están visibles.
        	
        		if(RadI.isChecked() && !"Leido".equals(clasif) ){
	    	    	Log.i("GUARDADATOS", Tit + " -> Actualizado a Leido");
	    	    	//sortList.put(Tit, "Leido");
	    	     	OK = dba.addClassificationTit(Tit, "Leido");
	    	     	
	    	     	if(OK == 1 && spiner.equals("Pendiente")){
	    	     	    _listDataHeader.remove(groupPosition);	 
	    	     	    _listDataChild.remove(Tit);   		
	    	     		//prepareListDataClas("Pendiente");
	    	     		notifyDataSetChanged();
	    	     	  Log.i("ELIMINADO", Tit+" eliminado del grupo: "+spiner);
	    	     	}		
	    	    }
	    	    else if (RadD.isChecked() && (!"Pendiente".equals(clasif))) {
	    	    	
	    	    	Log.i("GUARDADATOS", Tit + " -> Actualizado a Pendiente");
	    	    	//sortList.put(Tit, "Pendiente");
	    	    	OK = dba.addClassificationTit(Tit, "Pendiente");
	    	    	if(OK == 1 && spiner.equals("Leido")){
		    	     	  _listDataHeader.remove(groupPosition);
		    	     	  _listDataChild.remove(Tit); 	    		  
		    	     	  notifyDataSetChanged();
		    	     	  Log.i("ELIMINADO", Tit+" eliminado del grupo: "+spiner );
		    	     	}
				}
        		RadGr.clearCheck();        		
            	RadI.setVisibility(View.GONE);
                RadD.setVisibility(View.GONE);
        	    RadGr.setVisibility(View.GONE);
        	    delete.setVisibility(View.VISIBLE);
	    	    Log.i("GUARDADATOS", "Visibilidad Radbtns "+String.valueOf(RadI.getVisibility() == RadioButton.VISIBLE && RadD.getVisibility() == RadioButton.VISIBLE)+"; " +
	    	    		"boton visible "+Integer.toString(visible));
		}
        
        lblListHeader.setText(headerTitle);
        Log.i("POSICION", "Posicion del grupo: " + getGroupId(groupPosition));
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    /*
     * Es necesario tener esta funcion ya que desde la actividad no se puede actualizar la lista.
     */
    public void InsertBook(List<String>Details, String Tit){
    	
    	_listDataHeader.add(Tit);
    	_listDataChild.put(Tit, Details);
    	notifyDataSetChanged();
    }
    /*
     * Da la orden de muestra o no de los radiobuttons.
     * params:visible:puede ser 1,0 o -1 para indicar si mostrar o no, o una posicion neutral hasta que sea inicializada.
     */
    public void setvisibility(int visible){
    	
    	this.visible = visible;	
    }
    
     public void sendStateSpinner(String state){
    	 
    	 this.spiner = state;
     }
    
}