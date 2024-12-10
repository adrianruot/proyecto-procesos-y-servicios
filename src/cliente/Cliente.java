package cliente;

import cifrado.CifradoAsimetrico;
import global.GLOBALES;
import modelos.Empleado;
import servidor.Servidor;

import javax.net.ssl.*;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Cliente {

    private SSLSocket socket = null;

    private Scanner scanner = new Scanner(System.in);

    private int iniciadoSesion = 0;

    private String usuarioInicio = null;

    private String nombreInicio = null;

    public Cliente() {
        try {
            System.setProperty("javax.net.ssl.trustStore", "src/certificados/UsuarioAlmacenSSL.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "7654321");

            System.out.println("Conectando como cliente\nEscribe una direccion IP: ");

            String ip = scanner.next();

            this.socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(ip, 4444);

            inicioORegistroSesion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void iniciadoSesion() {
        int i = -1;

        while(i != 0) {
            System.out.println("1: Crear incidencia\n2: Salir\n");
            System.out.print(GLOBALES.cursor);
            i = scanner.nextInt();
            switch (i) {
                case 1: {
                    System.out.print("Aqui creamos cosas.");
                    break;
                }
                case 2: {
                    i = 0;
                    iniciadoSesion = 0;
                    usuarioInicio = null;
                    nombreInicio = null;
                    break;
                }
                default: {
                    System.out.println("Por favor recuerde escoger o la opcion 1: Crear incidencia, o la opción 2: Salir\n");
                }
            }
        }
    }

    private void inicioORegistroSesion() {
        int i = -1;

        while(i != 0) {
            System.out.println("1: Iniciar sesion\n2: Registrarse\n");
            System.out.print(GLOBALES.cursor);
            i = scanner.nextInt();
            switch (i) {
                case 1: {
                    iniciarSesion();
                    if(iniciadoSesion == 1) {
                        iniciadoSesion(); // ENTRAMOS EN EL CAMPO YA UNA VEZ INICIADOS SESION
                    }
                    break;
                }
                case 2: {
                    registrarSesion();

                    break;
                }
                default: {
                    System.out.println("Por favor recuerde escoger o la opcion 1: Iniciar sesion, o la opción 2: Registrarse\n");
                }
            }
        }
    }

    private void iniciarSesion() {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeInt(1); // CON ESTO INDICAMOS QUE QUEREMOS INICAR SESION.

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            PublicKey publicKey = (PublicKey) in.readObject();

            System.out.println("Panel para iniciar sesion: \nUsuario: \n");
            System.out.print(GLOBALES.cursor);

            String usuario = scanner.next().trim().replaceAll(" ", "");

            System.out.println("\nContraseña: \n");
            System.out.print(GLOBALES.cursor);

            String contra = scanner.next().trim().replaceAll(" ", "");

            byte[] cifradoInicioSesion = CifradoAsimetrico.cifrarConClavePublica(usuario + " " + contra, publicKey);

            out.writeInt(cifradoInicioSesion.length);

            out.write(cifradoInicioSesion);

            DataInputStream inData = new DataInputStream(socket.getInputStream());

            String resul = inData.readUTF();

            if(resul.equals("Error inicio sesion")) {
                System.out.println("Error inicio sesion, vuelva a intentarlo por favor");
            } else {
                System.out.println("Se ha realizado la sesion");
                iniciadoSesion = 1;
                usuarioInicio = resul.split(" ")[1];
                nombreInicio = resul.split(" ")[0];
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void registrarSesion() {
        System.out.println("Panel para registrarse: \nNombre: \n");
        System.out.print(GLOBALES.cursor);
        String nombre = scanner.next().trim();
        System.out.println("\nApellido: \n");
        System.out.print(GLOBALES.cursor);
        String apellido = scanner.next().trim();

        int check = 0;
        int edad = 0;

        // EN ESTE WHILE COMPROBAMOS QUE EL DATO PARA EDAD SEA UN NUMERO SI O SI
        while(check == 0) {
            try {

                System.out.println("\nEdad: \n");
                System.out.print(GLOBALES.cursor);
                edad = scanner.nextInt();
                check = 1;

            } catch (InputMismatchException ex) {
                System.out.println("No se admiten caracteres en la edad por favor vuelva a intentarlo.\n");
                scanner.next();
            }
        }

        System.out.println("\nEmail: \n");
        System.out.print(GLOBALES.cursor);
        String email = scanner.next().trim();
        System.out.println("\nUsuario: \n");
        System.out.print(GLOBALES.cursor);
        String usuario = scanner.next().trim();
        System.out.println("\nContraseña: \n");
        System.out.print(GLOBALES.cursor);
        String contra = scanner.next().trim();

        nombre = nombre.replaceAll(" ", "");
        apellido = apellido.replaceAll(" ", "");
        email = email.replaceAll(" ", "");
        usuario = usuario.replaceAll(" ", "");
        contra = contra.replaceAll(" ", "");

        //Empleado empleadoNuevo = new Empleado(nombre, apellido, edad, email, usuario, contra);
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeInt(2); // PARA INDICAR AL SERVIDOR QUE QUEREMOS REGISTRARNOS
            // Y NOS DE SU CLAVE PUBLICA.

            ObjectInputStream inObj = new ObjectInputStream(socket.getInputStream());

            PublicKey publicKey = (PublicKey) inObj.readObject();

            byte[] datosEmpleadoNuevo = CifradoAsimetrico.cifrarConClavePublica(
                    nombre + " " + apellido + " " + edad + " " + email + " " + usuario + " " + contra, publicKey
            );

            // MANDAMOS TAMANIO DE EL ARRAY DE BYTES
            out.writeInt(datosEmpleadoNuevo.length);

            out.write(datosEmpleadoNuevo);
        } catch(Exception e) {
            System.out.println("Error en la conexion de datos.");
            e.printStackTrace();
        }

    }

}
