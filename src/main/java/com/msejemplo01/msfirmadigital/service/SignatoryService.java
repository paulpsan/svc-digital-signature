package com.msejemplo01.msfirmadigital.service;

import com.msejemplo01.msfirmadigital.model.Signatory;
import com.msejemplo01.msfirmadigital.repo.SignatoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SignatoryService {
    @Autowired
    private SignatoryRepo signatoryRepo;

    public Signatory insertSignatory(Signatory signatory){
        return signatoryRepo.save(signatory);
    }

    public List<Signatory> readSignatory(){
        return signatoryRepo.findAll();
    }

    public Signatory updateSignatory(Signatory signatory){
        return signatoryRepo.save(signatory);
    }

    public void deleteSignatory(Signatory signatory){
        signatoryRepo.delete(signatory);
    }

}
