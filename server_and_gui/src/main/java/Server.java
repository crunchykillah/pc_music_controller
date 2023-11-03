import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Server {
    private static final int PORT = 11616;
    private ProcessCommand processCommand;

    public static void main(String[] args) {
        ProcessCommand processCommand = null;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            File tempNircmd = File.createTempFile("nircmd", ".exe");
            try (InputStream is = Server.class.getResourceAsStream("nircmd.exe")) {
                Files.copy(is, tempNircmd.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            ComputerControl computerControl = new ComputerControl(tempNircmd.getAbsolutePath());
            System.out.println("Server successfully started " );
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().toString().replace("/",""));
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client: " + inputLine);
                    if (inputLine.equals("/server_shutdown")) {
                        System.out.println("Server is shutting down...");
                        in.close();
                        clientSocket.close();
                        System.exit(0);
                    } else {
                        processCommand = new ProcessCommand(inputLine,computerControl);
                        new Thread(processCommand).start();
                    }
                }
                in.close();
                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


//5.101.16.112