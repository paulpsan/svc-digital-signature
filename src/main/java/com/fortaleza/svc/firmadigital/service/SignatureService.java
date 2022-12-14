package com.fortaleza.svc.firmadigital.service;

import com.fortaleza.svc.firmadigital.model.Signature;
import com.fortaleza.svc.firmadigital.repo.SignatureRepo;
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
