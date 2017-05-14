package database;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Database implements DatabaseInterface{
    private Map<String, Set<Book>> catLists;
    private Date lastUpdated;
    private boolean isUpdated;
    private String path;

    public Database(){
        setCatLists(new TreeMap<>());
        setUpdated(true);
        setLastUpdated(new Date());
        setPath(null);
    }
    /**
     * Konstruktor, der Datenbank direkt mit Inhalt aus Datei initialisiert
     * @param path - Weg zur Datenbankdatei
     * @throws IOException 
     */
    public Database(String path) throws IOException{
        this();
        this.setPath(path);
        readFromFile();
    }
    
    public Set<String> getCategories() {
        return catLists.keySet();
    }
    public boolean addCategory(String category){
        if(getCategories().contains(category)){
            return false;
        }
        else{
            setUpdated(true);
            getCatLists().put(category, new TreeSet<Book>());
            return true;
        }
    }
    public boolean removeCategory(String category){
        if(!getCategories().contains(category)){
            return false;
        }
        else{
            getCatLists().remove(category);
            setUpdated(true);
            return true;
        }
    }

    
    /**
     * Gibt Liste der Buecher aus, GROUP BY Kategorie
     * @return Map mit Buechern, in Kategorien unterteilt
     */
    public Map<String, Set<Book>> getCatLists() {
        return catLists;
    }
    /**
     * Setzt Liste der Buecher nach Kategorien sortiert
     * Map<String, Set<Book>>:
     * Key = Kategorie
     * Value = Menge der Buecher, die dieser Kategorie angehoeren
     * @param catLists
     */
    public void setCatLists(Map<String, Set<Book>> catLists) {
        this.catLists = catLists;
        setUpdated(true);
    }
    public boolean isCategory(String category){
        return getCategories().contains(category);
    }
    
    public boolean renameCategory(String oldCategory, String newCategory){
        if(!isCategory(oldCategory) || getCategories().contains(newCategory)){
            return false;
        }
        getCatLists().put(newCategory, getCatLists().get(oldCategory));
        getCatLists().remove(oldCategory);
        setUpdated(true);
        return true;
    }
    public void clearCategory(String category){
        if(!isCategory(category)){
            throw new IllegalArgumentException("Kategorie existiert nicht");
        }
        getCatLists().get(category).clear();
        setUpdated(true);
    }
    
    /**
     * isUpdated gibt Status des Informationsstandes des Files aus
     * @return true, gdw Datenbank enthaelt Veraenderungen gegenueber dem File
     */
    public boolean isUpdated() {
        return isUpdated;
    }
    public void setUpdated(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }
    
    public Date getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
        setUpdated(true);
    }
    
    /**
     * Gibt Set aller Buecher einer bestimmten Kategorie zurueck
     * @param category
     * @return
     * @throws IllegalArgumentException
     */
    public Set<Book> getBooks(String category) throws IllegalArgumentException{
        if(!isCategory(category)){
            throw new IllegalArgumentException("Kategorie existiert nicht");
        }
        return getCatLists().get(category);
    }
    /**
     * Gibt Set aller gelisteten Buecher aus
     * @return
     */
    public Set<Book> getAllBooks(){
        Set<Book> setBooks = new TreeSet<>();
        for(String actCategory : getCategories()){
            setBooks.addAll(getBooks(actCategory));
        }
        return setBooks;
    }
    /**
     * Prueft, ob Buch mit angegebenem Titel gelistet ist
     * @param title
     * @return
     */
    public boolean containsBook(String title){
        for(Book book : getAllBooks()){
            if(book.getTitle().equalsIgnoreCase(title)){
                return true;
            }
        }
        return false;
    }
    /**
     * Fuegt das angegebene Buch in die Kategorie ein, falls letztere vorhanden ist und kein weiteres Buch mit gleichem Titel vorhanden ist
     * @param book
     * @param category
     * @return true, gdw Buchmenge der Kategorie veraendert worden ist
     * @throws IllegalArgumentException
     */
    public boolean addBook(Book book, String category) throws IllegalArgumentException{
        if(!isCategory(category)){
            throw new IllegalArgumentException("Kategorie existiert nicht");
        }
        for(Book book2 : getBooks(category)){
            if(book.getTitle().equalsIgnoreCase(book2.getTitle())){
                return false;
            }
        }
        getCatLists().get(category).add(book);
        setUpdated(true);
        return true;
    }
    /**
     * Loescht ein Buch mit gleichem Titel aus Kategorie, falls letztere vorhanden ist
     * @param book
     * @param category
     * @return true, gdw Buchmenge der Kategorie veraendert worden ist
     * @throws IllegalArgumentException
     */
    public boolean remBook(Book book, String category) throws IllegalArgumentException{
        if(!isCategory(category)){
            throw new IllegalArgumentException("Kategorie existiert nicht");
        }
        for(Book book2 : getBooks(category)){
            if(book.getTitle().equalsIgnoreCase(book2.getTitle())){
                getCatLists().get(category).remove(book2);
                setUpdated(true);
                return true;
            }
        }
        return false;
    }
       
    /**
     * Loescht die aktuelle Datenbank
     */
    public void clear(){
        catLists.clear();
        setUpdated(true);
    }

    /**
     * Liest Inhalt des von path spezifizierten Files in die Datenbank ein
     * File muss folgendes Format haben:
     * 
     *<lastUpdated>10.08.2016</lastUpdated>
     *<category name="Gelesen">
     *    <title>Harry Potter - Magic Beasts and where to find them</title>   <author>J. K. Rowling</author>  <date>2014</date>
     *</category>
     *<category name="Begonnen">
     *    <title>Das große ABC</title>    <author>Deine Mudda</author>    <date>2014</date>
     *</category>
     *
     * @param path
     * @throws IOException
     */
    public void readFromFile() throws IOException{
        this.clear();
        
        //Lies lastUpdated ein
        Scanner scanner = new Scanner(new FileReader(path));
        try {
            scanner.findWithinHorizon("<\\s*lastUpdated\\s*>\\s*(.+)\\s*<\\s*/lastUpdated\\s*>", 0);
            SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            setLastUpdated(df.parse(scanner.match().group(1)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        //Lies Kategorien und ihre Buecher ein
        scanner = new Scanner(new FileReader(path));
        Pattern ptnCategoryStart = Pattern.compile("(?i)<\\s*category\\s*name=\\s*\"(.+)\"\\s*>");
        Pattern ptnCategoryEnd = Pattern.compile("<\\s*/category\\s*>");
        Pattern ptnTitle = Pattern.compile("(?i)<\\s*title\\s*>(.+)<\\s*/title\\s*>");
        Pattern ptnAuthor = Pattern.compile("(?i)<\\s*autho\\s*r>(.+)<\\s*/author\\s*>");
        Pattern ptnDate = Pattern.compile("(?i)<\\s*date\\s*>(.+)<\\s*/date\\s*>");
        
        //finde naechste Kategorie und fuege sie der Liste hinzu
        while(scanner.hasNext()){
            scanner.findWithinHorizon(ptnCategoryStart, 0);
            String actCategory = scanner.match().group(1);
            catLists.put(actCategory, new TreeSet<Book>());
            scanner.nextLine();

            
            //Lies Buecher dieser Kategorie ein
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                if(ptnCategoryEnd.matcher(line).find()){
                    break;
                }
                //Metadaten des Buches einlesen
                Matcher m = ptnTitle.matcher(line);
                m.find();
                Book newBook = new Book(m.group(1));

                m = ptnAuthor.matcher(line);
                if(m.find()){
                    newBook.setAuthor(m.group(1));
                }
                m = ptnDate.matcher(line);
                if(m.find()){
                    newBook.setDate(m.group(1));
                }

                //Buch in die Kategorieliste aufnehmen
                catLists.get(actCategory).add(newBook);
            }
        }
        scanner.close();
        setUpdated(false);
    }
    /**
     * Schreibt Datenbank in das von path spezifizierte File
     * @throws IOException 
     */
    public void writeToFile() throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(path, false));
        
        //schreibe lastUpdated
        if(isUpdated()){
            lastUpdated = new Date();
        }

        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        writer.write("<lastUpdated>" + 
                df.format(lastUpdated)
                + "</lastUpdated>");
        writer.newLine();
        
        //schreibe Kategorien und ihre Buecher
        for(String actCategory : getCategories()){
            writer.write("<category name=\"" + actCategory + "\">");
            writer.newLine();
            
            for(Book book : catLists.get(actCategory)){
                writer.write("\t" + book.toString());
                writer.newLine();
            }
            
            writer.write("</category>");
            writer.newLine();
        }
        writer.close();
        setUpdated(false);
    }
    
    /**
     * Gibt eine Map mit allen Buechern aus, die title im Titel enthalten, gruppiert nach Kategorie
     */
    public Map<String, Set<Book>> searchBooksByTitle(String title){
        title = title.toUpperCase();
        Map<String, Set<Book>> bookMap = new HashMap<>();
        for(String category : getCategories()){
            bookMap.put(category, new TreeSet<Book>());
            for(Book book : getBooks(category)){
                if(book.getTitle().toUpperCase().contains(title)){
                    bookMap.get(category).add(book);
                }
            }
        }
        return bookMap;
    }
    /**
     * Gibt eine Map mit allen Buechern aus, die author im Autor enthalten, gruppiert nach Kategorie
     */
    public Map<String, Set<Book>> searchBooksByAuthor(String author){
        author = author.toUpperCase();
        Map<String, Set<Book>> bookMap = new HashMap<>();
        for(String category : getCategories()){
            bookMap.put(category, new TreeSet<Book>());
            for(Book book : getBooks(category)){
                if(book.getAuthor() != null && book.getAuthor().toUpperCase().contains(author)){
                    bookMap.get(category).add(book);
                }
            }
        }
        return bookMap;
    }
    /**
     * Gibt eine Map mit allen Buechern aus, die date im Datum enthalten, gruppiert nach Kategorie
     */
    public Map<String, Set<Book>> searchBooksByDate(String date){
        Map<String, Set<Book>> bookMap = new HashMap<>();
        for(String category : getCategories()){
            bookMap.put(category, new TreeSet<Book>());
            for(Book book : getBooks(category)){
                if(book.getDate() == date){
                    bookMap.get(category).add(book);
                }
            }
        }
        return bookMap;
    }
    
    /**
     * Formatiert uebergebenen String in Datumstring, falls urspruengliches Format gueltig ist
     * Gueltig sind folgende Formate: "   "(leeres Datum), "2016", "April 2016", "20. April 2016"
     * @param date
     *          = der zu pruefende/formatierende String
     * @return formattierter Datumstring
     * @throws IllegalArgumentException, falls uebergebener String keinem Format entspricht
     */
    public String formatDate(String date) throws IllegalArgumentException{
        boolean format0Accepted = true;
        boolean format1Accepted = true;
        boolean format2Accepted = true;
        boolean format3Accepted = true;
        boolean format4Accepted = true;
        
        String strDate = "";
        
        //Format 0: "" (beliebig viele Leerzeichen, sonst nichts
        if(date.matches("\\s*")){
            System.out.println("Format1 akzeptiert");
            strDate = "";
        }
        else{
            format0Accepted = false;
        }
        
        //Format 1: "2016"
        try{
            Scanner scanner = new Scanner(date);
            scanner.findWithinHorizon("\\s*(\\d{4})\\s*", 0);

            strDate = scanner.match().group(1);
            scanner.close();
        }
        catch(Exception pe){
            format1Accepted = false;
        }
        
        //Format 2: "April 2016"
        try{
            Scanner scanner = new Scanner(date);
            scanner.findWithinHorizon("([a-zA-ZäÄ]+)\\s*(\\d{4})", 0);

            switch (scanner.match().group(1).toUpperCase()){
                case "JANUAR":{
                    break;
                }
                case "FEBRUAR":{
                    break;
                }
                case "MÄRZ":{
                    break;
                }
                case "APRIL":{
                    break;
                }
                case "MAI":{
                    break;
                }
                case "JUNI":{
                    break;
                }
                case "JULI":{
                    break;
                }
                case "AUGUST":{
                    break;
                }
                case "SEPTEMBER":{
                    break;
                }
                case "OKTOBER":{
                    break;
                }
                case "NOVEMBER":{
                    break;
                }
                case "DEZEMBER":{
                    break;
                }
                default:{
                    //Ueberspringe das Formatieren des strDate
                    scanner.close();
                    throw new Exception("Format 2 ungültig bei Datum der Bucheingabe"); 
                }
            }
            strDate = scanner.match().group(1).substring(0, 1).toUpperCase() 
                    + scanner.match().group(1).substring(1).toLowerCase()
                    + " "
                    + scanner.match().group(2);
            scanner.close();
            
        }
        catch(Exception pe){
            format2Accepted = false;
        }
        
        //Format 3: "20. April 2016"
        try{
            Scanner scanner = new Scanner(date);
            scanner.findWithinHorizon("(\\d+)\\.*\\s*([a-zA-ZäÄ]+)\\s*(\\d{4})", 0);
            int monthDays = 0;
            switch (scanner.match().group(2).toUpperCase()){
                case "JANUAR":{
                    monthDays = 31;
                    break;
                }
                case "FEBRUAR":{
                    monthDays = 29;
                    break;
                }
                case "MÄRZ":{
                    monthDays = 31;
                    break;
                }
                case "APRIL":{
                    monthDays = 30;
                    break;
                }
                case "MAI":{
                    monthDays = 31;
                    break;
                }
                case "JUNI":{
                    monthDays = 30;
                    break;
                }
                case "JULI":{
                    monthDays = 31;
                    break;
                }
                case "AUGUST":{
                    monthDays = 31;
                    break;
                }
                case "SEPTEMBER":{
                    monthDays = 30;
                    break;
                }
                case "OKTOBER":{
                    monthDays = 31;
                    break;
                }
                case "NOVEMBER":{
                    monthDays = 30;
                    break;
                }
                case "DEZEMBER":{
                    monthDays = 31;
                    break;
                }
                default:{
                    //Ueberspringe das Formatieren des strDate
                    scanner.close();
                    throw new Exception("Format 3 ungültig bei Datum der Bucheingabe"); 
                }
            }
                
            if(Integer.parseInt(scanner.match().group(1)) > monthDays){
                //Ueberspringe das Formatieren des strDate
                scanner.close();
                throw new Exception("Format 3 ungültig bei Datum der Bucheingabe"); 
            }
            strDate = scanner.match().group(1)
                    + ". "
                    + scanner.match().group(2).substring(0, 1).toUpperCase() 
                    + scanner.match().group(2).substring(1).toLowerCase()
                    + " "
                    + scanner.match().group(3);

            scanner.close();
        }
        catch(Exception pe){
            format3Accepted = false;
        }
        
        //Format 3: "20. 04. 2016"
        try{
            Scanner scanner = new Scanner(date);
            scanner.findWithinHorizon("(\\d+)[\\.|\\s]\\.*\\s*(\\d+)[\\.|\\s]\\.*\\s*(\\d{4})", 0);
            int monthDays = 0;
            int month = Integer.parseInt(scanner.match().group(2));
            String strMonth = "";
            
            if(month > 12 || month < 1){
              //Ueberspringe das Formatieren des strDate
                scanner.close();
              throw new Exception("Format 4 ungültig bei Datum der Bucheingabe");
            }
            
            if(month <= 6){
                if(month == 1){
                    monthDays = 31;
                    strMonth = "Januar";
                }
                else if(month == 2){
                    monthDays = 29;
                    strMonth = "Februar";
                }
                else if(month == 3){
                    monthDays = 31;
                    strMonth = "März";
                }
                else if(month == 4){
                    monthDays = 30;
                    strMonth = "April";
                }
                else if(month == 5){
                    monthDays = 31;
                    strMonth = "Mai";
                }
                else if(month == 6){
                    monthDays = 30;
                    strMonth = "Juni";
                }
            }
            else{
                if(month == 7){
                    monthDays = 31;
                    strMonth = "Juli";
                }
                else if(month == 8){
                    monthDays = 31;
                    strMonth = "August";
                }
                else if(month == 9){
                    monthDays = 30;
                    strMonth = "September";
                }
                else if(month == 10){
                    monthDays = 31;
                    strMonth = "Oktober";
                }
                else if(month == 11){
                    monthDays = 30;
                    strMonth = "November";
                }
                else if(month == 12){
                    monthDays = 31;
                    strMonth = "Dezember";
                }
            }

            if(Integer.parseInt(scanner.match().group(1)) > monthDays){
                //Ueberspringe das Formatieren des strDate
                scanner.close();
                throw new Exception("Format 4 ungültig bei Datum der Bucheingabe"); 
            }
            strDate = scanner.match().group(1)
                    + ". "
                    + strMonth
                    + " "
                    + scanner.match().group(3);

            scanner.close();
        }
        catch(Exception pe){
            format4Accepted = false;
        }
        
        if(!format0Accepted && !format1Accepted && !format2Accepted && !format3Accepted && !format4Accepted){
            throw new IllegalArgumentException();
        }
        
        return strDate;
    }
}
