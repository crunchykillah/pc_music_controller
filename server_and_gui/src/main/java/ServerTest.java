import java.io.*;
import java.net.Socket;

public class ServerTest {

    private static BufferedReader reader;
    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args) {
        try {
            try (Socket clientSocket = new Socket("localhost", 1616)) {
                reader = new BufferedReader(new InputStreamReader(System.in));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                System.out.println("Вы что-то хотели сказать? Введите это здесь:");
                String word = reader.readLine(); // ждём пока клиент что-нибудь
                // не напишет в консоль
                out.write(word + "\n"); // отправляем сообщение на сервер
                out.flush();
            } finally {
                out.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }
}