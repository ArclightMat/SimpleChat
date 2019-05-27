import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import javax.swing.*;

public class Client extends JFrame implements ActionListener, KeyListener{
    private JTextArea textChat;
    private JTextField txtMsg;
    private JButton btnSend;
    private JButton btnExit;
    private JLabel lblMsg;
    private JPanel pnlContent;
    private Socket socket;
    private JTextField txtIP;
    private JTextField txtPort;
    private JTextField txtNick;

    private PrintWriter output;
    private BufferedReader input;

    private Client() {
        JLabel lblIP = new JLabel("IP do servidor:");
        txtIP = new JTextField("127.0.0.1");
        JLabel lblPort = new JLabel("Porta do servidor:");
        txtPort = new JTextField("1337");
        JLabel lblNick = new JLabel("Username:");
        txtNick = new JTextField("Cliente");
        Object[] clientUI = {lblIP, txtIP, lblPort, txtPort, lblNick, txtNick};
        JOptionPane.showMessageDialog(null, clientUI);
        pnlContent = new JPanel();
        textChat = new JTextArea(10, 40);
        textChat.setEditable(false);
        txtMsg = new JTextField(20);
        lblMsg = new JLabel(txtNick.getText() + ":");
        btnSend = new JButton("Enviar");
        btnSend.setToolTipText("Enviar Mensagem");
        btnExit = new JButton("Desconectar");
        btnSend.addActionListener(this);
        btnExit.addActionListener(this);
        btnSend.addKeyListener(this);
        txtMsg.addKeyListener(this);
        JScrollPane scroll = new JScrollPane(textChat);
        textChat.setLineWrap(true);
        pnlContent.add(scroll);
        pnlContent.add(lblMsg);
        pnlContent.add(txtMsg);
        pnlContent.add(btnExit);
        pnlContent.add(btnSend);
        setTitle(txtNick.getText());
        setContentPane(pnlContent);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(500, 300);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    private void connect(){
        try {
            socket = new Socket(txtIP.getText(),Integer.parseInt(txtPort.getText()));
            output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output.write(txtNick.getText() + "\n");
            output.flush();
            String msg = "";
            while (!"/q".equalsIgnoreCase(msg)) {
                if(input.ready()) {
                msg = input.readLine();
                textChat.append(msg + "\n");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Conexão recusada! Servidor pode estar desligado.\n\nException: " + e);
            System.exit(1);
        }

    }
    private void sendMsg (String msg) throws IOException{
        if(msg.equals("/q")) {
            stop();
        } else if (!msg.equals("")){
            output.write(msg + "\n");
            textChat.append(txtNick.getText() + ":" + txtMsg.getText()+"\n");
        }
        output.flush();
        txtMsg.setText("");
    }
    private void stop() throws IOException{
        output.write("desconectou\n");
        output.flush();
        output.close();
        socket.close();
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e) {

        try {
            if(e.getActionCommand().equals(btnSend.getActionCommand()))
                sendMsg(txtMsg.getText());
            else
            if(e.getActionCommand().equals(btnExit.getActionCommand()))
                sendMsg("/q");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,"Você não deveria ver isso.\n\nException: " + ex);
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            try {
                sendMsg(txtMsg.getText());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,"Você não deveria ver isso.\n\nException: " + ex);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
    }

    private static void fallbackCross() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Você não deveria ver isso.\n\nException: " + e);
        }
    }

    private static void nativeLookAndFeel() {
        String OS = System.getProperty("os.name").toLowerCase();
        if(OS.contains("windows")) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception e) {
                fallbackCross();
            }
        }
        else if (OS.contains("linux")) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } catch (Exception e) {
                fallbackCross();
            }
        }
        else {
            fallbackCross(); // Mac deve usar aparência nativa automaticamente, usercases como BSD/Unix estão além do escopo.
        }
    }


    public static void main(String []args) {
        nativeLookAndFeel();
        Client chatClient = new Client();
        chatClient.connect();
    }
}
