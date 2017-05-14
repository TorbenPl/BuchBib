package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

public class BuchEingabe extends JDialog {

    private final JPanel contentPanel = new JPanel();

    private JTextField tfTitel;
    private JTextField tfAutor;
    private JTextField tfDatum;
    private JComboBox<String> cbCategories;
    
    private boolean isBookAccepted = false;

    private JLabel lblKategorie;

    private JLabel lblTitel;

    private JLabel lblAutor;

    private JLabel lblDatum;

    private JPanel buttonPane;

    /**
     * Create the dialog.
     */
    public BuchEingabe(Bibliothekar bib) {
        setBounds(100, 100, 411, 247);
        setTitle("Bucheingabe");
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        lblTitel = new JLabel("Titel");
        lblAutor = new JLabel("Autor");
        lblDatum = new JLabel("Datum");

        tfTitel = new JTextField();
        tfTitel.setToolTipText("Titel eingeben");
        tfAutor = new JTextField();
        tfAutor.setToolTipText("Autor eingeben");
        tfDatum = new JTextField();
        tfDatum.setToolTipText("Datum eingeben");
        tfDatum.setToolTipText("Datum der Form \"2016\", \"April 2016\" oder \"20. April 2016\"");
        
        lblKategorie = new JLabel("Kategorie");
        
        cbCategories = new JComboBox<String>();
        for(String category : bib.getController().getCategories()){
            cbCategories.addItem(category);
        }
        cbCategories.setSelectedItem(bib.getActCategory());
        
        GroupLayout gl_contentPane = new GroupLayout(contentPanel);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
                                .addComponent(lblTitel)
                                .addComponent(lblDatum)
                                .addComponent(lblAutor))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                .addComponent(tfTitel, 333, 333, 333)
                                .addComponent(tfAutor, 333, 333, 333)
                                .addComponent(tfDatum, 333, 333, 333)))
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addComponent(lblKategorie)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(cbCategories, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addGap(1)
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblTitel, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addComponent(tfTitel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblAutor, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addComponent(tfAutor, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblDatum, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addComponent(tfDatum, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblKategorie)
                        .addComponent(cbCategories, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(22))
        );
        contentPanel.setLayout(gl_contentPane);
        {
            buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setActionCommand("OK");
                okButton.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        setBookAccepted(true);
                        exit();
                        setBookAccepted(false);
                    }
                });
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                cancelButton.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        exit();
                    }
                });
                buttonPane.add(cancelButton);
            }
        }
    }

    public String getTitle(){
        return tfTitel.getText();
    }
    public String getAuthor(){
        return tfAutor.getText();
    }
    public String getDate(){
        return tfDatum.getText();
    }

    public boolean isBookAccepted() {
        return isBookAccepted;
    }

    public void setBookAccepted(boolean isBookAccepted) {
        this.isBookAccepted = isBookAccepted;
    }
    
    public String getCategory(){
        return cbCategories.getSelectedItem().toString();
    }
    
    private void exit(){
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
    
    public void setColorForeground(Color color){
        contentPanel.setForeground(color);
        buttonPane.setForeground(color);
        
        lblAutor.setForeground(color);
        lblDatum.setForeground(color);
        lblKategorie.setForeground(color);
        lblTitel.setForeground(color);
        
    }
    public void setColorBackground(Color color){
        contentPanel.setBackground(color);
        buttonPane.setBackground(color);
    }
}
