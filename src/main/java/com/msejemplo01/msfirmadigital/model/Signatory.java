package com.msejemplo01.msfirmadigital.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "signatory")
public class Signatory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_signatory;
    @Column
    private String username;
    @Column
    private boolean state_signatory;
    @Column
    private Date date_signatory;
    @Column
    private int nro_signatory;
    @Column
    private  int process_signatory;

    public int getId_signatory() {
        return id_signatory;
    }

    public void setId_signatory(int id_signatory) {
        this.id_signatory = id_signatory;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isState_signatory() {
        return state_signatory;
    }

    public void setState_signatory(boolean state_signatory) {
        this.state_signatory = state_signatory;
    }

    public Date getDate_signatory() {
        return date_signatory;
    }

    public void setDate_signatory(Date date_signatory) {
        this.date_signatory = date_signatory;
    }

    public int getNro_signatory() {
        return nro_signatory;
    }

    public void setNro_signatory(int nro_signatory) {
        this.nro_signatory = nro_signatory;
    }

    public int getProcess_signatory() {
        return process_signatory;
    }

    public void setProcess_signatory(int process_signatory) {
        this.process_signatory = process_signatory;
    }
}
