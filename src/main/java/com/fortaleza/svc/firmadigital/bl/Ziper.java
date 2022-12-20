package com.fortaleza.svc.firmadigital.bl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.awt.*;
import java.io.*;
import java.io.File;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class Ziper {
    private static Logger LOGGER = LoggerFactory.getLogger(Ziper.class);
    /**
     * Esta varable hace referencia al espaciondonde se guardaran los ZIP con los PDF firmados
     */
    @Value("${path-out-zip}")
    String path_out_zip;
    /**
     * Esta variable hace refencia al espacio donde se Almacenaran temporalmente los arhivos PDF firmados
     */
    @Value("${dir-pdf-firma}")
    String dir_pdf_firma;
    /**
     * Esta variable hace refencia al espacio donde se descomprimiran los arhivos PDF
     */
    @Value("${path-out-zip-home}")
    String path_out_zip_home;
    /**
     * Esta variable hace refencia al tipo de acceso que se tiene segun el sistema oretaivo donde se almacenaran los registros
     * (\\) WINDOWS
     * (/) LINUX
     */
    @Value("${path-system}")
    String path_system;
    /**
     * Este método obtiene una lista de todos los archivos de la ruta especificada por tipo de archivo(.ext)
     * @param dir Ruta del folder a listar
     * @param typeFile Tipo de Extension a listar
     * @return arrayDocumentos retorna un array de archivos con la clase Documentos.
     * @throws IOException
     */
    private ArrayList<Documentos> DocumentArray(String dir, String typeFile) throws IOException {
        LOGGER.info("parametro dir methodo(DocumentArray) {}", dir);
        LOGGER.info("parametro typeFile methodo(DocumentArray) {}", typeFile);
        ArrayList<Documentos> arrayDocumentos = new ArrayList<>();
        java.io.File carpeta = new java.io.File(dir);
        LOGGER.debug("Obteniendo la direccion del folder {}", dir);
        java.io.File[] lista = carpeta.listFiles();
        String[] data;
        byte[] content;
        String encode;
        for (java.io.File it : lista) {
            if (it.isFile()) {
                data = it.getName().split("\\.");
                if (typeFile.equals(data[1])) {
                    LOGGER.debug("Obteniendo la typeFile del doc {}", typeFile);
                    content = Files.readAllBytes(it.toPath());
                    encode = Base64.encodeBase64String(content);
                    arrayDocumentos.add(new Documentos(data[0], it.getPath(), data[1], encode));
                }
            }
        }
        return arrayDocumentos;
    }
    /**
     * Este método obtiene una lista de todos los archivos de la ruta especificada por tipo de archivo(.ext)
     * @param dir Ruta del folder a listar
     * @param typeFile Tipo de Extension a listar
     * @return arrayZIP retorna un array de archivos zip con la clase DocumentosZIP.
     */
    private ArrayList<DocumentosZIP> ZIPArray(String dir, String typeFile){
        LOGGER.info("parametro dir methodo(ZIPArray) {}", dir);
        LOGGER.info("parametro typeFile methodo(ZIPArray) {}", typeFile);
        ArrayList<DocumentosZIP> arrayZIP = new ArrayList<>();
        java.io.File carpeta = new java.io.File(dir);
        java.io.File[] lista = carpeta.listFiles();
        String[] data;
        for (java.io.File it : lista) {
            if (it.isFile()) {
                data = it.getName().split("\\.");
                if (typeFile.equals(data[1])) {
                    String myDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(
                            new Date( it.lastModified() )
                    );
                    arrayZIP.add(new DocumentosZIP(data[0], data[1].toUpperCase(), myDate, getStringSizeLengthFile(it.length())));
                }
            }
        }
        return arrayZIP;
    }
    /**
     * Este método realiza una conversion del tamaño del archivo a MB
     * @param size tamaño del archivo
     * @return la longitud del archivo en MB
     */
    private String getStringSizeLengthFile(long size) {
        LOGGER.info("parametro size methodo(getStringSizeLengthFile) {}", size);
        DecimalFormat df = new DecimalFormat("0.00");
        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;

        if(size < sizeMb)
            return df.format(size / sizeKb)+ " Kb";
        else if(size < sizeGb)
            return df.format(size / sizeMb) + " Mb";
        else if(size < sizeTerra)
            return df.format(size / sizeGb) + " Gb";
        return "";
    }
    /**
     * Este método obtiene una lista de todos los archivos de la ruta especificada por usuario.
     * @param nameFolder nombre de la carpeta del usuario.
     * @return ZIPArray con todos los documentos ZIP dentro de la carperta (nameFolder).
     */
    public ArrayList<DocumentosZIP> list_zip( String nameFolder ) {
        LOGGER.info("parametro nameFolder methodo(list_zip) {}", nameFolder);
        String path = path_out_zip + path_system + nameFolder;
        return ZIPArray( path, "zip" );
    }
    /**
     * Este método obtiene extrae todos los arhivos PDF de un ZIP comprimido
     * @param file archivo multipar .ZIP
     * @return True si se descomprimio con exito False si ocurrio un error
     * @throws IOException
     */
    public Boolean extract_zip(MultipartFile file) throws IOException {
        LOGGER.info("parametro file methodo(extract_zip) {}", file);
        String[] dataDoc;
        String[] dataFile;
        dataFile = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        if (dataFile[1].equals("zip")) {
            try ( ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(file.getBytes()))) {
                ZipEntry entry = null;
                while ((entry = zin.getNextEntry()) != null) {
                    if (entry.isDirectory()) {
                        File file_ = new File(path_out_zip_home, path_system + entry.getName());
                        file_.mkdir();
                        continue;
                    }
                    int len;
                    byte[] data = new byte[1024];
                    dataDoc = entry.getName().split("\\.");
                    if ("pdf".equals(dataDoc[1])) {
                        try ( FileOutputStream fos = new FileOutputStream(path_out_zip_home + path_system + entry.getName())) {
                            while ((len = zin.read(data)) != -1) {
                                fos.write(data, 0, len);
                            }
                        }
                    }
                    zin.closeEntry();
                }
                return true;
            }
        } else {
            return false;
        }
    }
    /**
     * Este método obtiene lista todos los PDF descomprimidos del ZIP
     * @return DocumentArray que lista todos los archivos PDF
     * @throws IOException
     */
    public ArrayList<Documentos> list_doc_pdf() throws IOException {
        return DocumentArray(path_out_zip_home, "pdf");
    }
    /**
     * Este método obtiene lista todos los PDF descomprimidos del ZIP
     * @param carpetaFinal carpeta donde se encuetra los ultimos documentos firmados
     * @return DocumentArray que lista todos los archivos PDF bajo el folder carpetaFinal
     * @throws IOException
     */
    public ArrayList<Documentos> listPDF_signed(int carpetaFinal) throws IOException {
        LOGGER.info("parametro carpetaFinal methodo(listPDF_signed) {}", carpetaFinal);
        return DocumentArray(dir_pdf_firma+path_system+carpetaFinal, "pdf");
    }
    /**
     * Este método firma digitalmente el PDF bajo los sigueintes parametros de entrada
     * @param password contraseña del certificado p12 o PFX funciona para ambos (Obs. Linux)
     * @param signatory nombre del firmante con el que se estampara la firma digital
     * @param dirCertificationPFX Archivo multipart del Certificado p12 o PFX
     * @param dirPDF direccion del archivo PDF a firmar
     * @param dirPDF_out direccion de salida del archivo PDF firmando
     * @param namePDF nombre del nuevo archivo PDF firmado
     * @param x posicion de la firma dentro del PDF
     * @param y posicion de la firma dentro del PDF
     * @param nro_firma cantidad de firmas que se realizara dentro del PDF
     * @throws IOException,KeyStoreException,CertificateException,NoSuchAlgorithmException,UnrecoverableKeyException,DocumentException
     */
    private void digital_signature(String password, String signatory, MultipartFile dirCertificationPFX, String dirPDF, String dirPDF_out, String namePDF, int x, int y, int nro_firma) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, DocumentException {
        LOGGER.info("parametro password methodo(digital_signature) {}", password);
        LOGGER.info("parametro signatory methodo(digital_signature) {}", signatory);
        LOGGER.info("parametro dirCertificationPFX methodo(digital_signature) {}", dirCertificationPFX);
        LOGGER.info("parametro dirPDF methodo(digital_signature) {}", dirPDF);
        LOGGER.info("parametro dirPDF_out methodo(digital_signature) {}", dirPDF_out);
        LOGGER.info("parametro x methodo(digital_signature) {}", x);
        LOGGER.info("parametro y methodo(digital_signature) {}", y);
        LOGGER.info("parametro nro_firma methodo(digital_signature) {}", nro_firma);

        Base64 decoder = new Base64();
        byte[] decodeBytes = decoder.decode(password);
        String pass = new String(decodeBytes, "UTF-8");

        File signPdfSrcFile = new File(dirPDF);
        DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String date = dateFormat.format(new Date());

        KeyStore ks = KeyStore.getInstance("pkcs12");
        char[] pwdArray = pass.toCharArray();
        ks.load(new ByteArrayInputStream(dirCertificationPFX.getBytes()), pwdArray);
        String alias = (String) ks.aliases().nextElement();
        PrivateKey key = (PrivateKey) ks.getKey(alias, pwdArray);
        Certificate[] chain = ks.getCertificateChain(alias);

        PdfReader reader = new PdfReader(dirPDF);
        File temp = new File(signPdfSrcFile.getParent(), System.currentTimeMillis() + ".pdf");
        FileOutputStream fout = new FileOutputStream(dirPDF_out + path_system + namePDF + "_" + nro_firma + ".pdf");
        PdfStamper stp = PdfStamper.createSignature(reader, fout, '\0', temp, true);
        PdfSignatureAppearance sap = stp.getSignatureAppearance();
        sap.setAcro6Layers(true);
        sap.setCrypto(key, (java.security.cert.Certificate[]) chain, null, PdfSignatureAppearance.WINCER_SIGNED);
        sap.setVisibleSignature(new Rectangle(x, y, x + 200, y + 68), reader.getNumberOfPages(), null);
        PdfTemplate n2 = sap.getLayer(2);
        n2.setCharacterSpacing(0.0f);
        ColumnText ct = new ColumnText(n2);
        ct.setSimpleColumn(n2.getBoundingBox().getLeft(), n2.getBoundingBox().getBottom(), n2.getBoundingBox().getWidth(), n2.getBoundingBox().getHeight());
        n2.setRGBColorFill(255, 0, 0);
        Paragraph p1 = new Paragraph(" ");
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        Font font1 = new Font(bf, 8, Font.ITALIC, Color.BLUE);
        Font font2 = new Font(bf, 10, Font.BOLD, Color.BLUE);
        Font font3 = new Font(bf, 8, Font.NORMAL, Color.BLUE);
        p1.setFont(font1);
        ct.addElement(p1);
        Paragraph p = new Paragraph(signatory, font2);
        p.setAlignment(Element.ALIGN_CENTER);
        Paragraph p2 = new Paragraph("Firmado digitalmente por:", font1);
        Paragraph p3 = new Paragraph(date, font3);
        p2.setAlignment(Element.ALIGN_CENTER);
        p3.setAlignment(Element.ALIGN_CENTER);
        ct.addElement(p2);
        ct.addElement(p);
        ct.addElement(p3);
        ct.go();
        stp.getWriter().setCompressionLevel(PdfStream.BEST_COMPRESSION);
        stp.close();
        reader.close();
        fout.close();
    }
    /**
     * Este método firma digitalmente los archivos segun la cantidad de firmas que sena necesesarias dentro del PDF
     * @param password contraseña del certifcado
     * @param signatory nombre del firmante
     * @param file archivo multipar del certificado PFX o p12
     * @param x posicion de la firma dentro del PDF
     * @param y posicion de la firma dentro del PDF
     * @param nro_signatures cantidad de firmas dentro del documento PDF
     * @return DocumentArray que lista todos los archivos PDF bajo la catidad de firmas realizadas
     * @throws KeyStoreException,NoSuchAlgorithmException,IOException,CertificateException,UnrecoverableKeyException,DocumentException
     */
    public ArrayList<Documentos> signaturePDF(String password, String signatory, MultipartFile file, int x, int y, int nro_signatures) throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, UnrecoverableKeyException, DocumentException {
        LOGGER.info("parametro password methodo(signaturePDF) {}", password);
        LOGGER.info("parametro signatory methodo(signaturePDF) {}", signatory);
        LOGGER.info("parametro file methodo(signaturePDF) {}", file);
        LOGGER.info("parametro x methodo(signaturePDF) {}", x);
        LOGGER.info("parametro y methodo(signaturePDF) {}", y);
        LOGGER.info("parametro nro_signatures methodo(signaturePDF) {}", nro_signatures);

        File directory;
        if (nro_signatures == 0) {
            directory = new File(dir_pdf_firma + path_system + nro_signatures);
            if(!directory.exists()){
                if (directory.mkdir()){
                    System.out.println("Carpeta "+nro_signatures);
                }else{
                    System.out.println("Error al crear la carpeta");
                }
            }
            for (Documentos it6 : DocumentArray(path_out_zip_home, "pdf")) {
                digital_signature(password, signatory, file, it6.rutaDoc, dir_pdf_firma+ path_system + nro_signatures, it6.nombreDoc, x, y, nro_signatures);
            }
        } else {
            directory = new File(dir_pdf_firma + path_system + nro_signatures);
            if(!directory.exists()){
                if (directory.mkdir()){
                    System.out.println("Carpeta "+nro_signatures);
                }else{
                    System.out.println("Error al crear la carpeta");
                }
            }
            int data = nro_signatures - 1;
            for (Documentos it6 : DocumentArray(dir_pdf_firma + path_system + data, "pdf")) {
                digital_signature(password, signatory, file, it6.rutaDoc, dir_pdf_firma+ path_system + nro_signatures, it6.nombreDoc, x, y, nro_signatures);
            }
        }
        return DocumentArray(dir_pdf_firma + path_system + nro_signatures, "pdf");
    }
    /**
     * Este método verifica que la contraseña del certificado sea correcta
     * @param password contraseña del certificado PFX o P12
     * @param dirCertificationPFX archivo multipart PFX o P12
     * @return True si la contraseña es correcta o False si la contraseña no es correcta.
     * @throws NoSuchAlgorithmException,InvalidAlgorithmParameterException
     */
    public Boolean certificate_verification(String password, MultipartFile dirCertificationPFX) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        try {
            LOGGER.info("parametro typeFile methodo(certificate_verification) {}", password);
            LOGGER.info("parametro typeFile methodo(certificate_verification) {}", dirCertificationPFX);
            Base64 decoder = new Base64();
            byte[] decodeBytes = decoder.decode(password);
            String pass = new String(decodeBytes, "UTF-8");
            LOGGER.debug("Obteniendo el pass {}", pass);

            KeyStore ks = KeyStore.getInstance("pkcs12");
            char[] pwdArray = pass.toCharArray();
            ks.load(new ByteArrayInputStream(dirCertificationPFX.getBytes()), pwdArray);
            System.out.println("Keystore Verificada");
            return true;
        } catch (java.security.cert.CertificateException | KeyStoreException | IOException e) {
            System.out.println(e);
            return false;
        }
    }
    /**
     * Este método comprime todos los archivos de una direccion o ruta en un ZIP
     * @param name_zip nombre del archivo ZIP
     * @param folder_final nombre del folder con la ultima firma digital realizada sobre los archovos PDF
     * @return ruta del archivo a descargar
     * @throws IOException
     */
    public String compress_files_pdf(String name_zip, String folder_final) throws IOException {
        String path_dowload = dir_pdf_firma + path_system + folder_final + path_system + name_zip + ".zip";
        LOGGER.info("parametro typeFile methodo(compress_files_pdf) {}", name_zip);
        LOGGER.info("parametro typeFile methodo(compress_files_pdf) {}", folder_final);
        System.out.println(path_dowload);
        try ( ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dir_pdf_firma + path_system + folder_final + path_system + name_zip + ".zip"))) {
            LOGGER.debug("Obteniendo el folder_final {}", folder_final);
            LOGGER.debug("Obteniendo el name_zip {}", name_zip);
            for (Documentos doc : DocumentArray(dir_pdf_firma + path_system + folder_final, "pdf")) {
                File file = new File(doc.rutaDoc);
                ZipEntry entry = new ZipEntry(file.getName());
                zos.putNextEntry(entry);
                try ( FileInputStream fis = new FileInputStream(file)) {
                    int len;
                    byte[] data = new byte[1024];
                    while ((len = fis.read(data)) != -1) {
                        zos.write(data, 0, len);
                    }
                    fis.close();
                }
                zos.closeEntry();
            }
            zos.finish();
        }
        return path_dowload;
    }
    /**
     * Este método comprime y crea un archivo ZIP con todas la firmas digitales realizadas como backup
     * @param name_zip nombre del ZIP
     * @param nameFolderUser nombre del folder del usuario a ingresar
     * @param folder_final nombre de la carpeta donde se encuentra las ultimas firmas realizadas
     * @return ruta del archivo a descargar
     * @throws IOException
     */
    public void end_process_signature(String name_zip, String nameFolderUser, String folder_final) throws IOException {
        LOGGER.info("parametro typeFile methodo(end_process_signature) {}", name_zip);
        LOGGER.info("parametro typeFile methodo(end_process_signature) {}", nameFolderUser);
        LOGGER.info("parametro typeFile methodo(end_process_signature) {}", folder_final);

        String path_dowload = path_out_zip + path_system + nameFolderUser + path_system + name_zip + ".zip";
        LOGGER.debug("Obteniendo el nameFolderUser {}", nameFolderUser);
        LOGGER.debug("Obteniendo el name_zip {}", name_zip);
        System.out.println(path_dowload);
        File directorio = new File(path_out_zip + path_system + nameFolderUser);
        LOGGER.debug("Obteniendo el name_zip {}", name_zip);
        if(!directorio.exists()){
            if (directorio.mkdir()){
                System.out.println("Carpeta de usuario "+nameFolderUser);
            }else{
                System.out.println("Error al crear la carpeta");
            }
        }
        try ( ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(path_out_zip + path_system + nameFolderUser + path_system + name_zip + ".zip"))) {
            LOGGER.debug("Obteniendo el nameFolderUser {}", nameFolderUser);
            LOGGER.debug("Obteniendo el name_zip {}", name_zip);
            for (Documentos doc : DocumentArray(dir_pdf_firma+ path_system +folder_final, "pdf")) {
                File file = new File(doc.rutaDoc);
                ZipEntry entry = new ZipEntry(file.getName());
                zos.putNextEntry(entry);
                try ( FileInputStream fis = new FileInputStream(file)) {
                    int len;
                    byte[] data = new byte[1024];
                    while ((len = fis.read(data)) != -1) {
                        zos.write(data, 0, len);
                    }
                    fis.close();
                }
                zos.closeEntry();
            }
            zos.finish();
        }
        endProcess();
    }
    /**
     * Este método abstrae el path del ZIP a descargar
     * @param nameFolder nombre del folder del usuario a ingresar
     * @param nameFile nombre del folder del usuario a ingresar
     * @return el path con todos los detalles del ZIP a descargar
     */
    public String downloadZIP(String nameFolder, String nameFile) {
        LOGGER.info("parametro typeFile methodo(downloadZIP) {}", nameFolder);
        LOGGER.info("parametro typeFile methodo(downloadZIP) {}", nameFile);
        LOGGER.debug("Obteniendo el nameFolder {}", nameFolder);
        LOGGER.debug("Obteniendo el nameFile {}", nameFile);
        return path_out_zip+ path_system +nameFolder+ path_system + nameFile;
    }
    /**
     * Este método limpia las carpetas al termnar el proceso de firma
     * @throws IOException
     */
    public void endProcess() throws IOException {
        deleteFile(path_out_zip_home);
        deleteFile(dir_pdf_firma);
    }
    /**
     * Este método limpia las carpetas segun el path
     * @param pathFile ruta del folder a limpiar
     * @throws IOException
     */
    private void deleteFile(String pathFile) throws IOException {
        LOGGER.info("parametro typeFile methodo(deleteFile) {}", pathFile);
        File directory = new File(pathFile);
        LOGGER.debug("Obteniendo la direccion del folder {}", pathFile);
        FileUtils.cleanDirectory(directory);
    }
}
