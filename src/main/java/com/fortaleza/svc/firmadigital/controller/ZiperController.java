package com.fortaleza.svc.firmadigital.controller;

import com.fortaleza.svc.firmadigital.bl.Ziper;
import com.fortaleza.svc.firmadigital.config.Response;
import com.fortaleza.svc.firmadigital.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("/v1/ms-firmas")
public class ZiperController {
    private static Logger LOGGER = LoggerFactory.getLogger(ZiperController.class);
    @Autowired
    private Ziper ziper;
    @RequestMapping(value ="/extract_files", method = RequestMethod.POST)
    public ResponseEntity<Object> extract_files(@RequestParam("files") MultipartFile files, @RequestParam("name_user") String name_user) throws IOException{
        try {
            boolean state = ziper.extract_zip(files, name_user);
            if (!state){
                System.out.println("ARCHIVOS DESCOMPRIMIDOS");
                LOGGER.info("BAD-REQUEST: La solicitud extract_files no se ejecuto con exito");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("EL archivo seleccionado no es un ZIP"));
            }else{
                LOGGER.info("SUCCESS-REQUEST: La solicitud extract_files se ejecuto con exito");
                return ResponseEntity.status(HttpStatus.OK).body(new Response("Archivo ZIP descomprimido con Exito!!!"));
            }
        }catch (Error error){
            LOGGER.info("ERROR-REQUEST: La solicitud extract_files no se ejecuto");
            LOGGER.info(String.valueOf(error));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Error al Prcoesar el archivo contacte con el Administrador"));
        }
    }

    @RequestMapping(value = "/files_pdf_signed{carpetaFinal}{name_user}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> get_files_pdf_signed(@RequestParam int carpetaFinal, @RequestParam String name_user) throws Exception{
        try{
            System.out.println("PDF FIRMADOS LISTADOS");
            LOGGER.info("SUCCESS-REQUEST: La solicitud files_pdf_signed se ejecuto con exito");
            return ResponseEntity.status(HttpStatus.OK).body(ziper.listPDF_signed( carpetaFinal, name_user ));
        }catch (Error error){
            LOGGER.info("BAD-REQUEST: La solicitud files_pdf_signed se ejecuto con exito");
            LOGGER.info(String.valueOf(error));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Error contacte con el Administrador"));
        }
    }

    @RequestMapping(value="/get_files_pdf{name_user}", method = RequestMethod.GET)
    public  ResponseEntity<Object> get_files_pdf(@RequestParam String name_user) throws IOException{
        try {
            var data = ziper.list_doc_pdf(name_user);
            System.out.println("PDF LISTADOS");
            LOGGER.info("SUCCESS-REQUEST: La solicitud get_files_pdf se ejecuto con exito");
            return ResponseEntity.status(HttpStatus.OK).body( data );
        }catch (Error error){
            LOGGER.info("BAD-REQUEST: La solicitud get_files_pdf no se ejecuto ");
            LOGGER.info(String.valueOf(error));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Error contacte con el Administrador"));
        }
    }

    @RequestMapping(value = "/signed_files_pdf", method = RequestMethod.POST)
    public ResponseEntity<Object> signed_files_pdf(
            @RequestParam("name_user") String name_user,
            @RequestParam("contraseña") String clavePrivada,
            @RequestParam("cargo") String cargo,
            @RequestParam("firmante") String firmante,
            @RequestParam("certificado") MultipartFile file,
            @RequestParam("x") int x,
            @RequestParam("y") int y,
            @RequestParam("firma") int firma) throws Exception{
        if ( clavePrivada.equals("") ){
            LOGGER.info("INTERNAL_SERVER_ERROR: La solicitud signed_files_pdf no se ejecuto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("error");
        }
        System.out.println("Archivos Firmados" + firma);
        LOGGER.info("SUCCESS-REQUEST: La solicitud signed_files_pdf se ejecuto con exito");
        ziper.signaturePDF(name_user, clavePrivada, cargo, firmante, file, x, y, firma);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response("ok"));
    }

    @RequestMapping(value = "/certificate_verification", method = RequestMethod.POST)
    public ResponseEntity<Object> certificate_verification(
            @RequestParam String password,
            @RequestParam("files") MultipartFile files
    ) throws NoSuchAlgorithmException {
        try {
            if(password.equals("")){
                LOGGER.info("BAD-REQUEST: La solicitud certificate_verification No recibio la contraseña");
                return ResponseEntity.status(HttpStatus.OK).body("Password NONE");
            }
            var data = ziper.certificate_verification(password, files);
            if(!data){
                LOGGER.info("SUCCESS-REQUEST: La solicitud certificate_verification muestra contraseña es incorrecta");
                return ResponseEntity.status(HttpStatus.OK).body("Contraseña Incorrecta");
            }else{
                LOGGER.info("SUCCESS-REQUEST: La solicitud certificate_verification es contraseña correcta");
                return ResponseEntity.status(HttpStatus.OK).body("Contraseña Verificada");
            }
        }catch (Error error){
            LOGGER.info("BAD-REQUEST: La solicitud files_pdf_signed no se ejecuto con exito");
            LOGGER.info(String.valueOf(error));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Error contacte con el Administrador"));
        } catch (InvalidAlgorithmParameterException e) {
            LOGGER.info("BAD-REQUEST: La solicitud files_pdf_signed no se ejecuto.");
            LOGGER.info(String.valueOf(e));
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/compress_files_pdf", method = RequestMethod.POST)
    public ResponseEntity<Object> compress_files_pdf(
            @RequestBody ImportRequestCompressPDF compressPDF) throws IOException {
        try{
            var data = ziper.compress_files_pdf( compressPDF.getName_zip(), compressPDF.getFolder_final(), compressPDF.getName_user());
            System.out.println("Archivos Comprimidos");
            File file = new File(data);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition",
                    String.format("attachment; filename=\"%s\"", file.getName()));
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/zip")).body(resource);
            LOGGER.info("SUCCESS-REQUEST: La solicitud compress_files_pdf se ejecuto con exito.");
            return responseEntity;
        }catch (Error error){
            LOGGER.info("BAD-REQUEST: La solicitud compress_files_pdf no se ejecuto.");
            LOGGER.info(String.valueOf(error));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("error"));
        }
    }

    @RequestMapping(value = "/download_zip_signature", method = RequestMethod.POST)
    public ResponseEntity<Object> download_zip_signature(
            @RequestBody ImportRequestDownloadZIP importRequestDownloadZIP
            ) throws IOException {
        try{
            var dataDowload = ziper.downloadZIP(importRequestDownloadZIP.getNameFolder(), importRequestDownloadZIP.getNameFile());
            System.out.println("Archivos Encotrados....");
            File file = new File(dataDowload);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition",
                    String.format("attachment; filename=\"%s\"", file.getName()));
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/zip")).body(resource);
            LOGGER.info("SUCCESS-REQUEST: La solicitud download_zip_signature se ejecuto con exito.");
            return responseEntity;
        }catch (Error error){
            LOGGER.info("BAD-REQUEST: La solicitud download_zip_signature no se ejecuto.");
            LOGGER.info(String.valueOf(error));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("error"));
        }
    }

    @RequestMapping(value = "/end_process_signature", method = RequestMethod.POST)
    public ResponseEntity<Object> end_process_signature(@RequestBody ImportRequestEndProcessSignature importRequestEndProcessSignature){
        try {
            ziper.end_process_signature(importRequestEndProcessSignature.getName_zip(),
                                        importRequestEndProcessSignature.getName_folder_user(),
                                        importRequestEndProcessSignature.getFolder_final());
            ziper.endProcess( importRequestEndProcessSignature.getName_folder_user() );
            LOGGER.info("SUCCESS-REQUEST: La solicitud end_process_signature se ejecuto con exito.");
            System.out.println("Proceso Terminado");
            return ResponseEntity.status(HttpStatus.OK).body(new Response("Proceso de Firma Digital Terminado"));
        } catch (IOException e) {
            LOGGER.info("BAD-REQUEST: La solicitud end_process_signature no se ejecuto.");
            LOGGER.info(String.valueOf(e));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Proceso de Firma Digital Interrumpido"));
        }
    }

    @RequestMapping(value = "/cancel_process_signature{name_user}", method = RequestMethod.POST)
    public ResponseEntity<Object> cancel_process_signature(@RequestParam String name_user){
        try {
            ziper.endProcess(name_user);
            LOGGER.info("SUCCESS-REQUEST: La solicitud cancel_process_signature se ejecuto con exito.");
            System.out.println("Proceso Cancelado.");
            return ResponseEntity.status(HttpStatus.OK).body(new Response("Proceso de Firma Digital Cancelado."));
        } catch (IOException e) {
            LOGGER.info("BAD-REQUEST: La solicitud cancel_process_signature no se ejecuto.");
            LOGGER.info(String.valueOf(e));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Proceso de Firma Digital Interrumpido"));
        }
    }

    @RequestMapping(value = "/get_zip_signature{nameFolder}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> get_zip_signature(@RequestParam String nameFolder) {
        try {
            var data = ziper.list_zip(nameFolder);
            System.out.println("ZIP LISTADOS");
            LOGGER.info("SUCCESS-REQUEST: La solicitud zip_signature se ejecuto con exito.");
            return ResponseEntity.status(HttpStatus.OK).body( data );
        }catch (Error error){
            LOGGER.info("BAD-REQUEST: La solicitud zip_signature no se ejecuto.");
            LOGGER.info(String.valueOf(error));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Error contacte con el Administrador"));
        }
    }
}
