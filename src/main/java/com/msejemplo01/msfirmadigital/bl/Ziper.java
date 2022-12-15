package com.msejemplo01.msfirmadigital.bl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
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

@Service
public class Ziper {
    String path_out_zip = "/opt/files/ZIP_OUT";
    String dir_pdf_firma = "/opt/files/PDF_FIRMADOS";
    String path_out_zip_home = "/opt/files/PDF_ZIP";
    private ArrayList<Documentos> DocumentArray(String dir, String typeFile) throws IOException {
        ArrayList<Documentos> arrayDocumentos = new ArrayList<>();
        java.io.File carpeta = new java.io.File(dir);
        java.io.File[] lista = carpeta.listFiles();
        String[] data;
        byte[] content;
        String encode;
        for (java.io.File it : lista) {
            if (it.isFile()) {
                data = it.getName().split("/.");
                if (typeFile.equals(data[1])) {
                    content = Files.readAllBytes(it.toPath());
                    encode = Base64.encodeBase64String(content);
                    arrayDocumentos.add(new Documentos(data[0], it.getPath(), data[1], encode));
                }
            }
        }
        return arrayDocumentos;
    }
    private ArrayList<DocumentosZIP> ZIPArray(String dir, String typeFile) throws IOException {
        ArrayList<DocumentosZIP> arrayZIP = new ArrayList<>();
        java.io.File carpeta = new java.io.File(dir);
        java.io.File[] lista = carpeta.listFiles();
        String[] data;
        for (java.io.File it : lista) {
            if (it.isFile()) {
                data = it.getName().split("/.");
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
    private String getStringSizeLengthFile(long size) {
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
    public ArrayList<DocumentosZIP> list_zip( String nameFolder ) throws IOException {
        String path = path_out_zip + "/" + nameFolder;
        return ZIPArray( path, "zip" );
    }
    public Boolean extrac_zip(MultipartFile file) throws IOException {
        String[] dataDoc;
        String[] dataFile;
        dataFile = Objects.requireNonNull(file.getOriginalFilename()).split("/.");
        if (dataFile[1].equals("zip")) {
            try ( ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(file.getBytes()))) {
                ZipEntry entry = null;
                while ((entry = zin.getNextEntry()) != null) {
                    if (entry.isDirectory()) {
                        File file_ = new File(path_out_zip_home, "/" + entry.getName());
                        file_.mkdir();
                        continue;
                    }
                    int len;
                    byte[] data = new byte[1024];
                    dataDoc = entry.getName().split("/.");
                    if ("pdf".equals(dataDoc[1])) {
                        try ( FileOutputStream fos = new FileOutputStream(path_out_zip_home + "/" + entry.getName())) {
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
    public ArrayList<Documentos> list_doc_files_word() throws IOException {
        return DocumentArray(path_out_zip_home, "pdf");
    }
    public ArrayList<Documentos> listPDF_f(int carpetaFinal) throws IOException {
        return DocumentArray(dir_pdf_firma+"/"+carpetaFinal, "pdf");
    }
    private void firmaDigital(String clavePrivada, String firmante, MultipartFile dirCertificationPFX, String dirPDF, String dirPDF_out, String namePDF, int x, int y, int nro_firma) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, DocumentException {
        File signPdfSrcFile = new File(dirPDF);
        DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String date = dateFormat.format(new Date());

        KeyStore ks = KeyStore.getInstance("pkcs12");
        char[] pwdArray = clavePrivada.toCharArray();
        ks.load(new ByteArrayInputStream(dirCertificationPFX.getBytes()), pwdArray);
        String alias = (String) ks.aliases().nextElement();
        PrivateKey key = (PrivateKey) ks.getKey(alias, pwdArray);
        Certificate[] chain = ks.getCertificateChain(alias);

        PdfReader reader = new PdfReader(dirPDF);
        File temp = new File(signPdfSrcFile.getParent(), System.currentTimeMillis() + ".pdf");
        FileOutputStream fout = new FileOutputStream(dirPDF_out + "/" + namePDF + "_" + nro_firma + ".pdf");
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
        Paragraph p = new Paragraph(firmante, font2);
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
    public ArrayList<Documentos> firmaPDF(String clavePrivada, String firmante, MultipartFile file, int x, int y, int firma) throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, UnrecoverableKeyException, DocumentException {
        File directory;
        if (firma == 0) {
            directory = new File(dir_pdf_firma + "/" + firma);
            if(!directory.exists()){
                if (directory.mkdir()){
                    System.out.println("Carpeta "+firma);
                }else{
                    System.out.println("Error al crear la carpeta");
                }
            }
            for (Documentos it6 : DocumentArray(path_out_zip_home, "pdf")) {
                firmaDigital(clavePrivada, firmante, file, it6.rutaDoc, dir_pdf_firma+ "/" + firma, it6.nombreDoc, x, y, firma);
            }
        } else {
            directory = new File(dir_pdf_firma + "/" + firma);
            if(!directory.exists()){
                if (directory.mkdir()){
                    System.out.println("Carpeta "+firma);
                }else{
                    System.out.println("Error al crear la carpeta");
                }
            }
            int data = firma - 1;
            for (Documentos it6 : DocumentArray(dir_pdf_firma + "/" + data, "pdf")) {
                firmaDigital(clavePrivada, firmante, file, it6.rutaDoc, dir_pdf_firma+ "/" + firma, it6.nombreDoc, x, y, firma);
            }
        }
        return DocumentArray(dir_pdf_firma+"/"+firma, "pdf");
    }
    public Boolean verific_firma(String clavePrivada, MultipartFile dirCertificationPFX) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        try {
            KeyStore ks = KeyStore.getInstance("pkcs12");
            char[] pwdArray = clavePrivada.toCharArray();
            ks.load(new ByteArrayInputStream(dirCertificationPFX.getBytes()), pwdArray);
            System.out.println("Keystore Verificada");
            return true;
        } catch (java.security.cert.CertificateException | KeyStoreException | IOException e) {
            System.out.println(e);
            return false;
        }
    }
    public String compress_file(String name_zip, String nameFolderUser, String folder_final) throws IOException {
        String path_dowload = path_out_zip + "/" + nameFolderUser + "/" + name_zip + ".zip";
        System.out.println(path_dowload);
        File directorio = new File(path_out_zip + "/" + nameFolderUser);
        if(!directorio.exists()){
            if (directorio.mkdir()){
                System.out.println("Carpeta de usuario "+nameFolderUser);
            }else{
                System.out.println("Error al crear la carpeta");
            }
        }
        try ( ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(path_out_zip + "/" + nameFolderUser + "/" + name_zip + ".zip"))) {
            for (Documentos doc : DocumentArray(dir_pdf_firma+"/"+folder_final, "pdf")) {
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
    public String dowloadZIP(String nameFolder, String nameFile) throws IOException {
        return path_out_zip+"/"+nameFolder+"/"+nameFile;
    }
    public void endProcess() throws IOException {
        deleteFile(path_out_zip_home);
        deleteFile(dir_pdf_firma);
    }
    private void deleteFile(String pathFile) throws IOException {
        File directory = new File(pathFile);
        FileUtils.cleanDirectory(directory);
    }
}
