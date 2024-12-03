package modelos;

public class Incidencia {

    private String infoPrec;

    private String comunicado;

    private String impacto;

    public Incidencia(String infoPrec, String comunicado, String impacto) {
        this.infoPrec = infoPrec;
        this.comunicado = comunicado;
        this.impacto = impacto;
    }


    public String getInfoPrec() {
        return infoPrec;
    }

    public void setInfoPrec(String infoPrec) {
        this.infoPrec = infoPrec;
    }

    public String getComunicado() {
        return comunicado;
    }

    public void setComunicado(String comunicado) {
        this.comunicado = comunicado;
    }

    public String getImpacto() {
        return impacto;
    }

    public void setImpacto(String impacto) {
        this.impacto = impacto;
    }
}
