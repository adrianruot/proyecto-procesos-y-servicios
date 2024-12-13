package modelos;

// CLASE PARA INCIDENCIAS
public class Incidencia {

    private String infoPrec;

    private String caracteristicas;

    private String impacto;

    private String usuario;

    public Incidencia(String infoPrec, String caracteristicas, String impacto, String usuario) {
        this.infoPrec = infoPrec;
        this.caracteristicas = caracteristicas;
        this.impacto = impacto;
        this.usuario = usuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getInfoPrec() {
        return infoPrec;
    }

    public void setInfoPrec(String infoPrec) {
        this.infoPrec = infoPrec;
    }

    public String getCaracteristicas() {return caracteristicas;}

    public void setCaracteristicas(String caracteristicas) {this.caracteristicas = caracteristicas;}

    public String getImpacto() {
        return impacto;
    }

    public void setImpacto(String impacto) {
        this.impacto = impacto;
    }

    @Override
    public String toString() {
        return "Incidencia: Informacion precisa -> " + getInfoPrec() + " caracteristicas ->  " + getCaracteristicas() + " impacto -> " + getImpacto() + " usuario -> " + getUsuario();
    }
}
