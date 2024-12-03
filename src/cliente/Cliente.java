package cliente;

import javax.net.ssl.*;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    private SSLSocket socket = null;

    private Scanner scanner = new Scanner(System.in);

    public Cliente() {
        try {
            System.setProperty("javax.net.ssl.trustStore", "src/certificados/UsuarioAlmacenSSL.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "7654321");

            System.out.println("Conectando como cliente\nEscribe una direccion IP: ");

            String ip = scanner.next();

            this.socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(ip, 4444);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mandarMensaje(String mensaje) {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeUTF(mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
