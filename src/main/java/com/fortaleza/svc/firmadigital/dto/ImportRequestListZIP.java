package com.fortaleza.svc.firmadigital.dto;

import lombok.Data;

@Data
public class ImportRequestListZIP {
    private String nameFolder;

    public String getNameFolder() {
        return nameFolder;
    }

    public void setNameFolder(String nameFolder) {
        this.nameFolder = nameFolder;
    }
}
