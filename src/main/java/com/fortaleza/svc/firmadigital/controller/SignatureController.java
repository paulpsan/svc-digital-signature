package com.fortaleza.svc.firmadigital.controller;

import com.fortaleza.svc.firmadigital.model.Signature;
import com.fortaleza.svc.firmadigital.config.Response;
import com.fortaleza.svc.firmadigital.service.SignatureService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger LOGGER = LoggerFactory.getLogger(SignatureController.class);
    @Autowired
    private SignatureService signatureService;

    @GetMapping
    public ResponseEntity<Object> readSignature() {
        try {
            var data = signatureService.readSignature();
            LOGGER.info("SUCCESS-REQUEST: La solicitud SignatureController GetMapping se ejecuto con exito");
            if( data.stream().count() == 0 ){
                LOGGER.info("SUCCESS-REQUEST: La solicitud SignatureController GetMapping se ejecuto con exito pero no se tien datos");
                return ResponseEntity.status(HttpStatus.OK).body( new Response("No se tiene datos en la TABLA SIGNATURE") );
            }
            return ResponseEntity.status(HttpStatus.OK).body( data );
        }catch (Exception exc){
            LOGGER.info("BAD-REQUEST: La solicitud SignatureController GetMapping no se ejecuto con exito");
            LOGGER.info(String.valueOf(exc));
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Contactese con el Administrador") );
        }
    }

    @PostMapping
    public ResponseEntity<Object> addSinature( @RequestBody Signature signature ) {
        try{
            signatureService.insertSignature(signature);
            var id_signature = signature.getId_signature_process();
            LOGGER.info("SUCCESS-REQUEST: La solicitud SignatureController PostMapping se ejecuto con exito");
            return ResponseEntity.status(HttpStatus.OK).body( id_signature );
        }catch (Exception ex){
            LOGGER.info("BAD-REQUEST: La solicitud SignatureController PostMapping no se ejecuto con exito");
            LOGGER.info(String.valueOf(ex));
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Contactese con el Administrador.") );
        }
    }

    @PutMapping
    public ResponseEntity<Object> updateSinature( @RequestBody Signature signature ) {
        try{
            signatureService.updateSignature(signature);
            LOGGER.info("SUCCESS-REQUEST: La solicitud SignatureController PutMapping  se ejecuto con exito");
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Signature "+ signature.getId_signature_process() +" actualizado con exito.") );
        }catch (Exception ex){
            LOGGER.info("BAD-REQUEST: La solicitud SignatureController PutMapping no se ejecuto con exito");
            LOGGER.info(String.valueOf(ex));
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Contactese con el Administrador.") );
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteSinature( @RequestBody Signature signature ) {
        try{
            signatureService.deleteSignature(signature);
            LOGGER.info("SUCCESS-REQUEST: La solicitud SignatureController DeleteMapping  se ejecuto con exito");
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Signature "+ signature.getId_signature_process() +" eliminado con exito." ) );
        }catch (Exception ex){
            LOGGER.info("BAD-REQUEST: La solicitud SignatureController DeleteMapping no se ejecuto con exito");
            LOGGER.info(String.valueOf(ex));
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Contactese con el Administrador.") );
        }
    }
}
