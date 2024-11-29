import cliente.Cliente;
import servidor.Servidor;

import java.net.Socket;
import java.util.Scanner;

public class Main {

    // AQUI ARRANCAMOS EL PROGRAMA
    public static void main(String[] args) {
        System.out.println(GLOBALES.cartelBienvenida);
        System.out.println(GLOBALES.cursor);

        int i = 0;

        Scanner scanner = new Scanner(System.in);

        i = scanner.nextInt();

        switch (i) {
            case 0: {
                new Servidor();

                break;
            }
            case 1: {
                new Cliente();
            }
        }
    }
}