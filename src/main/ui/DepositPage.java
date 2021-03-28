package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class DepositPage extends JPanel implements ActionListener, ItemListener {
    JPanel app;
    JButton confirmButton = new JButton("Confirm Deposit");

    public DepositPage(JPanel app) {
        this.app = app;
        JPanel creditCardPane = new JPanel();

        String[] creditCards = {"Placeholder Card 1", "Placeholder Card 2"};

        JComboBox cb = new JComboBox(creditCards);

        cb.setEditable(false);
        cb.addItemListener(this);

        creditCardPane.add(cb);

        TextField amountField = new TextField();
        amountField.setPreferredSize(new Dimension(100, 30));
        JLabel amountLabel = new JLabel("Deposit Amount:");

        confirmButton.addActionListener(this);

        JButton returnToMenuButton = new ReturnToMenuButton(this.app);

        add(creditCardPane);
        add(amountLabel);
        add(amountField);
        add(confirmButton);
        add(returnToMenuButton);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        CardLayout cl = (CardLayout) (this.app.getLayout());
        if (e.getSource() == confirmButton) {
            cl.show(this.app, Pages.MENU.name());
        }


    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }
}
