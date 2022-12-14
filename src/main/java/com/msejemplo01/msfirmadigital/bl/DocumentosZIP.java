package com.msejemplo01.msfirmadigital.bl;

import java.util.Date;

public class DocumentosZIP {
    String nombreDoc;
    String extDoc;
    String fecha;
    String size_file;

    public DocumentosZIP(String nombreDoc, String extDoc, String fecha, String size_file) {
        this.nombreDoc = nombreDoc;
        this.extDoc = extDoc;
        this.fecha = fecha;
        this.size_file = size_file;
    }

    public String getNombreDoc() {
        return nombreDoc;
    }

    public void setNombreDoc(String nombreDoc) {
        this.nombreDoc = nombreDoc;
    }

    public String getExtDoc() {
        return extDoc;
    }

    public void setExtDoc(String extDoc) {
        this.extDoc = extDoc;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getSize_file() {
        return size_file;
    }

    public void setSize_file(String size_file) {
        this.size_file = size_file;
    }
}
