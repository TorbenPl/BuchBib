package database;

public class Book implements Comparable<Book>{
    private String title;
    private String author;
    private String date;
    
    public Book(String title)throws IllegalArgumentException{
        if(title == null || title.isEmpty()){
            throw new IllegalArgumentException("Titel fehlt");
        }
        this.title = title;
    }
    public Book(String title, String author, String date){
        this(title);
        this.author = author;
        this.date = date;
    }
            
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public int compareTo(Book book2) {
        return getTitle().compareTo(book2.getTitle());
    }
    
    @Override
    public String toString(){
        String res = "<title>" + getTitle() + "</title>";
        if(getAuthor() != null && !getAuthor().isEmpty()){
            res = res.concat("\t<author>" + getAuthor() + "</author>");
        }
        if(getDate() != null && !getDate().isEmpty()){
            res = res.concat("\t<date>" + getDate() + "</date>");
        }
        return res;
    }
}
