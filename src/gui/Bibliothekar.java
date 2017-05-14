package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import controller.BibController;
import database.Book;
import database.Database;

public class Bibliothekar extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private JScrollPane scrollPane;
    private Map<String, DefaultTableModel> modelMap;
    private String actCategory;
    private boolean tableUpdated = false; 
    
    private JButton btnBuchHinzufügen;
    private JButton btnSpeichern;
    private JButton btnSuche;
    private JButton btnKategorienVerwalten;
    
    //SingleTons
    private KategorienVerwalten jdManageCategories;
    private BuchSuche bookSearch;
    
    private JPanel buttonPane;
    private ButtonGroup rdbtngrpCategories;
    private Map<String, JRadioButton> rdbtnMap;
    
    private JLabel lblLastUpdated;
    private JLabel lblBuchzahl;
    private JLabel lblVerwaltung;
    private JLabel lblaktKategorieHeader;
    private JLabel lblBuchzahlHeader;
    private JLabel lblLastUpdatedHeader;
    
    private JComboBox<String> cbCategories;
    private JComboBox<String> cbActCategory;
    
    private final String categorySearch = "Suche"; 

//  testComboBoxListenerActive "verhindert" das Ausloesen des ActionListeners der ComboBox
//  wenn auf false gesetzt
    private boolean testComboBoxListenerActive = true;
    private boolean testTableListenerActive = true;
    
    private Color cBackground = new Color(153, 0, 0);
    private Color cForeground = Color.WHITE;
    private Color cTableBackg = new Color(254, 232, 178);
    private Color cTableForeg = Color.BLACK;
    private JPanel headPane;
    private DefaultTableModel prevModel;
    private BibController controller;



    /**
     * Create the frame.
     */
    public Bibliothekar(BibController controller) {

        this.controller = controller;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(myListenerMyWindowClosing());
        setTitle("Bücherliste");
        setBounds(100, 100, 800, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(5, 5));
        setContentPane(contentPane);
        
        
        //VerwaltungsPanel rechte Seite
        {
            buttonPane = new JPanel();
            contentPane.add(buttonPane, BorderLayout.EAST);
            buttonPane.setLayout(new GridLayout(0, 1, 0, 0));

            //btnSpeichern
            {
                btnSpeichern = new JButton("Speichern");
                btnSpeichern.addActionListener(myListenerSaveDatabase());
                btnSpeichern.setToolTipText("Schreibt Veränderungen in die Datenbank");
            }
            //btnBuchhinzufügen
            {
                btnBuchHinzufügen = new JButton("Buch hinzufügen");        
                btnBuchHinzufügen.addActionListener(myListenerTableAddRow());
            }
            //btnSuche
            {
                btnSuche = new JButton(categorySearch);
                btnSuche.addActionListener(myListenerSearch());
                btnSuche.setToolTipText("Suche in der Datenbank");
            }
            //btnKategorienVerwalten
            {
                btnKategorienVerwalten = new JButton("Kategorien verwalten");
                btnKategorienVerwalten.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        manageCategories();
                    }
                });
            }
            //Diverse Radiobuttons
            {
                lblVerwaltung = new JLabel("Verwaltung");
                lblVerwaltung.setFont(new Font("SansSerif", Font.BOLD, 20));
                lblVerwaltung.setHorizontalAlignment(SwingConstants.CENTER);
                buttonPane.add(lblVerwaltung);

                rdbtngrpCategories = new ButtonGroup();
                rdbtnMap = new HashMap<>();
                cbCategories = new JComboBox<String>();
                cbActCategory = new JComboBox<String>();
                for(String category : controller.getCategories()){
                    cbCategories.addItem(category);
                    cbActCategory.addItem(category);

                    rdbtnMap.put(category, new JRadioButton(category));
                    rdbtnMap.get(category).addActionListener(myListenerSwitchCategories(category));
//                    buttonPane.add(rdbtnMap.get(category));
                    rdbtngrpCategories.add(rdbtnMap.get(category));
                }
            }
            buttonPane.add(btnSpeichern);
            buttonPane.add(btnBuchHinzufügen);
            buttonPane.add(btnSuche);
            buttonPane.add(btnKategorienVerwalten);
        }
        cbActCategory.addItem(categorySearch);
        
        cbActCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(testComboBoxListenerActive){
                    switchCategories(cbActCategory.getSelectedItem().toString());
                }
            }
        });
        
        //Die eigentliche Tabelle
        {
            modelMap = new HashMap<>();

            actCategory = controller.getCategories().iterator().next();
            for(String category : controller.getCategories()){
                createModel(category);
            }
            table = new JTable();
            table.setRowHeight(20);
            table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 
                    table.getTableHeader().getFont().getSize()));

            scrollPane = new JScrollPane(table);
            scrollPane.setBorder(new EtchedBorder());
            contentPane.add(scrollPane, BorderLayout.CENTER);
        }        
        
        //Kopfzeile mit Informationen
        {
            headPane = new JPanel();
            headPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
            lblaktKategorieHeader = new JLabel("aktuelle Kategorie:");

            lblBuchzahlHeader = new JLabel("Buchzahl in Kategorie:");

            lblBuchzahl = new JLabel("");      

            lblLastUpdatedHeader = new JLabel("zuletzt geändert:");

            lblLastUpdated = new JLabel("test");
            setLblLastUpdated();

            contentPane.add(headPane, BorderLayout.NORTH);

            GroupLayout gl_headPane = new GroupLayout(headPane);
            gl_headPane.setHorizontalGroup(
                    gl_headPane.createParallelGroup(Alignment.LEADING)
                    .addGroup(Alignment.LEADING, gl_headPane.createSequentialGroup()
                            .addGap(10)
                            .addComponent(lblaktKategorieHeader)
                            .addGap(10)
                            .addComponent(cbActCategory, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                            .addGap(50)
                            .addComponent(lblBuchzahlHeader)
                            .addGap(10)
                            .addComponent(lblBuchzahl)
                            .addGap(50)
                            .addComponent(lblLastUpdatedHeader)
                            .addGap(10)
                            .addComponent(lblLastUpdated)
                            )
                    );
            gl_headPane.setVerticalGroup(
                    gl_headPane.createParallelGroup(Alignment.CENTER)
                    .addGroup(gl_headPane.createParallelGroup(Alignment.CENTER)
                            .addComponent(cbActCategory)
                            .addComponent(lblBuchzahl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                            .addComponent(lblaktKategorieHeader, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblBuchzahlHeader, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblLastUpdatedHeader)
                            .addComponent(lblLastUpdated)
                            )
                    );
            headPane.setLayout(gl_headPane);
        }
        
        //Colorierung
        {
            setColorBackground(cBackground);
            setColorForeground(cForeground);
        }
        
        switchCategories(actCategory);
        
    }
    
    /**
     * Erzeugt TableModel zu Inhalten der Kategorie aus Database und speichert Model in Map
     * @param category
     */
    private void createModel(String category){
        String[] columnNames = {
                "Titel",
                "Autor",
                "Datum"
                };
        String[][] data = new String[controller.getBooks(category).size()][3];
        int i = 0;
        for(Book book : controller.getBooks(category)){
            data[i][0] = book.getTitle();
            data[i][1] = book.getAuthor();
            data[i][2] = book.getDate();
            i++;
        }
    DefaultTableModel model = new DefaultTableModel(data, columnNames);
        
        model.addTableModelListener(myListenerTableUpdated());
        modelMap.put(category, model);
    }
    private void removeModel(String category){
        modelMap.remove(category);
    }
    
    
    private ActionListener myListenerSaveDatabase(){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDatabase();
                try {
                    if(controller.isUpdated()){
                        controller.saveDatabase();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        };
    }
    
    /**
     * Schreibt den Inhalt aller TableModel in die Datenbank, falls Aenderungen vorliegen
     */
    //TODO: Auslagern in den Controller
    public void updateDatabase(){
        if(isTableUpdated()){
            modelMap.forEach((category, model) ->{     
                controller.clearCategory(category);
                for(int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++){
                    Book newBook;
                    if(model.getValueAt(rowIndex, 0) != null && !model.getValueAt(rowIndex, 0).toString().isEmpty()){
                        newBook = new Book((String) model.getValueAt(rowIndex, 0));

                        if(model.getValueAt(rowIndex, 1) != null && !model.getValueAt(rowIndex, 1).toString().isEmpty()){
                            newBook.setAuthor((String) model.getValueAt(rowIndex, 1));
                        }
                        if(model.getValueAt(rowIndex, 2) != null && !model.getValueAt(rowIndex, 2).toString().isEmpty()){
                            boolean isParsable = false;
                            while(!isParsable)
                                try{
                                    String formattedDate = controller.formatDate((String) model.getValueAt(rowIndex, 2));
                                    newBook.setDate(formattedDate);
                                    
                                    //aktualisiere Datum im TableModel auf formattierte Variante
                                    testTableListenerActive = false;
                                    model.setValueAt(
                                            formattedDate,
                                            rowIndex,
                                            2
                                            );
                                    SwingUtilities.invokeLater(()-> testTableListenerActive = true);
                                    
                                    isParsable = true;
                                }catch(Exception e){
                                    
                                    testTableListenerActive = false;
                                    model.setValueAt(
                                            JOptionPane.showInputDialog("Fehler bei Datum von Titel \"" + model.getValueAt(rowIndex, 0) + "\"\n Bitte neues Datum eingeben",
                                                    model.getValueAt(rowIndex, 2)), 
                                                    rowIndex, 2); 
                                    SwingUtilities.invokeLater(()-> testTableListenerActive = true);
                                    
                                }
                        }
                        controller.addBook(newBook, category);
                    }
                    else{
                        model.removeRow(rowIndex);
                        rowIndex--;
                    }
                }
            });
            setTableUpdated(false);
        }
    }
    /**
     * Schreibt den Inhalt der Datenbank in das File
     * @throws IOException
     */
    private void saveDatabase() throws IOException{
        controller.saveDatabase();
        setLblLastUpdated();
    }

    
    /**
     * Aendert das TableModel im Table zu dem tableModel von category
     * @param category
     * @return
     */
    private ActionListener myListenerSwitchCategories(String category){
        return new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                switchCategories(category);
            }
        };
    }
    /**
     * Aendert das TableModel im Table zu dem tableModel von category
     * @param category
     * @return
     */
    private void switchCategories(String category){
        if(category.equalsIgnoreCase(categorySearch)){
            search();
        }
        else{
            if(!modelMap.containsKey(category)){
                createModel(category);
            }
            actCategory = category;
            setTableModel(modelMap.get(actCategory), actCategory);

            testComboBoxListenerActive = false;
            cbActCategory.setSelectedItem(actCategory);
            SwingUtilities.invokeLater(()-> testComboBoxListenerActive = true);
        }
    }
    
    /**
     * Zeigt das uebergebene TableModel an und passt die Kopfzeile an
     */
    private void setTableModel(TableModel model, String category){
        table.setModel(model);
        lblBuchzahl.setText(Integer.toString(model.getRowCount()));
        setTableColumnWidths(category);
        
        testComboBoxListenerActive = false;
        cbActCategory.setSelectedItem(category);
        SwingUtilities.invokeLater(() -> testComboBoxListenerActive = true);
    }
    /**
     * Setzt die Spaltenweiten so, wie es der Kategorie entspricht
     */
    private void setTableColumnWidths(String category){
        if(category.equalsIgnoreCase(categorySearch)){
            table.getColumnModel().getColumn(0).setPreferredWidth(150);
            table.getColumnModel().getColumn(0).setMinWidth(100);
            table.getColumnModel().getColumn(1).setPreferredWidth(300);
            table.getColumnModel().getColumn(1).setMinWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(200);
            table.getColumnModel().getColumn(2).setMinWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(50);
            table.getColumnModel().getColumn(2).setMinWidth(35);
        }
        else{
            table.getColumnModel().getColumn(0).setPreferredWidth(300);
            table.getColumnModel().getColumn(0).setMinWidth(100);
            table.getColumnModel().getColumn(1).setPreferredWidth(150);
            table.getColumnModel().getColumn(1).setMinWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(100);
            table.getColumnModel().getColumn(2).setMinWidth(35);
        }
    }
    
    /**
     * Fuegt dem aktuellen TableModel eine Zeile hinzu
     * @return
     */
    private ActionListener myListenerTableAddRow(){
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableAddRow();
            }
        };
    }    
    /**
     * Fuegt dem aktuellen TableModel eine Zeile hinzu, Inhalt wird aus JDialog gelesen
     * @return
     */
    private void tableAddRow(){
        BuchEingabe bucheingabe = new BuchEingabe(this);
        bucheingabe.setLocation(btnBuchHinzufügen.getLocationOnScreen());
        
        bucheingabe.setColorBackground(cBackground);
        bucheingabe.setColorForeground(cForeground);
        
        bucheingabe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        bucheingabe.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                if(bucheingabe.isBookAccepted()){
                    String title = bucheingabe.getTitle();
                    if(title == null || title.isEmpty()){
                        JOptionPane.showMessageDialog(bucheingabe, "Der Titel fehlt");
                        return;
                    }
                    else{
                        String strDate = "";
                        if(!bucheingabe.getDate().toString().isEmpty()){
                            try{
                                strDate = controller.formatDate(bucheingabe.getDate());
                            }
                            catch(IllegalArgumentException ie){
                                JOptionPane.showMessageDialog(bucheingabe, "Das Datum ist ungültig");
                                return;
                            }
                        }
                        for(Book book : controller.getBooks(bucheingabe.getCategory())){
                            if(book.getTitle().equalsIgnoreCase(bucheingabe.getTitle())){
                                JOptionPane.showMessageDialog(bucheingabe, "Der Titel ist bereits in der Kategorie aufgeführt");
                                return;
                            }
                        }
                        modelMap.get(bucheingabe.getCategory()).addRow(new String[]{
                                bucheingabe.getTitle(),
                                bucheingabe.getAuthor(),
                                strDate,
                        });
                    }
                }
                bucheingabe.setVisible(false);
                bucheingabe.dispose();
                updateDatabase();
            }
        });
        
        bucheingabe.setVisible(true);            
    }
    
    /**
     * Sortiert TableModel lexigraphisch nach bestimmter Spalte
     * @return
     */
    private ActionListener myListenerSortByColumn(){
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sortByColumn();
            }
        };
    }
    /**
     * Sortiert TableModel lexigraphisch nach bestimmter Spalte
     * @return
     */
    private void sortByColumn(){
        //TODO Nach Autor, Datum, etc sortieren einbauen
    }
    
    /**
     * Fragt vor dem Schließen, ob Inhalt in File gespeichert werden soll
     * @return WindowListener, der als DEfaultCLosingOperation genutzt werden kann
     */
    private WindowListener myListenerMyWindowClosing(){
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MyWindowClosing(e);
            }
        };
    }
    /**
     * Fragt vor dem Schließen, ob Inhalt in File gespeichert werden soll
     * @param e
     */
    private void MyWindowClosing(WindowEvent e){
        if(controller.isUpdated()){
            int decision = JOptionPane.showConfirmDialog(this, "Möchtest du vor dem Schließen speichern?");
            if(decision == JOptionPane.OK_OPTION){
                updateDatabase();
                try {
                    saveDatabase();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            else if(decision == JOptionPane.CANCEL_OPTION || decision == JOptionPane.CLOSED_OPTION){
                return;
            }
        }
        setVisible(false);
        dispose();
    }

    /**
     * @return TableModelListener, der die Variable tableUpdated auf true setzt, falls Table geaendert worden ist
     */
    private TableModelListener myListenerTableUpdated(){
        return new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if(testTableListenerActive){
                    setTableUpdated(true);
                    updateDatabase();
                }
            }
        };
    }
    public boolean isTableUpdated() {
        return tableUpdated;
    }
    public void setTableUpdated(boolean tableUpdated) {
        this.tableUpdated = tableUpdated;
    }
    
    private ActionListener myListenerSearch(){
        return new ActionListener(){
            public void actionPerformed(ActionEvent e){
                search();
            }
        };
    }
    /**
     * Sucht interaktiv in Datenbank nach Eintraegen
     * @param title
     */
    private void search(){
        //TODO: Moeglichkeit nur in bestimmter Kategorie zu suchen
        if(bookSearch == null){
        bookSearch = new BuchSuche();
        bookSearch.setLocation(btnSuche.getLocationOnScreen());
        
        bookSearch.setColorBackground(cBackground);
        bookSearch.setColorForeground(cForeground);
        
        prevModel = (DefaultTableModel) table.getModel();

        bookSearch.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        bookSearch.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                if(!bookSearch.isAccepted()){
                    if(prevModel == null){
                        prevModel = modelMap.get(actCategory);
                    }
                    if(prevModel.getColumnCount() > modelMap.get(actCategory).getColumnCount()){
                        setTableModel(prevModel, categorySearch);
                    }
                    else{
                        switchCategories(actCategory);
                    }
                }
                bookSearch.setVisible(false);
                bookSearch.dispose();
                bookSearch = null;
            }
        });
        bookSearch.getTfInput().addKeyListener(new KeyListener(){
            @Override
            public void keyPressed(KeyEvent arg0) {}
            @Override
            public void keyReleased(KeyEvent arg0) {   
                activeSearch(bookSearch);
            }
            @Override
            public void keyTyped(KeyEvent arg0) {}
        });
            
        bookSearch.setVisible(true);
        }
        else{
            bookSearch.requestFocus();
        }
    }
    
    private void activeSearch(BuchSuche bookSearch){
        if(bookSearch.getInput() == null || bookSearch.getInput().isEmpty()){
            return;
        }
        
        Map<String, Set<Book>> setBooks = new HashMap<>();
        switch(bookSearch.getSearchType()){
            case 'T': {
                setBooks = controller.searchBooksByTitle(bookSearch.getInput());
                break;
            }
            case 'A':{
                setBooks = controller.searchBooksByAuthor(bookSearch.getInput());
                break;
            }
            case 'D':{
                try{
                    setBooks = controller.searchBooksByDate(controller.formatDate(bookSearch.getInput()));
                }
                catch(IllegalArgumentException ie) {
                    JOptionPane.showMessageDialog(bookSearch, "Das Datum ist ungültig");
                    return;
                }
                break;
            }
        }
        if(setBooks.isEmpty()){
            System.out.println("LEER");
            bookSearch.setVisible(false);
            bookSearch.dispose();
        }
        String[] columnNames = {"Kategorie", "Titel", "Autor", "Datum"};
        int count = 0;
        Iterator<Set<Book>> it = setBooks.values().iterator();
        while(it.hasNext()){
            count += it.next().size();
        }
        
        //Erstelle TableModel der Suchtreffer und speichere Primary Keys der gefundenen Buecher
        String[][] data = new String[count][columnNames.length];
        String[][] primKeyBackUp = new String[count][2];    //Eintraege: {Kategorie, Titel}
        int i = 0;
        
        for(String category : setBooks.keySet()){
            for(Book book : setBooks.get(category)){
                data[i][0] = category;
                data[i][1] = book.getTitle();
                data[i][2] = book.getAuthor();
                data[i][3] = book.getDate();

                primKeyBackUp[i][0] = category;
                primKeyBackUp[i][1] = book.getTitle();
                i++;
            }
        }
    

        DefaultTableModel searchModel = new DefaultTableModel(data, columnNames);
        setTableModel(searchModel, categorySearch);
        
        //Setzt Inhalte der Spalte Kategorie als ComboBox
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(cbCategories));
        //ActionListener, der Veraenderungen in der Suchansicht in die TableModels uebertraegt
        searchModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if(testTableListenerActive){
                    int titleColumn = searchModel.findColumn("Titel");
                    int authorColumn = searchModel.findColumn("Autor");
                    int dateColumn = searchModel.findColumn("Datum");
                    int categoryColumn = searchModel.findColumn("Kategorie");
                    int changedRow = e.getFirstRow();

                    //Falls Kategorie geaendert wurde
                    if(e.getColumn() == categoryColumn){
                        //loesche das Buch aus der alten Kategorie
                        DefaultTableModel model = modelMap.get(primKeyBackUp[changedRow][0]);
                        for(int i = 0; i < model.getRowCount(); i++){
                            int titleColumnTmp = model.findColumn("Titel");
                            if(primKeyBackUp[changedRow][1].equalsIgnoreCase(model.getValueAt(i, titleColumnTmp).toString())){
                                model.removeRow(i);
                                i--;
                            }
                        }

                        //Fuege es in die neue gewuenschte Kategorie ein
                        String[] newRow = new String[searchModel.getColumnCount() - 1];
                        for(int column = 1; column < searchModel.getColumnCount(); column++){
                            if(searchModel.getValueAt(changedRow, column) != null){
                                newRow[column - 1] = 
                                        searchModel.getValueAt(changedRow, column).toString();
                            }
                        }
                        modelMap.get(
                                searchModel.getValueAt(changedRow, categoryColumn)
                                )
                                .addRow(newRow);
                    }
                    //falls Titel geaendert wurde, suche veraenderten Eintrag anhand primKeyBackUp und mache Aenderung
                    else if(e.getColumn() == titleColumn){
                        DefaultTableModel model = modelMap.get(
                                searchModel.getValueAt(
                                        changedRow, 
                                        categoryColumn)
                                );
                        int titleColumnTmp = model.findColumn("Titel");
                        for(int row = 0; row < model.getRowCount(); row++){
                            if(model.getValueAt(row, titleColumnTmp).toString()
                                    .equalsIgnoreCase(primKeyBackUp[changedRow][1])){
                                model.setValueAt(searchModel.getValueAt(changedRow, titleColumn), row, titleColumnTmp);
                            }
                        }
                        //passe Primary Key an neuen Titel an (da jetzt ueber neuen Titel in TableModels adressierbar)
                        primKeyBackUp[changedRow][1] = searchModel.getValueAt(changedRow, titleColumn).toString();
                    }
                    //Falls Datum oder Author veraendert wurde, suche Titel in Kategorie und mache Aenderung
                    else{
                        DefaultTableModel model = modelMap.get(
                                searchModel.getValueAt(
                                        changedRow, 
                                        categoryColumn)
                                );
                        int titleColumnTmp = model.findColumn("Titel");
                        int authorColumnTmp = model.findColumn("Autor");
                        int dateColumnTmp = model.findColumn("Datum");
                        for(int row = 0; row < model.getRowCount(); row++){
                            if(model.getValueAt(row, titleColumnTmp).toString()
                                    .equalsIgnoreCase(primKeyBackUp[changedRow][1])){
                                model.setValueAt(
                                        searchModel.getValueAt(changedRow, dateColumn), 
                                        row, dateColumnTmp);
                                model.setValueAt(
                                        searchModel.getValueAt(changedRow, authorColumn), 
                                        row, authorColumnTmp);
                                //passe Datum im searchModel an
                                testTableListenerActive = false;
                                String formattedDate = controller.formatDate((String) searchModel.getValueAt(changedRow, dateColumn));
                                searchModel.setValueAt(formattedDate, changedRow, dateColumn);
                                SwingUtilities.invokeLater(()-> testTableListenerActive = true);
                                
                                break;
                            }
                        }
                    }
                    updateDatabase();
                }
            }
        });
    }

    public String getActCategory(){
        return actCategory;
    }
    
    private void setLblLastUpdated(){
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        lblLastUpdated.setText(df.format(controller.getLastUpdated()));
    }
    
    
    private boolean addCategory(String newCategory){
        if(newCategory == null || newCategory.isEmpty()){
            JOptionPane.showMessageDialog(null, "Kategorie muss einen Namen haben");
            return false;
        }
        if(controller.getCategories().contains(newCategory)){
            JOptionPane.showMessageDialog(null, "Kategorie existiert bereits");
            return false;
        }
        controller.addCategory(newCategory);
        createModel(newCategory);
        
        //Füege neue Kategorie in ComboBox ein, aber lass "Suche" als unterste Kategorie
        testComboBoxListenerActive = false;
        cbActCategory.insertItemAt(newCategory, cbActCategory.getItemCount() - 1);
        SwingUtilities.invokeLater(()-> testComboBoxListenerActive = true);
        
        return true;
    }       
    
    private boolean removeCategory(String category){
        if(category == null || category.isEmpty()){
            JOptionPane.showMessageDialog(null, "Kategorie muss einen Namen haben");
            return false;
        }
        if(controller.getCategories().contains(category)){
            controller.removeCategory(category);
            
            testComboBoxListenerActive = false;
            cbActCategory.removeItem(category);
            SwingUtilities.invokeLater(()-> testComboBoxListenerActive = true);
            
            if(category == actCategory){
                switchCategories(controller.getCategories().iterator().next());
            }
            removeModel(category);
            return true;
        }
        else{
            JOptionPane.showMessageDialog(null, "Kategorie existiert nicht");
            return false;
        }
    }
    
    private boolean renameCategory(String oldCategory, String newCategory){
        if(newCategory == null || newCategory.isEmpty() || oldCategory == null || oldCategory.isEmpty()){
            JOptionPane.showMessageDialog(null, "Kategorie muss einen Namen haben");
            return false;
        }
        if(!controller.renameCategory(oldCategory, newCategory)){
            JOptionPane.showMessageDialog(null, "Kategorie ungültig");
            return false;
        }
        else{
            testComboBoxListenerActive = false;
            cbActCategory.removeItem(oldCategory);
            cbActCategory.addItem(newCategory);
            SwingUtilities.invokeLater(()-> testComboBoxListenerActive = true);
            
            modelMap.put(newCategory, modelMap.get(oldCategory));
            modelMap.remove(oldCategory);
            
            if(oldCategory == actCategory){
                switchCategories(newCategory);
            }
            return true;
        }
    }
    
    private void manageCategories(){
        if(jdManageCategories == null){
            jdManageCategories = new KategorienVerwalten(controller.getCategories());
            jdManageCategories.setLocation(btnKategorienVerwalten.getLocationOnScreen());

            jdManageCategories.setColorBackground(cBackground);
            jdManageCategories.setColorForeground(cForeground);
            
            jdManageCategories.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            jdManageCategories.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e){
                    if(jdManageCategories.isAccepted()){
                        switch(jdManageCategories.getManageType()){
                        
                        case "Hinzufügen":{
                            //falls Kategorie nicht hinzugefügt werden kann, lasse Fenster KategorienVerwalten offen
                            if(!addCategory(jdManageCategories.getTextField())){
                                return;
                            }
                            break;
                        }
                        
                        case "Entfernen":{
                            //falls Kategorie nicht entfernt wurde, lasse Fenster KategorienVerwalten offen
                            if(!removeCategory(jdManageCategories.getComboBox())){
                                return;
                            }
                            break;
                        }
                        
                        case "Umbenennen":{
                            //falls Kategorie nicht umbenannt wurde, lasse Fenster KategorienVerwalten offen
                            if(!renameCategory(jdManageCategories.getComboBox(), jdManageCategories.getTextField())){
                                return;
                            }
                            break;
                        }
                        
                        default:{
                            throw new RuntimeException("Unbekanntes Statement bei KategorienVerwalten");
                        }
                        }

                    }
                    jdManageCategories.setVisible(false);
                    jdManageCategories.dispose();
                    jdManageCategories = null;
                }
            });
            
            jdManageCategories.setVisible(true);
        }
        else{
            jdManageCategories.requestFocus();
        }
    }
    
    
    public void setColorForeground(Color color){
        cForeground = color;
        lblaktKategorieHeader.setForeground(color);
        lblBuchzahlHeader.setForeground(color);
        lblLastUpdatedHeader.setForeground(color);
        lblBuchzahl.setForeground(color);
        lblLastUpdated.setForeground(color);
        lblVerwaltung.setForeground(color);
        
        table.getTableHeader().setForeground(cTableForeg);
        table.setForeground(cTableForeg);
    }
    
    public void setColorBackground(Color color){
        cBackground = color;
        scrollPane.setBackground(color);
        scrollPane.getViewport().setBackground(color);
        scrollPane.getVerticalScrollBar().setBackground(color);
        
        table.setBackground(cTableBackg);
        table.getTableHeader().setBackground(cTableBackg);
        
        headPane.setBackground(color);

        buttonPane.setBackground(color);
        contentPane.setBackground(color);
        
        btnBuchHinzufügen.setBackground(cTableBackg);
        btnKategorienVerwalten.setBackground(cTableBackg);
        btnSpeichern.setBackground(cTableBackg);
        btnSuche.setBackground(cTableBackg);
        cbActCategory.setBackground(cTableBackg);
    }
    
    public BibController getController() {
        return controller;
    }

    public void setController(BibController controller) {
        this.controller = controller;
    }
    
}
