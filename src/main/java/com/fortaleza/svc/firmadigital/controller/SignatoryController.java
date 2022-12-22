package com.fortaleza.svc.firmadigital.controller;

import com.fortaleza.svc.firmadigital.model.Signatory;
import com.fortaleza.svc.firmadigital.service.SignatoryService;
import com.fortaleza.svc.firmadigital.config.Response;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
@RequestMapping("/v1/ms-firmas/signatory")
public class SignatoryController {

    private static Logger LOGGER = LoggerFactory.getLogger(SignatoryController.class);

    @Autowired
    private SignatoryService signatoryService;

    @GetMapping
    public ResponseEntity<Object> readSignature() {
        try {
            var data = signatoryService.readSignatory();
            LOGGER.info("SUCCESS-REQUEST: La solicitud SignatoryController GetMapping se ejecuto con exito");
            if( data.stream().count() == 0 ){
                LOGGER.info("SUCCESS-REQUEST: La solicitud SignatoryController GetMapping se ejecuto con exito pero no se tien datos");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body( new Response("No se tiene datos en la TABLA SIGNATORY") );
            }
            return ResponseEntity.status(HttpStatus.OK).body( data );
        }catch (Exception exc){
            LOGGER.info("BAD-REQUEST: La solicitud SignatoryController GetMapping no se ejecuto con exito");
            LOGGER.info(String.valueOf(exc));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body( new Response("Contactese con el Administrador") );
        }
    }

    @PostMapping
    public ResponseEntity<Object> addSignature( @RequestBody Signatory signatory ) {
        try{
            signatoryService.insertSignatory(signatory);
            LOGGER.info("SUCCESS-REQUEST: La solicitud SignatoryController PostMapping se ejecuto con exito");
            return ResponseEntity.status(HttpStatus.OK).body( new Response("Signature "+ signatory.getId_signatory() +" agregado con exito.") );
        }catch (Exception ex){
            LOGGER.info("BAD-REQUEST: La solicitud SignatoryController PostMapping no se ejecuto con exito");
            LOGGER.info(String.valueOf(ex));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body( new Response("Contactese con el Administrador.") );
        }
    }

    @PutMapping("{id_signature_process}")
    public ResponseEntity<Object> updateSignature( @RequestBody Signatory signatory ) {
        try{
            signatoryService.updateSignatory(signatory);
            LOGGER.info("SUCCESS-REQUEST: La solicitud SignatoryController PutMapping se ejecuto con exito");
            return ResponseEntity.status(HttpStatus.OK).body( new Response("signatory "+ signatory.getId_signatory() +" actualizado con exito.") );
        }catch (Exception ex){
            LOGGER.info("BAD-REQUEST: La solicitud SignatoryController PutMapping no se ejecuto con exito");
            LOGGER.info(String.valueOf(ex));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body( new Response("Contactese con el Administrador.") );
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteSignature( @RequestBody Signatory signatory ) {
        try{
            signatoryService.deleteSignatory(signatory);
            LOGGER.info("SUCCESS-REQUEST: La solicitud SignatoryController DeleteMapping se ejecuto con exito");
            return ResponseEntity.status(HttpStatus.OK).body( new Response("signatory "+ signatory.getId_signatory() +" eliminado con exito." ) );
        }catch (Exception ex){
            LOGGER.info("BAD-REQUEST: La solicitud SignatoryController DeleteMapping no se ejecuto con exito");
            LOGGER.info(String.valueOf(ex));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body( new Response("Contactese con el Administrador.") );
        }
    }
}
