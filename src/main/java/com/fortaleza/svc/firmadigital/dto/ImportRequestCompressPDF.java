package com.fortaleza.svc.firmadigital.dto;

import lombok.Data;

@Data
public class ImportRequestCompressPDF {
    private String name_zip;
    private String folder_final;

    public String getName_zip() {
        return name_zip;
    }

    public void setName_zip(String name_zip) {
        this.name_zip = name_zip;
    }

    public String getFolder_final() {
        return folder_final;
    }

    public void setFolder_final(String folder_final) {
        this.folder_final = folder_final;
    }
}
