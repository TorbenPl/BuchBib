package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class KategorienVerwalten extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JPanel jpInputPanel;

    private JLabel lblAlteKategorie;
    private JComboBox<String> cbType;

    private JLabel lblNeueKategorie;
    private JTextField tfInput;
    
    private JLabel lblAktion;
    private JComboBox<String> cbInput;

    private final String strHinzufuegen = "Hinzufügen";
    private final String strEntfernen = "Entfernen";
    private final String strUmbenennen = "Umbenennen";
    
    private boolean testActionListenerActive = true;
    private boolean isAccepted = false;
    private Color cBackground = Color.BLACK;
    private Color cForeground = Color.WHITE;
    private JPanel buttonPane;


    /**
     * Create the dialog.
     */
    public KategorienVerwalten(Set<String> categories){
        setBounds(100, 100, 330, 180);
        setTitle("Kategorien verwalten");
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        
        //dynamisches InputPanel
        {
            lblNeueKategorie = new JLabel("neue Kategorie");
            tfInput = new JTextField();
            
            lblAlteKategorie = new JLabel("alte Kategorie");
            cbInput = new JComboBox<String>();
            for(String category : categories){
                cbInput.addItem(category);
            }
            
            jpInputPanel = new JPanel();        
            
            GroupLayout gl_jpInputPanel = new GroupLayout(jpInputPanel);
            gl_jpInputPanel.setHorizontalGroup(
                    gl_jpInputPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_jpInputPanel.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(cbInput, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                            .addGap(10)
                            .addComponent(tfInput, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                            )
                    .addGroup(gl_jpInputPanel.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(lblAlteKategorie, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                            .addGap(10)
                            .addComponent(lblNeueKategorie, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                            )
                    );
            
            gl_jpInputPanel.setVerticalGroup(
                    gl_jpInputPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_jpInputPanel.createSequentialGroup()
                            .addGroup(gl_jpInputPanel.createParallelGroup(Alignment.LEADING)
                                    .addGroup(gl_jpInputPanel.createSequentialGroup()
                                            .addComponent(lblNeueKategorie, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                            )
                                    .addGroup(gl_jpInputPanel.createSequentialGroup()
                                            .addComponent(lblAlteKategorie, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                            ))
                            .addGroup(gl_jpInputPanel.createParallelGroup(Alignment.LEADING)
                                    .addGroup(gl_jpInputPanel.createSequentialGroup()
                                            .addComponent(tfInput, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                            )
                                    .addGroup(gl_jpInputPanel.createSequentialGroup()
                                            .addComponent(cbInput, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                            ))
                            )
                    );
            
            jpInputPanel.setLayout(gl_jpInputPanel);
        }
        
        //ComboBox zur Auswahl der Aktion
        {
            lblAktion = new JLabel("Aktion:");
            cbType = new JComboBox<String>();

            cbType.addItem(strHinzufuegen);
            cbType.addItem(strEntfernen);
            cbType.addItem(strUmbenennen);

            cbType.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setInputPanel(cbType.getSelectedItem().toString());
                }
            });
            
            cbType.setSelectedItem(strHinzufuegen);
        } 
        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(
                gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                        .addGap(15)
                        .addComponent(lblAktion, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(10)
                        .addComponent(cbType, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                        )
                .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                        .addGap(15)
                        .addComponent(jpInputPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                )
                );
        gl_contentPanel.setVerticalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(cbType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addComponent(jpInputPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    )
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblAktion, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    )
        );
        contentPanel.setLayout(gl_contentPanel);
        {
            buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setActionCommand("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setAccepted(true);
                        exit();
                        setAccepted(false);
                    }
                });
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        exit();
                    }
                });
                buttonPane.add(cancelButton);
            }
        }
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
    
    public String getManageType(){
        return cbType.getSelectedItem().toString();
    }
    
    /**
     * tauscht Inputelemente passend zur Aktion (Auswahl durch preparedStatements)
     */
    private void setInputPanel(String type){
        Color cForegroundTransparent = new Color(
                cForeground.getRed(),
                cForeground.getGreen(),
                cForeground.getBlue(),
                cForeground.getAlpha() / 2
                );
        switch(type){
        case strHinzufuegen:{
            lblNeueKategorie.setForeground(cForeground);
            tfInput.setEnabled(true);
            lblAlteKategorie.setForeground(cForegroundTransparent);
            cbInput.setEnabled(false);
            break;
        }
        case strEntfernen:{
            lblNeueKategorie.setForeground(cForegroundTransparent);
            tfInput.setEnabled(false);
            lblAlteKategorie.setForeground(cForeground);
            cbInput.setEnabled(true);
            break;
        }
        case strUmbenennen:{
            lblNeueKategorie.setForeground(cForeground);
            tfInput.setEnabled(true);
            lblAlteKategorie.setForeground(cForeground);
            cbInput.setEnabled(true);
            break;
        }
        default:{
            throw new IllegalArgumentException("Falscher String bei setInputPanel");
        }
        }
    }
    
    public String getTextField(){
        return tfInput.getText();
    }
    
    public String getComboBox(){
        return cbInput.getSelectedItem().toString();
    }
    
    private void exit(){
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
    
    public void setColorForeground(Color color){
        cForeground = color;
        contentPanel.setForeground(color);
        jpInputPanel.setForeground(color);
        lblAktion.setForeground(color);
        lblAlteKategorie.setForeground(color);
        lblNeueKategorie.setForeground(color);
        
        setInputPanel(cbType.getSelectedItem().toString());
    }
    public void setColorBackground(Color color){
        cBackground = color;
        contentPanel.setBackground(color);
        jpInputPanel.setBackground(color);
        buttonPane.setBackground(color);
        
        setInputPanel(cbType.getSelectedItem().toString());
    }
}
