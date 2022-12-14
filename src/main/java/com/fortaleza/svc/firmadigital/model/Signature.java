package com.fortaleza.svc.firmadigital.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "signature")
public class Signature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_signature_process;
    @Column
    private boolean state_signature_process;
    @Column
    private Date date_start_signature_process;
    @Column
    private int nro_signature;

    public int getId_signature_process() {
        return id_signature_process;
    }

    public void setId_signature_process(int id_signature_process) {
        this.id_signature_process = id_signature_process;
    }

    public boolean isState_signature_process() {
        return state_signature_process;
    }

    public void setState_signature_process(boolean state_signature_process) {
        this.state_signature_process = state_signature_process;
    }

    public Date getDate_start_signature_process() {
        return date_start_signature_process;
    }

    public void setDate_start_signature_process(Date date_start_signature_process) {
        this.date_start_signature_process = date_start_signature_process;
    }

    public int getNro_signature() {
        return nro_signature;
    }

    public void setNro_signature(int nro_signature) {
        this.nro_signature = nro_signature;
    }
}
