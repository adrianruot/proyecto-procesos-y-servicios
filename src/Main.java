import cliente.Cliente;
import servidor.Servidor;

import java.net.Socket;
import java.util.Scanner;

public class Main {

    // AQUI ARRANCAMOS EL PROGRAMA
    public static void main(String[] args) {
        System.out.println(GLOBALES.cartelBienvenida);

        int i = -1;

        Scanner scanner = new Scanner(System.in);

        while(i != 0) {
            System.out.print(GLOBALES.cursor);
            i = scanner.nextInt();
            switch (i) {
                case 1: {
                    new Servidor();


                    break;
                }
                case 2: {
                    new Cliente().mandarMensaje("hola ?");


                    break;
                }
                default: {
                    System.out.println("Por favor recuerde escoger o la opcion 1: Arrancar como servidor, o la opción 2: Arrancar como cliente");
                }
            }
        }

    }
}