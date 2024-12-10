package servidor;

import cifrado.CifradoAsimetrico;
import modelos.Empleado;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Servidor {

    private SSLServerSocket server = null;

    private SSLSocket socket = null;

    public static ArrayList<Empleado> empleados = new ArrayList<>();

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

            int valor = in.readInt();

            while(valor != -1) {
                switch(valor) { // LA LECTURA DE IN SERA LA OPCION QUE QUIERE HACER EL USUARIO.
                    case 1: { // INICIAR SESION
                        ObjectOutputStream outObj = new ObjectOutputStream(socket.getOutputStream());

                        outObj.writeObject(publicKey);

                        int tamBuf = in.readInt();
                        if(tamBuf > 0) {
                            byte[] inicioSesion = new byte[tamBuf];

                            in.readFully(inicioSesion, 0, inicioSesion.length);

                            String descInicioSesion = CifradoAsimetrico.descifrarConClavePrivada(inicioSesion, privateKey);

                            String[] inicSesion = descInicioSesion.split(" ");

                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                            int comprobar = 0;

                            for(int i = 0; i < Servidor.empleados.size(); i++) {
                                if(Servidor.empleados.get(i).getUsuario().equals(inicSesion[0])
                                        && Servidor.empleados.get(i).getContrasenia().equals(inicSesion[1])) {
                                    out.writeUTF(Servidor.empleados.get(i).getNombre() + " " + Servidor.empleados.get(i).getUsuario());
                                    comprobar = 1;
                                }
                            }
                            if(comprobar == 0) {
                                out.writeUTF("Error inicio sesion");
                            }
                        }

                        valor = in.readInt(); // RECOJEMOS EL NUEVO VALOR DEL USUARIO PARA SABER QUE QUIERE HACER.
                        break;
                    }
                    case 2: { // REGISTRARSE
                        ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
                        objOut.writeObject(publicKey);

                        int length = in.readInt();
                        if(length > 0) {
                            byte[] empleadoNuevoInfo = new byte[length];

                            in.readFully(empleadoNuevoInfo, 0, empleadoNuevoInfo.length);

                            String datosEmpleadoNuevo = CifradoAsimetrico.descifrarConClavePrivada(empleadoNuevoInfo, privateKey);

                            String[] datosEmplSep = datosEmpleadoNuevo.split(" ");

                            Servidor.empleados.add(new Empleado(datosEmplSep[0], datosEmplSep[1],
                                    Integer.parseInt(datosEmplSep[2]), datosEmplSep[3], datosEmplSep[4], datosEmplSep[5]));
                        }

                        valor = in.readInt();// RECOJEMOS EL NUEVO VALOR DEL USUARIO PARA SABER QUE QUIERE HACER.
                        break;
                    }
                    case 3: { // CREAR INCIDENCIA


                        valor = in.readInt();
                        break;
                    }
                }
            }


        } catch(Exception e) {
            System.out.println("USUARIO DESCONECTADO DEL SERVIDOR." + e.getMessage());
        }
    }

}
