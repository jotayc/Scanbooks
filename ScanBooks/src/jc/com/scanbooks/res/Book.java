package jc.com.scanbooks.res;


public class Book {

	private String Titulo = null;
	private String Autor = null;
	private String Editorial = null;
	private String Año = null;
	private String Categoria = null;
	private String Isbn = null;
	private String Clasif = null;
	
	private String ERROR = null;
	private String msg = null;
	
	
	
	public Book() {
		// TODO Auto-generated constructor stub
	}
	
	public Book(String Tit, String Aut, String Edit, String Ano, String Categ,String isb,String clasifi){
		
		
		Titulo = Tit;
		Autor = Aut;
		Editorial = Edit;
		Año = Ano;
		Categoria = Categ;
		Isbn = isb;
		Clasif = clasifi;
		ERROR = "0";
		msg = "";
	}
	
	public void Insert(String Tit, String Aut, String Edit, String Ano, String Categ,String isb, String clasifi ,String err, String mensaje){
		
		Titulo = Tit;
		Autor = Aut;
		Editorial = Edit;
		Año = Ano;
		Categoria = Categ;
		Isbn = isb;
		ERROR = err;
		msg = mensaje;
		Clasif = clasifi;
	}
	
	public void InsertError(String err, String msg){
		
		ERROR = err;
		this.msg = msg;
	}
	
	public String getTitulo(){
		return Titulo;
	}
	
	public String getAutor(){
		return Autor;
	}
	
	public String getEditorial(){
		return Editorial;
	}
	
	public String getAño(){
		return Año;
	}
	
	public String getCategoria(){
		return Categoria;
	}
	
	public String getISBN(){
		return Isbn;
	}
	
	public String getClasification(){
		return Clasif;
	}
	public String getERROR(){
		return ERROR;
	}
	public String getMsg(){
		return msg;
	}
	
	public Book GetBook(){
		return this;	
	}
	
	
	
}
