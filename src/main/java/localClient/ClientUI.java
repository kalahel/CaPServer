package localClient;

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
                clientTcp.sendMessage(textField1.getText());
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
