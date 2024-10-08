import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame {
    private JButton conn = new JButton("�s�u");
    private JTextArea txt = new JTextArea("");
    private JTextField edt = new JTextField("");
    private JButton send = new JButton("�ǰe");
    private JPanel p1 = new JPanel();
    private JPanel p2 = new JPanel();
    private ExecutorService exec = null;
    private Socket clientSocket = null;
    private DataInputStream fromServer;
    private DataOutputStream toServer;

    public Client() {
        setLayout(new BorderLayout());
        p1.setLayout(new BorderLayout());
        p2.setLayout(new BorderLayout());
        p1.add(edt, BorderLayout.CENTER);
        p1.add(send, BorderLayout.EAST);

        p2.add(new JScrollPane(txt), BorderLayout.CENTER);
        p2.add(p1, BorderLayout.SOUTH);
        p2.add(conn, BorderLayout.NORTH);

        add(p2, BorderLayout.CENTER);
        setSize(500, 300);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        conn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }

    private void connectToServer() {
        exec = Executors.newCachedThreadPool();
        exec.execute(new ClientSocket());
    }

    private void sendMessage() {
        String message = edt.getText();
        try {
            toServer.writeUTF(message);
            toServer.flush();
            edt.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ClientSocket implements Runnable {
        @Override
        public void run() {
            try {
                clientSocket = new Socket("localhost", 54321);
                fromServer = new DataInputStream(clientSocket.getInputStream());
                toServer = new DataOutputStream(clientSocket.getOutputStream());

                while (true) {
                    String message = fromServer.readUTF();
                    txt.append(message + "\n");
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
