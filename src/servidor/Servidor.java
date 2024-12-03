package servidor;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private SSLServerSocket server = null;

    private SSLSocket socket = null;

    public Servidor() {
        try {
            System.setProperty("javax.net.ssl.keyStore", "src/certificados/AlmacenSSL.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "1234567");

            server = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(4444);

            System.out.println("Servidor arrancado en IP: " + server.getInetAddress().getHostAddress());

            while((socket = (SSLSocket) server.accept()) != null) {
                System.out.println("Nueva conexion encontrada");
                new HiloServidor(socket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class HiloServidor extends Thread {

    private SSLSocket socket = null;

    HiloServidor(SSLSocket socket) {
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
