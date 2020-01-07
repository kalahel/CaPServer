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
    private BufferedReader inputStream;
    private PrintWriter outputStream;


    public ClientTcp() {
        // TODO CHANGE TO LOCALHOST
        this.portNumber = 5000;
        this.address = "localhost";
        this.isConnected = false;
    }

    public Boolean connection(){
        try {
            socket = new Socket(this.address, this.portNumber);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = new PrintWriter(socket.getOutputStream(), true);

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

    public Boolean getConnected() {
        return isConnected;
    }
}
