package com.example.application.views.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Used by RegisterView and SignInView to encode the user's uuid and the sign in session uuid
 * respectively into a QR code the user can scan.
 * */

public class QRCode {

    private static String charset = "UTF-8";
    private static ByteArrayOutputStream imageBuffer = null;

    // see "Dynamic Content" at: https://vaadin.com/docs/latest/advanced/dynamic-content#using-streamresource
    // found the constructor here: https://github.com/vaadin/flow/blob/main/flow-server/src/main/java/com/vaadin/flow/server/StreamResource.java
    // once you type InputStreamFactory in, it prompts for the methods to implement
    // goto the vaadin book (.pdf) p. 91 and adjust the implementation of getStream() to create dynamic image
    public static StreamResource stringToStreamResource(String qrcode_data) {
        return new StreamResource("", new InputStreamFactory() {
            @Override
            public InputStream createInputStream() {
                BitMatrix matrix = null;
                try {
                    matrix = new MultiFormatWriter().encode(new String(qrcode_data.getBytes(charset), charset), BarcodeFormat.QR_CODE, 300, 300);
                } catch (WriterException | UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
                imageBuffer = new ByteArrayOutputStream();
                try {
                    ImageIO.write(image, "png", imageBuffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return new ByteArrayInputStream(imageBuffer.toByteArray());
            }

            @Override
            public boolean requiresLock() {
                return InputStreamFactory.super.requiresLock();
            }
        });
    }
}
