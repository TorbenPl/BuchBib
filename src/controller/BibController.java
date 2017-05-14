package controller;

import gui.Bibliothekar;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import database.Book;
import database.Database;
import database.DatabaseInterface;

public class BibController implements DatabaseInterface{
    private Database database;
    
    
    /**
     * Setzt die dem Controller bekannte Datenbank auf database
     * @param database
     *              = neue Datenbank
     */
    public void setDatabase(Database database){
        this.database = database;
    }    
    /**
     * Gibt die zugrundeliegende Datenbank zurück
     * @return  Die zugrundeliegende Datenbank
     */
    public Database getDatabase(){
        return database;
    }
    /**
     * Aktualisiert die Datenbank gemäß der Änderungen im gui
     */
    public void updateDatabase(){
        //TODO: Ausarbeiten. Solange nicht getan, updateDatabase in gui selbst implementieren
    }
    /**
     * Schreibt den Inhalt der Datenbank in das File
     * @throws IOException
     */
    public void saveDatabase() throws IOException{
        database.writeToFile();
    }
    
    
    //------------------------------------- Weitergeleitete Methoden der Klasse Database --------------------------
    @Override
    public Set<String> getCategories() {
        return database.getCategories();
    }
    @Override
    public boolean addCategory(String category) {
        return database.addCategory(category);
    }
    @Override
    public boolean removeCategory(String category) {
        return database.removeCategory(category);
    }
    @Override
    public Map<String, Set<Book>> getCatLists() {
        return database.getCatLists();
    }
    @Override
    public void setCatLists(Map<String, Set<Book>> catLists) {
        database.setCatLists(catLists);        
    }
    @Override
    public boolean isCategory(String category) {
        return database.isCategory(category);
    }
    @Override
    public boolean renameCategory(String oldCategory, String newCategory) {
        return database.renameCategory(oldCategory, newCategory);
    }
    @Override
    public void clearCategory(String category) {
        database.clearCategory(category);
    }
    @Override
    public boolean isUpdated() {
        return database.isUpdated();
    }
    @Override
    public void setUpdated(boolean isUpdated) {
        database.setUpdated(isUpdated);
    }
    @Override
    public Date getLastUpdated() {
        return database.getLastUpdated();
    }
    @Override
    public void setLastUpdated(Date lastUpdated) {
        database.setLastUpdated(lastUpdated);
    }
    @Override
    public String getPath() {
        return database.getPath();
    }
    @Override
    public void setPath(String path) {
        database.setPath(path);        
    }
    @Override
    public Set<Book> getBooks(String category) throws IllegalArgumentException {
        return database.getBooks(category);
    }
    @Override
    public Set<Book> getAllBooks() {
        return database.getAllBooks();
    }
    @Override
    public boolean containsBook(String title) {
        return database.containsBook(title);
    }
    @Override
    public boolean addBook(Book book, String category)
            throws IllegalArgumentException {
        return database.addBook(book, category);
    }
    @Override
    public boolean remBook(Book book, String category)
            throws IllegalArgumentException {
        return database.remBook(book, category);
    }
    @Override
    public void clear() {
        database.clear();        
    }
    @Override
    public void readFromFile() throws IOException {
        database.readFromFile();
    }
    @Override
    public void writeToFile() throws IOException {
        database.readFromFile();
    }
    @Override
    public Map<String, Set<Book>> searchBooksByTitle(String title) {
        return database.searchBooksByTitle(title);
    }
    @Override
    public Map<String, Set<Book>> searchBooksByAuthor(String author) {
        return database.searchBooksByAuthor(author);
    }
    @Override
    public Map<String, Set<Book>> searchBooksByDate(String date) {
        return database.searchBooksByDate(date);
    }
    @Override
    public String formatDate(String date) throws IllegalArgumentException {
        return database.formatDate(date);
    }
    
    //------------------------------------- ENDE: Weitergeleitete Methoden der Klasse Database --------------------------
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.GERMAN);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    BibController controller = new BibController();
                    try {
                        controller.setDatabase(new Database("Buchbib.dat"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bibliothekar myGui = new Bibliothekar(controller);
//                    myGui.setController(controller);
//                    frame.setBackground(new Color(156, 247, 8));
                    myGui.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }    
}
