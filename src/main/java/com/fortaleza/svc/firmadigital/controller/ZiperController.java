package com.fortaleza.svc.firmadigital.controller;

import com.fortaleza.svc.firmadigital.bl.Ziper;
import com.fortaleza.svc.firmadigital.config.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("/v1/ms-firmas")
public class ZiperController {
    @Autowired
    private Ziper ziper;
    @RequestMapping(value ="/extract_files", method = RequestMethod.POST)
    public ResponseEntity<Object> upFile(@RequestParam("files") MultipartFile files ) throws IOException{
        try {
            boolean state = ziper.extrac_zip(files);
            if (!state){
                System.out.println("ARCHIVOS DESCOMPRIMIDOS");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("EL archivo seleccionado no es un ZIP"));
            }else{
                return ResponseEntity.status(HttpStatus.OK).body(new Response("Archivo ZIP descomprimido con Exito!!!"));
            }
        }catch (Error error){
            System.out.println("Error al extraer archivos -> " + error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Error al Prcoesar el archivo contacte con el Administrador"));
        }
    }

    @RequestMapping(value="/get_files_pdf", method = RequestMethod.GET)
    public  ResponseEntity<Object> list_word() throws IOException{
        try {
            var data = ziper.list_doc_pdf();
            System.out.println("PDF LISTADOS");
            return ResponseEntity.status(HttpStatus.OK).body( data );
        }catch (Error error){
            System.out.println("Lista de Archivos Error->"+error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Error contacte con el Administrador"));
        }
    }

    @RequestMapping(value = "/files_pdf_signed", method = RequestMethod.POST)
    public ResponseEntity<Object> convertPdf(@RequestParam("carpetaFinal") int carpetaFinal) throws Exception{
        try{
            System.out.println("PDF FIRMADOS LISTADOS");
            return ResponseEntity.status(HttpStatus.OK).body(ziper.listPDF_signed(carpetaFinal));
        }catch (Error error){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Error contacte con el Administrador"));
        }
    }

    @RequestMapping(value = "/signed_files_pdf", method = RequestMethod.POST)
    public ResponseEntity<Object> firma_PDF(
            @RequestParam("contraseña") String clavePrivada,
            @RequestParam("firmante") String firmante,
            @RequestParam("certificado") MultipartFile file,
            @RequestParam("x") int x,
            @RequestParam("y") int y,
            @RequestParam("firma") int firma) throws Exception{
        if ( clavePrivada.equals("") ){
            System.out.println("Error Al Firmar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("error");
        }
        System.out.println("Archivos Firmados" + firma);
        ziper.firmaPDF(clavePrivada, firmante, file, x, y, firma);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response("ok"));
    }

    @RequestMapping(value = "/certificate_verification", method = RequestMethod.POST)
    public ResponseEntity<Object> certificate_verification(
            @RequestParam String password,
            @RequestParam("files") MultipartFile files
    ) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        try {
            if(password.equals("")){
                return ResponseEntity.status(HttpStatus.OK).body("Password NONE");
            }
            var data = ziper.certificate_verification(password, files);
            if(!data){
                return ResponseEntity.status(HttpStatus.OK).body("Contraseña Incorrecta");
            }else{
                return ResponseEntity.status(HttpStatus.OK).body("Contraseña Verificada");
            }
        }catch (Error error){
            System.out.println("Verifiacaion-->"+error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Error contacte con el Administrador"));
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/compress_files_pdf", method = RequestMethod.POST)
    public ResponseEntity<Object> compress_files_pdf(
            @RequestBody String name_zip,
            @RequestBody String folder_final) throws IOException {
        try{
            var data = ziper.compress_files_pdf( name_zip, folder_final);
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
            return responseEntity;
        }catch (Error error){
            System.out.println("Verifiacaion-->"+error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("error"));
        }
    }

    @RequestMapping(value = "/dowload_file")
    public ResponseEntity<Object> dowload_file(
            @RequestParam String nameFolder,
            @RequestParam String nameFile
    ) throws IOException {
        try{
            var dataDowload = ziper.dowloadZIP(nameFolder,nameFile);
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
            return responseEntity;
        }catch (Error error){
            System.out.println("Verifiacaion-->"+error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("error"));
        }
    }

    @RequestMapping(value = "/end_process")
    public ResponseEntity<Object> end_process( @RequestParam String name_zip,
                                               @RequestParam String name_folder_user,
                                               @RequestParam String folder_final){
        try {
            ziper.end_process_file(name_zip, name_folder_user, folder_final);
            ziper.endProcess();
            System.out.println("Proceso Terminado");
            return ResponseEntity.status(HttpStatus.OK).body(new Response("Proceso de Firma Digital Terminado"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Proceso de Firma Digital Interrumpido"));
        }
    }

    @RequestMapping(value = "/cancel_process")
    public ResponseEntity<Object> cancel_process(){
        try {
            ziper.endProcess();
            System.out.println("Proceso Cancelado.");
            return ResponseEntity.status(HttpStatus.OK).body(new Response("Proceso de Firma Digital Cancelado."));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Proceso de Firma Digital Interrumpido"));
        }
    }

    @RequestMapping(value = "/zip_signature")
    public ResponseEntity<Object> list_zip( @RequestParam("nameFolder") String nameFolder ) throws IOException{
        try {
            var data = ziper.list_zip(nameFolder);
            System.out.println("ZIP LISTADOS");
            return ResponseEntity.status(HttpStatus.OK).body( data );
        }catch (Error error){
            System.out.println("Lista de Archivos ZIP Error->"+error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Error contacte con el Administrador"));
        }
    }
}
