package cifrado;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Contrasenia {

    public static byte[] cifrarContra(String plaintext) {
        byte[] digest = null;
        try {
            MessageDigest m = MessageDigest.getInstance("SHA-256");
            m.update(plaintext.getBytes());
            digest = m.digest();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("No se ha encontrado la implementación del algoritmo MD5 en ningún Provider");
        }

        return digest;
    }

    static String toHexadecimal(byte[] hash) {
        String hex = "";
        for (int i = 0; i < hash.length; i++) {
            String h = Integer.toHexString(hash[i] & 0xFF);
            if (h.length() == 1) {
                hex += "0";
            }
            hex += h;
        }
        return hex.toUpperCase();
    }

}
