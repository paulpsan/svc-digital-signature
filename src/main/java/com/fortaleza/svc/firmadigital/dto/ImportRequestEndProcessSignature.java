package com.fortaleza.svc.firmadigital.dto;

import lombok.Data;

@Data
public class ImportRequestEndProcessSignature {
    private String name_zip;
    private String name_folder_user;
    private String folder_final;

    public String getName_zip() {
        return name_zip;
    }

    public void setName_zip(String name_zip) {
        this.name_zip = name_zip;
    }

    public String getName_folder_user() {
        return name_folder_user;
    }

    public void setName_folder_user(String name_folder_user) {
        this.name_folder_user = name_folder_user;
    }

    public String getFolder_final() {
        return folder_final;
    }

    public void setFolder_final(String folder_final) {
        this.folder_final = folder_final;
    }
}
