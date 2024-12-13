package servidor;

import cifrado.CifradoAsimetrico;
import cifrado.Contrasenia;
import modelos.Empleado;
import modelos.Incidencia;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.random.RandomGenerator;

public class Servidor {

    // VARIABLE GLOBAL PARA LAS CONEXIONES DEL SERVER
    private SSLServerSocket server = null;

    // CONEXION ESTABLECIDA DEL SOCKET
    private SSLSocket socket = null;

    // ARRAYLIST DONDE ALMACENAREMOS TODOS LOS EMPLEADOS.
    public static ArrayList<Empleado> empleados = new ArrayList<>();

    // ARRAYLIST DONDE ALMACENAREMOS TODAS LAS INCIDENCIAS
    public static ArrayList<Incidencia> incidencias = new ArrayList<>();

    // CONSTRUCTOR DEL SERIVDOR.
    public Servidor() {
        try {

            // CARGAMOS LAS KEYSTORES PARA PODER USAR SSL SOCKET.
            System.setProperty("javax.net.ssl.keyStore", "src/certificados/AlmacenSSL.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "1234567");

            // CREAMOS EL SSLServerSokcet PARA LA ESCUCHA DE CLIENTES Y LUEGO MANIPULAR LA INFORMACION CON SOCKETS.
            server = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(4444);

            System.out.println("Servidor arrancado en IP: " + server.getInetAddress().getHostAddress());

            // LOOP WHILE PARA PODER CREAR UN NUEVO HILO CADA VEZ QUE VIENE UNA PETICION
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

    // ALMACENAMOS DICHO SOCKET LA CONEXION YA ESTABLECIDA.
    private SSLSocket socket = null;

    HiloServidor(SSLSocket socket) {
        this.socket = socket;
    }

    // EJECUCION DEL HILO
    @Override
    public void run() {
        try {
            // CREAMOS LAS CLAVES PARA LUEGO USARLAS.
            KeyPair keyPair = CifradoAsimetrico.generarParClaves();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // CREAMOS LA CONEXION DE ENTRADA
            DataInputStream in = new DataInputStream(socket.getInputStream());

            // ALMACENAMOS UN VALOR NUMERICO QUE NOS MANDARA EL CLIENTE PARA SABER QUE ACCION QUIERE TOMAR
            int valor = in.readInt();

            // WHILE PARA SABER QUE QUIERE HACER EL CLIENTE HASTA QUE SE CIERRE SU SESION.
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
                                        && Arrays.equals(Servidor.empleados.get(i).getContrasenia(), Contrasenia.cifrarContra(inicSesion[1]))) {
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
                                    Integer.parseInt(datosEmplSep[2]), datosEmplSep[3], datosEmplSep[4], Contrasenia.cifrarContra(datosEmplSep[5])));
                        }

                        valor = in.readInt();// RECOJEMOS EL NUEVO VALOR DEL USUARIO PARA SABER QUE QUIERE HACER.
                        break;
                    }
                    case 3: { // CREAR INCIDENCIA

                        String infoPrec = in.readUTF();

                        int tamBuf = in.readInt();
                        byte[] incidenciaNuevaInfo = null;
                        if(tamBuf > 0) {
                            incidenciaNuevaInfo = new byte[tamBuf];

                            in.readFully(incidenciaNuevaInfo, 0, tamBuf);

                            System.out.println(infoPrec + " " + incidenciaNuevaInfo.toString());
                        }

                        String caracteristicas = in.readUTF();

                        String impacto = in.readUTF();

                        String usuario = in.readUTF();

                        ObjectInputStream inObj = new ObjectInputStream(socket.getInputStream());

                        PublicKey clavePub = (PublicKey) inObj.readObject();

                        Signature verificadoDSA = Signature.getInstance("SHA256withDSA");
                        verificadoDSA.initVerify(clavePub);
                        verificadoDSA.update(caracteristicas.getBytes());
                        boolean check = verificadoDSA.verify(incidenciaNuevaInfo);

                        if(check) {
                            System.out.printf("Firmado correctamente: " + caracteristicas);
                            Servidor.incidencias.add(new Incidencia(infoPrec, caracteristicas, impacto, usuario));
                            for(int i = 0; i < Servidor.incidencias.size(); i++) {
                                System.out.println(Servidor.incidencias.get(i).toString());
                            }
                        } else {
                            System.out.println("No esta firmado correctamente. Se aborta el guardado.");
                        }
                        valor = in.readInt();
                        break;
                    }
                    case 4: { // CONSULTA DE INCIDENCIAS
                        String usuario = in.readUTF();

                        String resultado = "";

                        for(int i = 0; i < Servidor.incidencias.size(); i++) {
                            if(Servidor.incidencias.get(i).getUsuario().equals(usuario)) {
                                switch (Servidor.incidencias.get(i).getImpacto()) {
                                    case "leve": {
                                        RandomGenerator random = RandomGenerator.of("Random");
                                        resultado = resultado + "\n la incidencia: "
                                                + Servidor.incidencias.get(i).getCaracteristicas()
                                                + " tiene una gravedad de " + random.nextInt(1, 20);
                                        break;
                                    }

                                    case "moderada": {
                                        RandomGenerator random = RandomGenerator.of("Random");
                                        resultado = resultado + "\n la incidencia: "
                                                + Servidor.incidencias.get(i).getCaracteristicas()
                                                + " tiene una gravedad de " + random.nextInt(30, 50);
                                        break;
                                    }

                                    case "urgente": {
                                        RandomGenerator random = RandomGenerator.of("Random");
                                        resultado = resultado + "\n la incidencia: "
                                                + Servidor.incidencias.get(i).getCaracteristicas()
                                                + " tiene una gravedad de " + random.nextInt(50, 75);
                                        break;
                                    }
                                }
                            }

                        }

                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                        out.writeUTF(resultado);

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
