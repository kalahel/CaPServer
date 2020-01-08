package localClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTcp {
    private Socket socket;
    private int portNumber;
    private String address;
    private Boolean isConnected;
    private Boolean shouldRun;
    private BufferedReader inputStream;
    private PrintWriter outputStream;
    private ListenerRunnable listenerRunnable;
    private ClientUI clientUI;

    // TODO ADD LISTENING

    public ClientTcp(ClientUI clientUI) {
        this.portNumber = 5000;
        this.address = "localhost";
        this.isConnected = false;
        this.shouldRun = true;
        this.clientUI = clientUI;
    }

    public Boolean connection() {
        try {
            socket = new Socket(this.address, this.portNumber);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = new PrintWriter(socket.getOutputStream(), true);
            listenerRunnable = new ListenerRunnable(clientUI,this);
            Thread listenerThread = new Thread(listenerRunnable);
            listenerThread.start();


        } catch (UnknownHostException e) {
            System.err.println("Hote inconnu");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Can't connect");
            return false;
        }
        isConnected = true;
        return true;
    }

    public void sendMessage(String message) {
        outputStream.println(message);
    }

    public Boolean getConnected() {
        return isConnected;
    }

    public Boolean getShouldRun() {
        return shouldRun;
    }

    public BufferedReader getInputStream() {
        return inputStream;
    }

    private class ListenerRunnable implements Runnable {
        private ClientUI clientUI;
        private ClientTcp clientTcp;

        public ListenerRunnable(ClientUI clientUI, ClientTcp clientTcp) {
            this.clientUI = clientUI;
            this.clientTcp = clientTcp;
        }

        @Override
        public void run() {
            while (clientTcp.getShouldRun()) {
                if (clientTcp.getConnected()) {
                    String receivedString;
                    try {
                        receivedString = clientTcp.getInputStream().readLine();
//                        clientUI.getReceivedTextArea().setText("");
                        clientUI.getReceivedTextArea().append(receivedString + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
