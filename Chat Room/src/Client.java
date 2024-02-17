import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    public String activeUsers;

    JPanel panel;
    private JTextArea chatArea = new JTextArea();
    private JTextArea onlineUsers = new JTextArea();
    private JTextField inputField = new JTextField();

    public Client() {

        super("Chat Client by Cian Boyle");

        chatArea = new JTextArea();
        onlineUsers = new JTextArea();


        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        onlineUsers.setEditable(false);
        JScrollPane usersPane = new JScrollPane(onlineUsers);

        inputField = new JTextField();

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Create a new JTextArea for the additional text area above the "Send" button

        onlineUsers.setLineWrap(true);
        usersPane.setPreferredSize(new Dimension(200, 100));

        // Adjust the layout to accommodate the new text area
        panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(usersPane, BorderLayout.NORTH);

        panel.add(inputField, BorderLayout.SOUTH);
        panel.add(sendButton, BorderLayout.EAST);

        add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    @Override
    public void run() {
        try {
            client = new Socket("0.0.0.0", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inputHandler = new InputHandler();
            Thread t = new Thread(inputHandler);
            t.start();

            String inMessage;

            while ((inMessage = in.readLine()) != null) {

                if(inMessage.startsWith("/:users:/ ")) {
                    String[] message = inMessage.split(" ", 2);
                    onlineUsers.setText("Online Users: \n");

                    // Split the message into individual names
                    String[] userNames = message[1].split(" ");

                    for (String userName : userNames) {


                        if(userName.contains("(You)")){
                            onlineUsers.append(userName.replace("-", " ") + "\n");
                        }
                        else{
                            onlineUsers.append(userName + "\n");
                        }
                    }
                }
                else if(inMessage.startsWith("/:kick:/ ")) {
                    out.println(":/kicked/:");
                    String[] message = inMessage.split(" ", 2);
                    System.out.println("You've been kicked by: " + message[1]);
                    shutDown();
                } else{
                    chatArea.append(inMessage + "\n");
                }

            }

        } catch (IOException e) {
            shutDown();
        }
    }

    private void sendMessage() {
        try{
            String message = inputField.getText();
            if (message.equals("/quit")) {
                out.println(message);
                shutDown();
            } else if(!message.isEmpty()) {
                out.println(message);
                inputField.setText("");
            }
        }catch (Exception e){
            shutDown();
        }

    }

    public void shutDown() {
        done = true;
        try {
            in.close();
            out.close();

            if (!client.isClosed()) {
                client.close();
            }

            setVisible(false);
            dispose();
            System.exit(0);

        } catch (IOException e) {
            // Ignore
        }
    }

    class InputHandler implements Runnable {
        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String message = inReader.readLine();

                    if (message.equals("/quit")) {
                        out.println(message);
                        inReader.close();
                        shutDown();
                    } else {
                        out.println(message);
                    }
                }
                inReader.close();
            } catch (IOException e) {
                shutDown();
            }
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Client clientGUI = new Client();
            clientGUI.setVisible(true);
            new Thread(clientGUI).start();
        });
    }
}

