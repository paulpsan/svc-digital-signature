package com.msejemplo01.msfirmadigital.bl;

public class Documentos {
    String nombreDoc;
    String rutaDoc;
    String extDoc;
    String base64_pdf;

    public Documentos(String name, String path, String extDoc, String base64_pdf){
        this.nombreDoc = name;
        this.rutaDoc = path;
        this.extDoc = extDoc;
        this.base64_pdf = base64_pdf;
    }
    public String getNombreDoc() {
        return nombreDoc;
    }

    public void setNombreDoc(String nombreDoc) {
        this.nombreDoc = nombreDoc;
    }

    public String getRutaDoc() {
        return rutaDoc;
    }

    public void setRutaDoc(String rutaDoc) {
        this.rutaDoc = rutaDoc;
    }

    public String getExtDoc() {
        return extDoc;
    }

    public void setExtDoc(String extDoc) {
        this.extDoc = extDoc;
    }
    public String getBase64_pdf() {
        return base64_pdf;
    }

    public void setBase64_pdf(String base64_pdf) {
        this.base64_pdf = base64_pdf;
    }
}
