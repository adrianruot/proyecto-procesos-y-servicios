package cliente;

import java.io.DataOutputStream;
import java.net.Socket;

public class Cliente {

    private Socket socket = null;

    public Cliente() {
        try {
            this.socket = new Socket("localhost", 4444);
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
