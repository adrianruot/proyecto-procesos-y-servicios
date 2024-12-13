package modelos;

// CLASE DE ENTIDAD EMPLEADO
public class Empleado {

    private String usuario;

    private String nombre;

    private String apellido;

    private int edad;

    private String email;

    private byte[] contrasenia;

    public Empleado(String nombre, String apellido, int edad, String email, String usuario, byte[] contrasenia) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.email = email;
        this.contrasenia = contrasenia;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(byte[] contrasenia) {
        this.contrasenia = contrasenia;
    }
}
