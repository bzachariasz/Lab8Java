import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PooledConnectionHandler implements Runnable {
    Socket clientSocket;
    static List pool = new LinkedList();

    String USERNAME = "user";
    String PASSWORD = "passwd";


    PrintWriter out = null;
    BufferedReader in = null;
    String ID = "";
    String[] filenames = {"file", "file1"};

    public void handleConnection() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                if ((inputLine.equals("LOGIN " + USERNAME + ";" + PASSWORD))) {
                    ID = UUID.randomUUID().toString().substring(0, 10);
                    out.println(ID);
                } else if (inputLine.equals("LOGOUT " + ID)) {
                    out.println(true);
                    close();
                } else if (inputLine.equals("LS " + ID)) {
                    String result = "";
                    for (int i = 0; i < filenames.length; i++) {
                        result += filenames[i];
                        if (i != filenames.length - 1) {
                            result += ";";
                        }
                    }
                    out.println(result);
                } else if (inputLine.equals("GET " + ID + " " + filenames[0]) || inputLine.equals("GET " + ID + " " + filenames[1])) {
                    for (String file : filenames) {
                        if (file.equals(inputLine.substring(15))) {
                            BufferedReader fileReader = new BufferedReader(new FileReader("/Users/bartek/Desktop" + file));
                            out.println(fileReader.readLine());
                            fileReader.close();
                        }
                    }

                } else if (inputLine.length() >= 5) {
                    if (inputLine.substring(0, 5).equals("LOGIN")) {
                        String wrongPassword = inputLine.substring(inputLine.indexOf(";") + 1);
                        out.println(Levenshtein.levenshtein(PASSWORD, wrongPassword));
                    } else
                        out.println(false);
                } else {
                    out.println(false);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void processRequest(Socket requestToHandle) {
        synchronized (pool) {
            pool.add(pool.size(), requestToHandle);
            pool.notifyAll();
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (pool) {
                while (pool.isEmpty()) {
                    try {
                        pool.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                clientSocket = (Socket) pool.remove(0);
            }
            handleConnection();
        }
    }

    public void close() throws IOException {

        if (clientSocket != null)
            clientSocket.close();

        if (out != null)
            out.close();

        if (in != null)
            in.close();

    }
}
