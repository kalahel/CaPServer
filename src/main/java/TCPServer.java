
import database.DBConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TCPServer {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    //initialize socket and input stream

    HashMap<InetAddress, Socket> socketsIP;
    HashMap<Socket, String> socketsId;
    Boolean stopSCmdThread = false;
    ArrayList<Socket> sockets;

    // Lock l = new ReentrantLock();

    /**
     * @param port If port is set to -1 localhost is used
     */
    public TCPServer(int port) {
        Socket socket;
        ServerSocket server;
        Scanner sc = new Scanner(System.in);
        sockets = new ArrayList<>();
        socketsIP = new HashMap<>();
        socketsId = new HashMap<>();

        try {
            if (port == -1)
                server = new ServerSocket(9090, 0, InetAddress.getByName(null));
            else
                server = new ServerSocket(port);
            System.out.println(ANSI_GREEN + "Server started" + ANSI_RESET);
            System.out.println("Waiting for a client ...");

            Thread cmd = new Thread(() -> {
                while (true) {
                    String sCmd = sc.nextLine();
                    System.out.println("[CMD] " + sCmd);
                }
            });
            cmd.start();

            do {
                socket = server.accept();
                System.out.println(ANSI_GREEN + "Client accepted : " + socket.getInetAddress().getHostName() + " on " + socket.getInetAddress().getHostAddress() + ANSI_RESET);
                sockets.add(socket);
                socketsIP.put(socket.getInetAddress(), socket);

                SocketLife sl = new SocketLife(socket);
                Thread t = new Thread(sl);
                t.setUncaughtExceptionHandler(uncaughtExceptionHandler);
                t.setName("SocketLife");
                t.start();
                System.out.println("SOCKETS : " + sockets.size());
            } while (true);

        } catch (IOException i) {
            System.err.println(i);
        }
    }

    public static void main(String[] args) {
        new TCPServer(5000);
    }

    Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (th, ex) -> {
//        System.err.println("What happens in " + th.getName() + " stays in " + th.getName());
        stopSCmdThread = true;
    };

    public class SocketLife implements Runnable {
        Socket s;
        DataInputStream in;
        DataOutputStream out;
        BufferedReader bufIn;
        PrintWriter wrOut;

        public SocketLife(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try {
                bufIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
                wrOut = new PrintWriter(s.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String line;
            Command cmd;

            while (true) {
                try {
                    line = bufIn.readLine();
                    if (line == null)
                        break;
                    switch (line) {
                        case "bonjour":
                            wrOut.println("Bonjour a vous !");
                            System.out.println("Connected clients :");
                            for (Map.Entry<InetAddress, Socket> entry : socketsIP.entrySet()) {
                                if (socketsId.containsKey(entry.getValue()))
                                    System.out.println(entry.getKey() + " : " + entry.getValue().toString() + " identified as " + ANSI_PURPLE + socketsId.get(entry.getValue()) + ANSI_RESET);
                                else
                                    System.out.println(entry.getKey() + " : " + entry.getValue().toString());
                            }
                            break;
                        case "left":
                            wrOut.println("Going Left");
                            break;
                        case "right":
                            wrOut.println("Going Right");
                            break;
                        default:
                            cmd = cmdParser(line);
                            if (cmd != null)
                                System.err.println(cmd.toString());
                            break;
                    }

                } catch (EOFException i) {
                    System.out.println(ANSI_RED + "Client from " + s.getInetAddress().getHostAddress() + " disconnected" + ANSI_RESET);
                    try {
                        closeConnection();
                        return;
                    } catch (IOException e) {
                        System.err.println("IN DIRTY CLOSING");
                        e.printStackTrace();
                    }
                } catch (SocketException e) {
                    try {
                        closeConnection();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    return;
                } catch (IOException e) {
                    System.err.println("IN SOCKET EXCHANGES");
                    e.printStackTrace();
                }
            }
            if (socketsId.containsKey(s))
                System.out.println(ANSI_RED + "Closing connection with " + socketsId.get(s) + " on " + s.getInetAddress().getHostAddress() + ANSI_RESET);
            else
                System.out.println(ANSI_RED + "Closing connection with " + s.getInetAddress().getHostAddress() + ANSI_RESET);
            try {
                closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Socket socketFromId(String id) {
            for (Map.Entry<Socket, String> entry : socketsId.entrySet())
                if (entry.getValue().equals(id))
                    return entry.getKey();
            return null;
        }

        private void closeConnection() throws IOException {
            if (socketsId.get(s).equals("local")) {
                for (Socket t_s : sockets)
                    if (t_s != s)
                        new PrintWriter(t_s.getOutputStream(), true).println("OTHERS,local," + (new Date()).getTime() / 1000 + ",DISC,7,,0");
            }
            socketsIP.remove(s.getInetAddress());
            socketsId.remove(s);
            sockets.remove(s);
            s.close();
            out.close();
            in.close();
            stopSCmdThread = true;
        }

        private Command cmdParser(String cmd) {
            Locale locale = new Locale("fr", "FR");
            DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
            /*
              DST,  SRC,   TIME,       TYPE,   PRIORITY,   SENS_ID,    VAL
              STR, STR,    TIMESTAMP,  STR,    INT,        STR,        STR
             */
            Command c = null;
            try {
                c = new Command(cmd.split(","));
                /*
                l.lock();
                session = dbConnection.getSessionFactory().getCurrentSession();
                Transaction transaction = session.beginTransaction();
                session.persist(c);
                transaction.commit();
                session.close();
                l.unlock();

                */

                System.out.println(ANSI_YELLOW + " [" + s.getInetAddress().getHostAddress() + "] " + c.getEntireString() + ANSI_RESET);

                switch (c.getType()) {
                    case "AUTH":
                        if (!socketsId.containsKey(s) && !socketsId.containsValue(c.getValue())) {
                            socketsId.put(s, c.getValue());
                            System.out.println(ANSI_CYAN + s.getInetAddress().getHostAddress() + " logged in as " + c.getValue() + ANSI_RESET);
                            wrOut.println("You are now logged in as " + c.getValue());
                        } else if (socketsId.containsKey(s))
                            System.err.println(ANSI_CYAN + s.getInetAddress().getHostAddress() + " is already logged in." + ANSI_RESET);
                        else
                            System.err.println(ANSI_CYAN + c.getValue() + " is already logged in." + ANSI_RESET);
                        break;
                    case "PING":
                        wrOut.println(ANSI_RED + "p" + ANSI_CYAN + "o" + ANSI_PURPLE + "n" + ANSI_GREEN + "g" + ANSI_RESET);
                        System.out.println("[" + s.getInetAddress().getHostAddress() + "] " + ANSI_RED + "p" + ANSI_CYAN + "o" + ANSI_PURPLE + "n" + ANSI_GREEN + "g" + ANSI_RESET);
                        break;
                    case "LIST":
                        if (!socketsId.containsKey(s)) {
                            wrOut.println("You need to be authenticated to ask for the list.");
                            break;
                        }
                        String out = "";
                        for (Socket tmp_s : sockets) {
                            if (socketsId.containsKey(tmp_s))
                                out += (socketsId.get(tmp_s) + "@" + tmp_s.getInetAddress().getHostAddress() + ";");
                            else
                                out += (tmp_s.getInetAddress().getHostAddress() + ";");
                        }
                        out = out.substring(0, out.length() - 1);
                        wrOut.println(socketsId.get(s) + ",SRV," + (new Date().getTime() / 1000) + ",LIST_ACK,-1,," + out);
                        break;
                    case "MSG":
                        if (!socketsId.containsKey(s)) {
                            wrOut.println("You need to be authenticated to send messages.");
                            break;
                        }

                        String dst_id = c.getDestination();
                        String src_id = c.getSource();
                        // Sent to one client
                        if ("OTHERS".equals(dst_id)) {  // Sent to every other client
                            for (Socket t_s : sockets)
                                if (t_s != s)
                                    new PrintWriter(t_s.getOutputStream(), true).println(src_id + ":" + c.getValue());
                        } else {
                            if (socketFromId(dst_id) != null)
                                new PrintWriter(socketFromId(dst_id).getOutputStream(), true).println(src_id + ":" + c.getValue());
                            else
                                wrOut.println(dst_id + " was not found.");
                        }
                        break;
                    default:
                        if (!socketsId.containsKey(s)) {
                            wrOut.println("You need to be authenticated to send messages.");
                            break;
                        }

                        String d_id = c.getDestination();
                        switch (d_id) {
                            case "OTHERS":  // Sent to every other client
                                for (Socket t_s : sockets)
                                    if (t_s != s)
                                        new PrintWriter(t_s.getOutputStream(), true).println(c.getEntireString());
                                break;
                            default:    // Sent to one client
                                if (socketFromId(d_id) != null)
                                    new PrintWriter(socketFromId(d_id).getOutputStream(), true).println(c.getEntireString());
                                else
                                    wrOut.println(d_id + " was not found.");
                                break;
                        }
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Missing parameters : " + cmd);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return c;
        }
    }
}
