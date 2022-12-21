package com.fortaleza.svc.firmadigital.dto;

import lombok.Data;

@Data
public class ImportRequestFilesPDFSigned {
    private int carpetaFinal;

    public int getCarpetaFinal() {
        return carpetaFinal;
    }

    public void setCarpetaFinal(int carpetaFinal) {
        this.carpetaFinal = carpetaFinal;
    }
}
