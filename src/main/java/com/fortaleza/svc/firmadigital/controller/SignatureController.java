package com.fortaleza.svc.firmadigital.controller;

import com.fortaleza.svc.firmadigital.model.Signature;
import com.fortaleza.svc.firmadigital.config.Response;
import com.fortaleza.svc.firmadigital.service.SignatureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
@RequestMapping("/v1/ms-firmas/signature")
public class SignatureController {
    @Autowired
    private SignatureService signatureService;

    @GetMapping
    public ResponseEntity<Object> readSignature() throws IOException {
        try {
            var data = signatureService.readSignature();
            if( data.stream().count() == 0 ){
                return ResponseEntity.status(HttpStatus.OK).body( new Response("No se tiene datos en la TABLA SIGNATURE") );
            }
            return ResponseEntity.status(HttpStatus.OK).body( data );
        }catch (Exception exc){
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Contactese con el Administrador") );
        }
    }

    @PostMapping
    public ResponseEntity<Object> addSinature( @RequestBody Signature signature ) throws IOException {
        try{
            signatureService.insertSignature(signature);
            var id_signature = signature.getId_signature_process();
            return ResponseEntity.status(HttpStatus.OK).body( id_signature );
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Contactese con el Administrador.") );
        }
    }

    @PutMapping
    public ResponseEntity<Object> updateSinature( @RequestBody Signature signature ) throws IOException {
        try{
            signatureService.updateSignature(signature);
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Signature "+ signature.getId_signature_process() +" actualizado con exito.") );
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Contactese con el Administrador.") );
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteSinature( @RequestBody Signature signature ) throws IOException {
        try{
            signatureService.deleteSignature(signature);
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Signature "+ signature.getId_signature_process() +" eliminado con exito." ) );
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Contactese con el Administrador.") );
        }
    }
}
