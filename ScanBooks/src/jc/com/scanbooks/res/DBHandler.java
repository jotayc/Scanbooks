package jc.com.scanbooks.res;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHandler extends SQLiteOpenHelper {
	


	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 3;

	// Database Name
	private static final String DATABASE_NAME = "directbook";

	// Login table name
	private static final String TABLE_LIBRERIA = "libreria";

	// Login Table Columns names
	private static final String KEY_ISBN = "isbn";
	private static final String KEY_NOMBRE = "nombre";
	private static final String KEY_AUTOR = "autor";
	private static final String KEY_EDITORIAL = "editorial";
	private static final String KEY_AÑO = "año";
	private static final String KEY_CATEGORIA = "categoria";
	private static final String KEY_CONDICION = "condicion";
	
	//Constructor
	public DBHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	public void onCreate(SQLiteDatabase db) {
		//Creamos la tabla con sus atributos
		Log.i("INFO", Integer.toString(db.getVersion()));

		String CREATE_LIBRERY_TABLE = "CREATE TABLE " + TABLE_LIBRERIA + "("
				+ KEY_ISBN + " TEXT PRIMARY KEY," 
				+ KEY_NOMBRE + " TEXT UNIQUE,"
				+ KEY_AUTOR + " TEXT,"
				+ KEY_AÑO + " TEXT,"
				+ KEY_EDITORIAL + " TEXT," 
				+ KEY_CATEGORIA + " TEXT,"
				+ KEY_CONDICION + " TEXT"+")";
		
		db.execSQL(CREATE_LIBRERY_TABLE);
        
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
	
		
	  if((oldversion==1 && newversion>=2) || (oldversion==2 && newversion>=3)){			 
		db.execSQL("ALTER TABLE "+ TABLE_LIBRERIA + " ADD COLUMN " + KEY_CONDICION + " TEXT");
	   	Log.i("INFO", "Base de datos actualizada a la version 2");
	  }
		Log.i("INFO", "Version antigua: "+ Integer.toString(oldversion)+ "Version nueva: "+ Integer.toString(newversion));
	}
	
	public void DeleteTable(){
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete table if exist
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIBRERIA);
		db.close();
	}
	
	public void resetTable(){
		
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_LIBRERIA, null, null);
		
		db.close();
	}
	
	/*
	 * Add books in the local DB
	 * @params: All atributes of the table in the DB.
	 * @return res: return 1 if is OK. Return -1 means error. 
	 */
	public long addbook(String isbn, String nombre,String Autor, String año, String editorial,String categoria) {
		SQLiteDatabase db = this.getWritableDatabase();
		long res = -1;
		boolean ex = ExistBook(isbn);
		ContentValues values = new ContentValues();
		values.put(KEY_NOMBRE, nombre); // Name
		values.put(KEY_AUTOR, Autor); // Email
		values.put(KEY_ISBN, isbn); // Email
		values.put(KEY_EDITORIAL, editorial); // Created At
		values.put(KEY_CATEGORIA, categoria);
		values.put(KEY_AÑO, año);
		//values.put(KEY_CONDICION,clasif); //Read or not
		// Inserting Row
		if(ex == false){
			res = db.insert(TABLE_LIBRERIA, null, values);
		}
		db.close(); // Closing database connection
		
		return res;
	}
	/*
	 * Return all books in the DB
	 * @return book: return hashmap de tipo <Integer,Book> the key is an iterator.
	 */
	
	public List<Book> getAllBooks(){
		
		List<Book> Lista = new ArrayList<Book>();
		
		String selectQuery = "SELECT * FROM " + TABLE_LIBRERIA ;
		 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
	        while(!cursor.isAfterLast()){
	        	Book libro = new Book();
	        	
	        	libro.Insert(cursor.getString(1), cursor.getString(2), cursor.getString(4),cursor.getString(3),
	        			     cursor.getString(5), cursor.getString(0), cursor.getString(5), "0", null);	        	
	        	Lista.add(libro);
		        cursor.moveToNext();
		     }
        }
        cursor.close();
        db.close();
        
        return Lista;
	}
	
	public List<Book> getAllBooks(String classifi){
		
		List<Book> Lista = new ArrayList<Book>();
		String[] args = new String[]{classifi};
		String selectQuery = "SELECT * FROM " + TABLE_LIBRERIA + " WHERE " + KEY_CONDICION +"=?";
		 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, args);
        
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
	        while(!cursor.isAfterLast()){
	        	Book libro = new Book();
	        	
	        	libro.Insert(cursor.getString(1), cursor.getString(2), cursor.getString(4),cursor.getString(3),
	        			     cursor.getString(5), cursor.getString(0), cursor.getString(5), "0", null);	        	
	        	Lista.add(libro);
		        cursor.moveToNext();
		     }
        }
        cursor.close();
        db.close();
        
        return Lista;
	}
	
	
	/*
	 * Get one book of the DB
	 * @params: isb:ISBN of the book
	 * @return: Return books with details
	 */
	public Book getBook(String isb){
		
		Book libro = new Book();
		String selectQuery = "SELECT * FROM " + TABLE_LIBRERIA + " WHERE " + KEY_ISBN + "="+isb;
		 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        
        if(cursor.getCount() > 0){
        	cursor.moveToFirst();
        	libro.Insert(cursor.getString(1), cursor.getString(2), cursor.getString(4),cursor.getString(3),
        			     cursor.getString(5), cursor.getString(0), cursor.getString(5), "0", null);
	        }
        	//If there is an error, pass the msg in the book.
	        else{
	        	libro.InsertError("1", "No se encuentra el libro");
	        }

	        cursor.close();
	        db.close();
        
        return libro;
	}
	/*
	 * Delete a book
	 * @params: isbn:ISBN of the book.
	 * @return: return amount of row deleted. 0 if not.
	 */
	
	public int DeleteBook(String Titu){
		SQLiteDatabase db = this.getWritableDatabase();
		int OK = 0;
		String[] args = new String[]{Titu};
		
		OK = db.delete(TABLE_LIBRERIA, "nombre=?", args);
		
		db.close();
		return OK;
		
	}
	/*
	 * Modify the parameters in the DB
	 * @params: Atributes of books.
	 * @return: return the number of row modificated. 0 if not.
	 */
	public int UpdateBook(String isbn, String nombre,String Autor, String año, String editorial,String categoria){
		int OK = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues valores = new ContentValues();
		valores.put("nombre",nombre);
		valores.put("autor", Autor);
		valores.put("año",año);
		valores.put("editorial",editorial);
		valores.put("categoria",categoria);
		
		//Update the DB.
		OK = db.update(TABLE_LIBRERIA, valores, "isbn="+isbn,null);
		return OK;
	}
	/*
	 * Insert the clasification of the books.
	 * @params:ISBN, Clasifi: Read or pending
	 * @return: return the number of row modificated. 0 if not.
	 */
	public int addClassification(String isbn,String clasifi){
		int OK = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		String[] args = new String[]{isbn};
				
		ContentValues valores = new ContentValues();
		valores.put(KEY_CONDICION,clasifi);
		OK = db.update(TABLE_LIBRERIA, valores, "isbn=?",args);
		db.close();
		Log.i("DBHANDLER", Integer.toString(OK));
		
		return OK;	
	}
	/*
	 * Add the classification to the book, pass the title and it classification.
	 */
	
	public int addClassificationTit(String Tit,String clasifi){
		int OK = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		String[] args = new String[]{Tit};
		
		ContentValues valores = new ContentValues();
		valores.put(KEY_CONDICION,clasifi);
		OK = db.update(TABLE_LIBRERIA, valores, "nombre=?",args);
		db.close();
		Log.i("DBHANDLER", Integer.toString(OK));
		
		return OK;
		
	}
	/*
	 * Get the classification of book through of the title of the book. 
	 */
	public String getClassification(String title){
		
		String clas = "";
		String[] args = new String[] {title};
		String selectQuery = "SELECT "+ KEY_CONDICION +" FROM " + TABLE_LIBRERIA + " WHERE " + KEY_NOMBRE +"=?";
		 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, args);
		
        cursor.moveToFirst();
        if(cursor.getCount() > 0)	
        	clas = cursor.getString(0);
	    
      	cursor.close();
        db.close();
        
        return clas;

	}
	
	/*
	 * Proof if exist a book in the DB.
	 * return: true if exist.False if not.
	 */
	
	public boolean ExistBook(String isbn){
		boolean Exist=false;
		String selectQuery = "SELECT * FROM " + TABLE_LIBRERIA + " WHERE " + KEY_ISBN + "="+isbn;
		 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if(cursor.getCount() > 0){
        	Exist = true;
        }	
        
        return Exist;
	}
	
	
}
