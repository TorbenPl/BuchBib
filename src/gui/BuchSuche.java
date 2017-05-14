package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class BuchSuche extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private JTextField tfInput;
    private boolean isAccepted = false;
    private Character searchType = 'T';
    private JPanel rdbtnPane;
    private JRadioButton rdbtnTitel;
    private JRadioButton rdbtnAutor;
    private JRadioButton rdbtnDatum;
    private JPanel buttonPane;


    /**
     * Create the dialog.
     */
    public BuchSuche() {
        setTitle("Suche");
        setBounds(100, 100, 450, 170);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        rdbtnPane = new JPanel();
        rdbtnPane.setPreferredSize(new Dimension(80, 150));
        rdbtnPane.setMaximumSize(rdbtnPane.getPreferredSize());
        rdbtnPane.setLayout(new GridLayout(0, 1));
        {
            rdbtnTitel = new JRadioButton("Titel");
            rdbtnTitel.setPreferredSize(new Dimension(30, 20));
            rdbtnTitel.setMaximumSize(rdbtnTitel.getPreferredSize());
            buttonGroup.add(rdbtnTitel);
            rdbtnPane.add(rdbtnTitel);
            rdbtnTitel.addActionListener(myListenerSwitchRadioButton(rdbtnTitel));
            rdbtnTitel.setSelected(true);
        }
        {
            rdbtnAutor = new JRadioButton("Autor");
            rdbtnAutor.setPreferredSize(new Dimension(30, 20));
            rdbtnAutor.setMaximumSize(rdbtnAutor.getPreferredSize());
            buttonGroup.add(rdbtnAutor);
            rdbtnPane.add(rdbtnAutor);
            rdbtnAutor.addActionListener(myListenerSwitchRadioButton(rdbtnAutor));
        }
        {
            rdbtnDatum = new JRadioButton("Datum");
            rdbtnDatum.setPreferredSize(new Dimension(30, 20));
            rdbtnDatum.setMaximumSize(rdbtnDatum.getPreferredSize());
            buttonGroup.add(rdbtnDatum);
            rdbtnPane.add(rdbtnDatum);
            rdbtnDatum.addActionListener(myListenerSwitchRadioButton(rdbtnDatum));
        }
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        {
            tfInput = new JTextField();
            contentPanel.add(tfInput);
            tfInput.setPreferredSize(new Dimension(1000, 25));
            tfInput.setMaximumSize(tfInput.getPreferredSize());
            tfInput.setToolTipText("Suche eingeben");
            tfInput.setColumns(10);
        }
        contentPanel.add(rdbtnPane);
        
        {
            buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setActionCommand("OK");
                okButton.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
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
                cancelButton.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        exit();
                    }
                });
                buttonPane.add(cancelButton);
            }
        }
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e){
                tfInput.requestFocus();     
            }
        });        
    }
    
    private void exit(){
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
    
    public ActionListener myListenerSwitchRadioButton(JRadioButton rdbtn){
        return new ActionListener(){
            public void actionPerformed(ActionEvent e){
                rdbtn.setSelected(true);
                setSearchType(rdbtn.getText().charAt(0));
                tfInput.requestFocus();
            }
        };
    }

    public Character getSearchType() {
        return searchType;
    }

    public void setSearchType(Character searchType) {
        this.searchType = searchType;
    }
    
    public String getInput(){
        return tfInput.getText();
    }

    public JTextField getTfInput() {
        return tfInput;
    }

    public void setTfInput(JTextField tfInput) {
        this.tfInput = tfInput;
    }
    
    public void setColorForeground(Color color){
        contentPanel.setForeground(color);
        buttonPane.setForeground(color);
        Enumeration<AbstractButton> it = buttonGroup.getElements();
        while(it.hasMoreElements()){
            it.nextElement().setForeground(color);
        }
    }
    public void setColorBackground(Color color){
        contentPanel.setBackground(color);
        buttonPane.setBackground(color);
        Enumeration<AbstractButton> it = buttonGroup.getElements();
        while(it.hasMoreElements()){
            it.nextElement().setBackground(color);
        }
    }
}
