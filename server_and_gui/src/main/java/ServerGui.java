import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ServerGui {
    private static boolean serverStarted = false;
    private static Color darkRedColor  = new Color(139,0,0);

    public static void main(String[] args) throws IOException, FontFormatException {
        FlatMacDarkLaf.setup();
        JFrame frame = new JFrame("Server GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 700); // Увеличим высоту окна
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) (screenSize.getWidth() - frame.getWidth()) / 2;
        int centerY = (int) (screenSize.getHeight() - frame.getHeight()) / 2;
        frame.setLocation(centerX, centerY);

        JPanel panel = new JPanel(new GridBagLayout());
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) throws IOException, FontFormatException {
        ClassLoader classLoader = ServerGui.class.getClassLoader();
        InputStream gothicFontStream = classLoader.getResourceAsStream("gothTitleFont.ttf");
        InputStream textFontStream = classLoader.getResourceAsStream("mainTextFont.ttf");
        InputStream imageStream = classLoader.getResourceAsStream("skull16mobb.png");

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        Font gothicFont = Font.createFont(Font.TRUETYPE_FONT, gothicFontStream);
        gothicFont = gothicFont.deriveFont(52f);
        Font textFont = Font.createFont(Font.TRUETYPE_FONT, textFontStream);
        textFont = textFont.deriveFont(18f);


        // MUSIC CONTROLLER TITLE
        JLabel titleLabel = new JLabel("MUSIC CONTROLLER");
        titleLabel.setFont(gothicFont);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        panel.add(titleLabel, constraints);

        //  "IP Address"
        JLabel ipLabel = new JLabel("IP Address:");
        ipLabel.setFont(textFont);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(ipLabel, constraints);


        //  "Port"
        JLabel portLabel = new JLabel("Port:      11616");
        portLabel.setFont(textFont);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(portLabel, constraints);

        // "Start Server"
        JButton startServerButton = new JButton("Start Server");
        startServerButton.setFont(textFont);
        startServerButton.setBackground(Color.BLACK);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(startServerButton, constraints);

        // "Shutdown Server"
        JButton shutdownServerButton = new JButton("Shutdown Server");
        shutdownServerButton.setFont(textFont);
        shutdownServerButton.setBackground(darkRedColor);
        shutdownServerButton.setEnabled(false);
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(shutdownServerButton, constraints);

        JLabel imageLabel = new JLabel();
        // SKULL IMAGE
        ImageIcon imageIcon = new ImageIcon(ImageIO.read(imageStream));
        imageLabel.setIcon(imageIcon);
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(imageLabel, constraints);

        //   "Server output"
        JLabel consoleOutputLabel = new JLabel("Server Output:");
        consoleOutputLabel.setFont(textFont);
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(consoleOutputLabel, constraints);

        // CONSOLE OUTPUT
        JTextArea consoleOutputTextArea = new JTextArea(10, 40);
        consoleOutputTextArea.setFont(textFont);
        consoleOutputTextArea.setEditable(false);
        JScrollPane consoleOutputScrollPane = new JScrollPane(consoleOutputTextArea);
        constraints.gridx = 0;
        constraints.gridy = 7;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(consoleOutputScrollPane, constraints);



        PrintStream printStream = new PrintStream(new ConsoleOutputStream(consoleOutputTextArea));
        System.setOut(printStream);
        System.setErr(printStream);



        startServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!serverStarted) {
                    Thread serverThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Server.main(new String[0]);
                        }
                    });
                    serverThread.start();
                    serverStarted = true;
                    shutdownServerButton.setEnabled(true);
                    startServerButton.setEnabled(false);
                }
            }
        });

        shutdownServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    serverShutdown();
                    serverStarted = false;
                    shutdownServerButton.setEnabled(false);
                    startServerButton.setEnabled(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });


        String localIpAddress = GetIpAddress.getWlanIpAddress();
        if (localIpAddress.equals("no ip")) {
            localIpAddress = InetAddress.getLocalHost().getHostAddress();
        }
        ipLabel.setText("IP Address:     " + localIpAddress);
    }

    public static void serverShutdown() throws IOException {
        try (Socket clientSocket = new Socket("localhost", 11616)) {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            out.write("/server_shutdown");
            out.flush();
        }
    }
}
