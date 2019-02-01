import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {
    private static Socket clientSocket = null;
    private static PrintStream os = null;   // The output stream
    private static DataInputStream is = null;   // The input stream

    private static BufferedReader inputLine = null;
    private static boolean closed = false;

    public static void main(String[] args) {

        int portNumber = 5555;  // Default portnumber
        String host = "localhost";

        if (args.length < 2) {
            System.out.println("Usage: java MultiThreadClient <host> <portNumber>\n" +
                    "Now using host = " + host + ", portNumber = " + portNumber);
        } else {
            host = args[0];
            portNumber = Integer.valueOf(args[1]).intValue();
        }

        // Open a socket on given host and port, open input and output streams
        try {
            clientSocket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Unknown host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host " + host);
        }

        // If everything above is OK, write data to the opened socket with connection to port portNumber
        if (clientSocket != null && os != null && is != null) {
            try {
                // Create a thread to read from the server
                new Thread(new Client()).start();
                while (!closed) {
                    os.println(inputLine.readLine().trim());
                }
                // Closer streams and socket
                os.close();
                is.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("IOException: " + e);
            }
        }
    }

    // Create a thread to read from server

    public void run() {
        // Keep reading until we receive "Bye" from the server
        String responseLine;
        try {
            while ((responseLine = is.readLine()) != null) {
                System.out.println(responseLine);
                if (responseLine.indexOf("*** Bye") != -1) break;
            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        }
    }
}
