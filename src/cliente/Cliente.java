package cliente;

import cifrado.CifradoAsimetrico;
import global.GLOBALES;
import modelos.Empleado;
import servidor.Servidor;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Cliente {

    private SSLSocket socket = null;

    // PARA ENTRADA DE DATOS.
    private Scanner scanner = new Scanner(System.in);

    // CLAVES PUBLICA Y PRIVADA PARA USAR DESPUES
    private PrivateKey privateKey = null;

    private PublicKey publicKey = null;

    // PARA SABER SI HEMOS INICIADO SESION
    private int iniciadoSesion = 0;

    // NOMBRE DE USUARIO Y NOMBRE ESCOGIDO POR USUARIO
    private String usuarioInicio = null;

    private String nombreInicio = null;

    public Cliente() {
        try {
            //CERTIFICADOS PARA LA CONEXION SSL
            System.setProperty("javax.net.ssl.trustStore", "src/certificados/UsuarioAlmacenSSL.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "7654321");

            // NUESTRAS CLAVES PUBLICAS Y PRIVADAS.
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");

            KeyPair keyPair = keyPairGenerator.genKeyPair();

            privateKey = keyPair.getPrivate();

            publicKey = keyPair.getPublic();

            System.out.println("Conectando como cliente\nEscribe una direccion IP: ");

            String ip = scanner.next();

            this.socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(ip, 4444);

            inicioORegistroSesion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // METODO PARA FIRMAR UN CONTEXTO
    private byte[] firmarCaracteristicas(String caracteristicas) {
        try {
            Signature dsa = Signature.getInstance("SHA256withDSA");

            dsa.initSign(privateKey);

            dsa.update(caracteristicas.getBytes());

            return dsa.sign();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Fallo en la firma");
        }
        return null;
    }

    // PARA PODER IMPRIMIR POR PANTALLA (SE ME OCURRIO MAS TARDE)
    private String prompt(String mensaje) {
        System.out.println("\n" + mensaje + "\n");
        System.out.print(GLOBALES.cursor);
        String resul = scanner.nextLine().trim();
        return resul;
    }

    // METODO PARA QUE EN CASO DE QUE HAYA INICIADO SESION EL USUARIO ENTRAR
    private void iniciadoSesion() {
        int i = -1;

        // LOOP WHILE PARA PODER PREGUNTAR AL USUARIO QUE ACCION QUIERE HACER
        while(i != 0) {
            System.out.println("Bienvenid@ " + nombreInicio + "\n1: Crear incidencia\n2: Ver mis incidencias\n3: Salir\n");
            System.out.print(GLOBALES.cursor);
            i = scanner.nextInt();
            switch (i) {
                case 1: { // EN CASO DE QUE QUIERA CREAR UNA INCIDENCIA
                    try {
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                        out.writeInt(3); // QUEREMOS ENTRAR EN EL MODO DE CREAR INCIDENCIA

                        System.out.println("\nInformacion precisa de la incidencia: \n");
                        System.out.print(GLOBALES.cursor);

                        String infoPrec = scanner.nextLine().trim();

                        scanner.nextLine();

                        String caracteristicas = prompt("Caracteristicas: ");

                        byte[] bytesCarac = firmarCaracteristicas(caracteristicas);

                        //String impacto = prompt("Impacto (Leve, Moderada, Urgente): ");

                        int check = 0;
                        String impacto = null;

                        // EN ESTE WHILE COMPROBAMOS LOS PARAMETROS INTRODUCIDOS SON CORRECTOS.
                        while(check == 0) {
                            impacto = prompt("Impacto (Leve, Moderada, Urgente): ");
                            if(impacto.toLowerCase().equals("leve") || impacto.toLowerCase().equals("moderada") || impacto.toLowerCase().equals("urgente")) {
                                check = 1;
                            }
                        }


                        out.writeUTF(infoPrec);

                        out.writeInt(bytesCarac.length);

                        out.write(bytesCarac);

                        out.writeUTF(caracteristicas);

                        out.writeUTF(impacto.trim().toLowerCase());

                        out.writeUTF(usuarioInicio);

                        ObjectOutputStream outObj = new ObjectOutputStream(socket.getOutputStream());

                        outObj.writeObject(this.publicKey);
                    } catch(Exception e) {
                        System.out.println("Fallo en la incidencia");
                    }
                    break;
                }
                case 2: { // EN CASO DE QUE QUIERA LEER SUS INCIDENCIAS
                    try {
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                        out.writeInt(4); // QUEREMOS CONSULTAR MIS INCIDENCIAS

                        out.writeUTF(usuarioInicio);

                        DataInputStream in = new DataInputStream(socket.getInputStream());

                        System.out.println(in.readUTF());
                    } catch(Exception e) {
                        System.out.println("Fallo en la incidencia");
                    }

                    break;
                }
                case 3: { // EN CASO DE QUE QUIERA CERRAR SESION
                    i = 0;
                    iniciadoSesion = 0;
                    usuarioInicio = null;
                    nombreInicio = null;
                    break;
                }
                default: { // EN CASO DE QUE NO HAYA ESCOGIDO UN VALOR CORRECTO
                    System.out.println("Por favor recuerde escoger o la opcion 1: Crear incidencia, 2: consultar mis incidencias, o la opción 3: Salir\n");
                }
            }
        }
    }

    // METODO PARA SABER SI QUIERE INICIAR SESION O REGISTRARSE
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

    // METODO PARA INICIAR SESION MANDANDO DATOS AL SERVIDOR
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

    // METODO PARA REGISTRARNOS MANDANDO LOS DATOS AL SERVIDOR
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
                scanner.nextLine();

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
