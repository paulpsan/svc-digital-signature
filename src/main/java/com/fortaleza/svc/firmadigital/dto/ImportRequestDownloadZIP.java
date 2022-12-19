package com.fortaleza.svc.firmadigital.dto;

import lombok.Data;

@Data
public class ImportRequestDownloadZIP {
    private String nameFolder;
    private String nameFile;

    public String getNameFolder() {
        return nameFolder;
    }

    public void setNameFolder(String nameFolder) {
        this.nameFolder = nameFolder;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }
}
