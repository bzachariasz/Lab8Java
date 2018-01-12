import java.io.*;
import java.net.*;

public class Client {
    Socket echoSocket = null;
    String ID = null;
    PrintWriter out = null;
    BufferedReader in = null;
    BufferedReader stdIn = null;

    public void connect() {

        try {

            echoSocket = new Socket("localhost", 6666);

            out = new PrintWriter(echoSocket.getOutputStream(), true);
            stdIn = new BufferedReader(
                    new InputStreamReader(System.in));

            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

        } catch (UnknownHostException e) {
            System.err.println("Unknown host");
            System.exit(1);

        } catch (IOException e) {
            System.err.println("Couldnt get I/O");
            System.exit(1);
        }


    }

    public void close() throws IOException {
        if (echoSocket != null)
            echoSocket.close();

        if (out != null)
            out.close();

        if (in != null)
            in.close();

        if (stdIn != null)
            stdIn.close();
    }

    public boolean login(String username, String password) throws IOException {

        out.println("LOGIN " + username + ";" + password);

        String result = in.readLine();
        System.out.println(result);
        if (result.length() != 10) {
            return false;
        } else {
            ID = result;
            return true;
        }
    }

    public void logout() throws IOException {
        out.println("LOGOUT " + ID);
        String result = in.readLine();
        System.out.println("LOGOUT=" + result);
    }

    public void ls() throws IOException {

        out.println("LS " + ID);
        String result = in.readLine();
        System.out.println("LS=" + result);
    }

    public void get(String filename) throws IOException {
        out.println("GET " + ID + " " + filename);
        String result = in.readLine();
        System.out.println("GET=" + result);
    }

    public void write(Client client) {

        String userInput;


        try {
            while ((userInput = stdIn.readLine()) != null) {
                try {
                    if (userInput.equals("connect")) {
                        client.connect();
                        System.out.println("Type a message: ");
                    } else {
                        out.println(userInput);
                        System.out.println("echo: " + in.readLine());
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();

        client.connect();
        client.write(client);

    }

}


