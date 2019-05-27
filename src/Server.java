import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
    private static ArrayList<PrintWriter> clientList;
    private static ServerSocket server;
    private static String name;
    private Socket con;

    private BufferedReader input;
    private PrintWriter output;

    private Server(Socket con) {
        this.con = con;
        try {
            input = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try {
            String msg;
            output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.con.getOutputStream())));
            clientList.add(output);
            name = msg = input.readLine();
            while(!"/q".equalsIgnoreCase(msg) && msg != null)
            {
                msg = input.readLine();
                sendToAll(output, msg);
                System.out.println(msg);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToAll(PrintWriter pwOutput, String msg) {
        PrintWriter pwO;
        for(PrintWriter pw : clientList) {
            pwO = pw;
            if (!(pwOutput == pwO)) {
                pw.write(name + ":" + msg + "\n");
                pw.flush();
            }
        }
    }

    public static void main(String[] args) {
        int port = 1337;
        if (args.length != 1) {
            System.out.println("Usando porta 1337.\nPara definir sua porta: java Server <porta>");
        }
        else {
            port = Integer.parseInt(args[0]);
        }
        try {
            server = new ServerSocket(port);
            clientList = new ArrayList<>();
            System.out.println("Servidor executando na porta: " + port);
            while(true){
                System.out.println("Aguardando conexão...");
                Socket con = server.accept();
                System.out.println(con + " conectado...");
                Thread t = new Server(con);
                t.start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}