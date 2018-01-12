import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class Server {
    ServerSocket serverSocket = null;

    public void acceptConnection() {
        try {
            serverSocket = new ServerSocket(6666);
            Socket clientSocket = null;
            while (true) {
                clientSocket = serverSocket.accept();
                handleConnection(clientSocket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void setUpHandlers() {
        for (int i = 0; i < 50; i++) {
            PooledConnectionHandler currentHandler = new PooledConnectionHandler();
            new Thread(currentHandler, "Handler " + i).start();
        }
    }

    public void handleConnection(Socket connectiontoHandle) {
        PooledConnectionHandler.processRequest(connectiontoHandle);
    }


    public static void main(String[] args) {
        Server server = new Server();
        server.setUpHandlers();

        server.acceptConnection();
        try {
            server.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
