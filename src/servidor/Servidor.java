package servidor;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private ServerSocket server = null;

    private Socket socket = null;

    public Servidor() {
        try {
            server = new ServerSocket(4444);

            while((socket = server.accept()) != null) {
                new HiloServidor(socket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class HiloServidor extends Thread {

    private Socket socket = null;

    HiloServidor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());

            System.out.println(in.readUTF());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
