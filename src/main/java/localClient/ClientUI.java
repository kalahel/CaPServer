package localClient;

import database.DatabaseFiller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientUI {
    private JTextField textField1;
    private JButton sendButton;
    private JTextArea receivedTextArea;
    private JPanel rootPanel;
    private JPanel rightPanel;
    private JPanel LeftPanel;
    private JLabel sendingText;
    private JLabel receivingText;
    private JLabel connectionStatus;
    private JButton connectionButton;
    private ClientTcp clientTcp;

    public ClientUI() {
        clientTcp = new ClientTcp(this);
        connectionButton.addActionListener(new ConnectionBehaviour());
        sendButton.addActionListener(new SendingBehaviour());
    }

    private class ConnectionBehaviour implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!clientTcp.getConnected()) {
                if (clientTcp.connection()) {
                    connectionStatus.setText("Connection Status : Connected");
                }
            }
        }
    }

    private class SendingBehaviour implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (clientTcp.getConnected()) {
                switch (clientTcp.getTransactionState()) {
                    case ClientTcp.STARTING_STATE:
                        clientTcp.sendMessage(textField1.getText());
                        clientTcp.setTransactionState(ClientTcp.USERNAME_ENTERED);
                        break;
                    case ClientTcp.USERNAME_ENTERED:
                        /*
                        clientTcp.sendMessage(
                                DatabaseFiller.hashPasswordWithSeed(clientTcp.getCurrentSeed(),
                                        DatabaseFiller.hashPassword(clientTcp.getSelX(), clientTcp.getSelY(), textField1.getText())
                                )
                        );
                        */
                        clientTcp.sendMessage(DatabaseFiller.hashPassword(clientTcp.getSelX(), clientTcp.getSelY(), textField1.getText()));
                        /*System.out.println("Sending : " +
                                DatabaseFiller.hashPasswordWithSeed(clientTcp.getCurrentSeed(),
                                        DatabaseFiller.hashPassword(clientTcp.getSelX(), clientTcp.getSelY(), textField1.getText())
                                ));*/
                        System.out.println(DatabaseFiller.hashPassword(clientTcp.getSelX(), clientTcp.getSelY(), textField1.getText()));
                        clientTcp.setTransactionState(ClientTcp.PASSWORD_INFO_SENT);
                        break;
                }
            }
        }
    }

    public JTextField getTextField1() {
        return textField1;
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public JTextArea getReceivedTextArea() {
        return receivedTextArea;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
