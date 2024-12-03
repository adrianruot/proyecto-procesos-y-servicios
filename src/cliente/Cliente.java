package cliente;

import global.GLOBALES;
import modelos.Empleado;
import servidor.Servidor;

import javax.net.ssl.*;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.InputMismatchException;
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

            inicioORegistroSesion();
        } catch (Exception e) {
            e.printStackTrace();
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

        Empleado empleadoNuevo = new Empleado(nombre, apellido, edad, email, usuario, contra);


    }

}
