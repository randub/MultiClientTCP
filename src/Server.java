import java.io.*;
import java.net.Socket;
import org.jsoup.Jsoup;
import java.net.ServerSocket;
import java.io.IOException;
import java.util.Date;


import static com.oracle.net.Sdp.openServerSocket;

public class Server {

    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    private static final int maxClientsCount = 10;  // Max number of client connection the server accepts
    private static final clientThread[] threads = new clientThread[maxClientsCount];
    private String dateTime;

    public static void main(String[] args) {

        int portNumber = 5555;
        if (args.length < 1) {
            System.out.println("Usage: java MultiThreadServer <portNumber>\n" +
                    "now using port number = " + portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }

        // Open a server socket on port number (default 5555)
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }

        // Create a client socket for each connection and pass it to a new client thread
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i = 0;
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new clientThread(clientSocket, threads)).start();
                        break;
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server is too busy, try again later.");
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}


     class clientThread extends Thread {
        private String clientName = null;
        private DataInputStream is = null;
        private PrintStream os = null;
        private Socket clientSocket = null;
        private final clientThread[] threads;
        private int maxClientsCount;
        public String dateTime = null;
        public String place = "";

        public clientThread(Socket clientSocket, clientThread[] threads) {
            this.clientSocket = clientSocket;
            this.threads = threads;
            maxClientsCount = threads.length;
        }

        public void run() {
            int maxClientsCount = this.maxClientsCount;
            clientThread[] threads = this.threads;

            try {
                is = new DataInputStream(clientSocket.getInputStream());
                os = new PrintStream(clientSocket.getOutputStream());
                String place;
                while (true) {
                    os.println("Please enter a city or country, type /quit to quit.");
                    place = is.readLine();
                    if (place.startsWith("/quit")) {
                        break;
                    }
                    dateTime = WorldTimeService.getTime(place);
                    os.println("From server: Time in " + place + " is: " + dateTime);

                }
            } catch (IOException e) {
                System.err.println("Problems with I/O.");
            }
            os.println("*** Bye ***");


            // Cleaning up - set current thread variable to null so a new client can be accepted by server
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }
            try {
                // Close streams and socket:
                is.close();
                os.close();
                clientSocket.close();
            } catch (IOException e){
                System.err.println(e);
            }
        }
}