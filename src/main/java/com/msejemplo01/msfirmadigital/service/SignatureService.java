package com.msejemplo01.msfirmadigital.service;

import com.msejemplo01.msfirmadigital.model.Signature;
import com.msejemplo01.msfirmadigital.repo.SignatureRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SignatureService {

    @Autowired
    private SignatureRepo signatureRepo;

    public Signature insertSignature(Signature signature){
        return signatureRepo.save(signature);
    }

    public List<Signature> readSignature(){
        return signatureRepo.findAll();
    }

    public Signature updateSignature(Signature signature){
        return signatureRepo.save(signature);
    }

    public void deleteSignature(Signature signature){
        signatureRepo.delete(signature);
    }

}
