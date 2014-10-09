package jc.com.scanbooks.res;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class HtmlJsoup {

	
	private Book libro = new Book();
	
public Book ExtraerHTML(String isbnLibro){
		
		
		Document doc;
		try {
			
			//Se Conecta con la pagina y obtenemos su HTML
			doc = Jsoup.connect("http://www.elkar.com/es/liburu_fitxa/"+isbnLibro) //"http://www.lacentral.com/web/book/?id=9788483835210" //9788445001301
					.userAgent("Mozilla")
					.timeout(3000)
					.get();
			
			
			 
			 if(doc.select(".errore_box").isEmpty()){
					
					 Element Tit = doc.select(".titulu_nagusia").first();
					 Element Categoria = doc.select(".kategoria").first();
					 Element datosFicha = doc.select(".fitxa_info").first();
					 Elements lista = datosFicha.getElementsByTag("li");
	 
					String Titulo = Tit.text();
					String Aut = (lista.get(0).text()).substring(7);
					String categoria = Categoria.text();
					String Editorial = (lista.get(3).text()).substring(11); // Ejemplo de substring por si se quiere eliminar a la hora de introducirla en la BD
					String Año = (lista.get(5).text()).substring(15);
					String ISBN = (lista.get(2).text()).substring(6);
					
					
					libro.Insert(Titulo, Aut, Editorial, Año, categoria, ISBN,"","0", null);
					
			}		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			libro.InsertError("1", "Libro no encontrado o Existe algun problema con la conexión");	
		}	
		
		return libro;
		
	}

}
