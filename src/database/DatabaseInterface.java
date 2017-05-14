package database;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface DatabaseInterface {
    public Set<String> getCategories();
    public boolean addCategory(String category);
    public boolean removeCategory(String category);

    /**
     * Gibt Liste der Buecher aus, GROUP BY Kategorie
     * @return Map mit Buechern, in Kategorien unterteilt
     */
   public Map<String, Set<Book>> getCatLists();
   /**
    * Setzt Liste der Buecher nach Kategorien sortiert
    * Map<String, Set<Book>>:
    * Key = Kategorie
    * Value = Menge der Buecher, die dieser Kategorie angehoeren
    * @param catLists
    */
   public void setCatLists(Map<String, Set<Book>> catLists);
   public boolean isCategory(String category);
   public boolean renameCategory(String oldCategory, String newCategory);
   public void clearCategory(String category);
   /**
    * isUpdated gibt Status des Informationsstandes des Files aus
    * @return true, gdw Datenbank enthaelt Veraenderungen gegenueber dem File
    */
   
   public boolean isUpdated();
   public void setUpdated(boolean isUpdated);
   public Date getLastUpdated();
   public void setLastUpdated(Date lastUpdated);
   
   public String getPath();
   public void setPath(String path);
   
   /**
    * Gibt Set aller Buecher einer bestimmten Kategorie zurueck
    * @param category
    * @return
    * @throws IllegalArgumentException
    */
   public Set<Book> getBooks(String category) throws IllegalArgumentException;
   /**
    * Gibt Set aller gelisteten Buecher aus
    * @return
    */
   public Set<Book> getAllBooks();
   /**
    * Prueft, ob Buch mit angegebenem Titel gelistet ist
    * @param title
    * @return
    */
   public boolean containsBook(String title);
   /**
    * Fuegt das angegebene Buch in die Kategorie ein, falls letztere vorhanden ist und kein weiteres Buch mit gleichem Titel vorhanden ist
    * @param book
    * @param category
    * @return true, gdw Buchmenge der Kategorie veraendert worden ist
    * @throws IllegalArgumentException
    */
   public boolean addBook(Book book, String category) throws IllegalArgumentException;
   /**
    * Loescht ein Buch mit gleichem Titel aus Kategorie, falls letztere vorhanden ist
    * @param book
    * @param category
    * @return true, gdw Buchmenge der Kategorie veraendert worden ist
    * @throws IllegalArgumentException
    */
   public boolean remBook(Book book, String category) throws IllegalArgumentException;
   /**
    * Loescht die aktuelle Datenbank
    */
   
   public void clear();
   
   /**
    * Liest Inhalt des von path spezifizierten Files in die Datenbank ein
    * File muss folgendes Format haben:
    * 
    *<lastUpdated>10.08.2016</lastUpdated>
    *<category name="Gelesen">
    *    <title>Harry Potter - Magic Beasts and where to find them</title>   <author>J. K. Rowling</author>  <date>2014</date>
    *</category>
    *<category name="Begonnen">
    *    <title>Das groﬂe ABC</title>    <author>Deine Mudda</author>    <date>2014</date>
    *</category>
    *
    * @param path
    * @throws IOException
    */
   public void readFromFile() throws IOException;
   /**
    * Schreibt Datenbank in das von path spezifizierte File
    * @throws IOException 
    */
   public void writeToFile() throws IOException;
   
   /**
    * Gibt eine Map mit allen Buechern aus, die title im Titel enthalten, gruppiert nach Kategorie
    */
   public Map<String, Set<Book>> searchBooksByTitle(String title);
   /**
    * Gibt eine Map mit allen Buechern aus, die author im Autor enthalten, gruppiert nach Kategorie
    */
   public Map<String, Set<Book>> searchBooksByAuthor(String author);
   /**
    * Gibt eine Map mit allen Buechern aus, die date im Datum enthalten, gruppiert nach Kategorie
    */
   public Map<String, Set<Book>> searchBooksByDate(String date);
   /**
    * Formatiert uebergebenen String in Datumstring, falls urspruengliches Format gueltig ist
    * Gueltig sind folgende Formate: "   "(leeres Datum), "2016", "April 2016", "20. April 2016"
    * @param date
    *          = der zu pruefende/formatierende String
    * @return formattierter Datumstring
    * @throws IllegalArgumentException, falls uebergebener String keinem Format entspricht
    */
   public String formatDate(String date) throws IllegalArgumentException;
}
