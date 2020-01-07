package localClient;

import javax.swing.*;

public class ClientUI {
    private JTextField textField1;
    private JButton sendButton;
    private JTextArea receivedTextArea;
    private JPanel rootPanel;
    private JPanel rightPanel;
    private JPanel LeftPanel;
    private JLabel sendingText;
    private JLabel receivingText;

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
