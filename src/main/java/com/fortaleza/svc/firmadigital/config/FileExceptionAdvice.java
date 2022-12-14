package com.fortaleza.svc.firmadigital.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class FileExceptionAdvice {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Response> handleMaxSizeException(MaxUploadSizeExceededException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response("Verifique el tamaño de los archivos"));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception ex){
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response("Error revise la consola"));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(ex.toString()));
    }
}
