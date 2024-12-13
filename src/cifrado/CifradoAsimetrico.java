package cifrado;

import modelos.Empleado;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

// CLASE CON DOS METODOS ESTATICOS PARA SABER QUE ACCION QUIERE HACER SEGUN LA LOGICA DE CIFRADO ASIMETRICO.
public class CifradoAsimetrico {

    public static KeyPair generarParClaves() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        keyPairGenerator.initialize(2048);

        return keyPairGenerator.genKeyPair();
    }

    public static byte[] cifrarConClavePublica(String msg, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");

        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(msg.getBytes());
    }

    public static String descifrarConClavePrivada(byte[] msgCifrado, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");

        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return new String(cipher.doFinal(msgCifrado));
    }

}
