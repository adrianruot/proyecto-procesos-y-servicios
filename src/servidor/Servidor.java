package servidor;

import cifrado.CifradoAsimetrico;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

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
            KeyPair keyPair = CifradoAsimetrico.generarParClaves();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());

            switch(in.readInt()) {
                case 1: { // INICIAR SESION
                    break;
                }
                case 2: {
                    objOut.writeObject(publicKey);

                    break;
                }
            }
        } catch(Exception e) {
            System.out.println("USUARIO DESCONECTADO DEL SERVIDOR." + e.getMessage());
        }
    }

}
