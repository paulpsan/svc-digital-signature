package com.fortaleza.svc.firmadigital.controller;

import com.fortaleza.svc.firmadigital.model.Signatory;
import com.fortaleza.svc.firmadigital.service.SignatoryService;
import com.fortaleza.svc.firmadigital.config.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
@RequestMapping("/v1/ms-firmas/signatory")
public class SignatoryController {
    @Autowired
    private SignatoryService signatoryService;

    @GetMapping
    public ResponseEntity<Object> readSignature() throws IOException {
        try {
            var data = signatoryService.readSignatory();
            if( data.stream().count() == 0 ){
                return ResponseEntity.status(HttpStatus.OK).body( new Response("No se tiene datos en la TABLA SIGNATORY") );
            }
            return ResponseEntity.status(HttpStatus.OK).body( data );
        }catch (Exception exc){
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Contactese con el Administrador") );
        }
    }

    @PostMapping
    public ResponseEntity<Object> addSignature( @RequestBody Signatory signatory ) throws IOException {
        try{
            signatoryService.insertSignatory(signatory);
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Signature "+ signatory.getId_signatory() +" agregado con exito.") );
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Contactese con el Administrador.") );
        }
    }

    @PutMapping
    public ResponseEntity<Object> updateSignature( @RequestBody Signatory signatory ) throws IOException {
        try{
            signatoryService.updateSignatory(signatory);
            return ResponseEntity.status(HttpStatus.OK).body( new Response("signatory "+ signatory.getId_signatory() +" actualizado con exito.") );
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Contactese con el Administrador.") );
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteSignature( @RequestBody Signatory signatory ) throws IOException {
        try{
            signatoryService.deleteSignatory(signatory);
            return ResponseEntity.status(HttpStatus.OK).body( new Response("signatory "+ signatory.getId_signatory() +" eliminado con exito." ) );
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Contactese con el Administrador.") );
        }
    }
}
