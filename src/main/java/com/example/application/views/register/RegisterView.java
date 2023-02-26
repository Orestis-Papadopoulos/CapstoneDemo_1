package com.example.application.views.register;

import com.example.application.backend.entity.User;
import com.example.application.views.MainLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

import javax.imageio.ImageIO;
import javax.xml.transform.stream.StreamSource;
import java.awt.image.BufferedImage;
import java.io.*;

@PageTitle("Register")
@Route(value = "register", layout = MainLayout.class)
public class RegisterView extends VerticalLayout {
    FormLayout formLayout = new FormLayout();
    TextField first_name = new TextField("First name");
    TextField last_name = new TextField("Last name");
    HorizontalLayout btn_layout;
    VerticalLayout tipsLayout = new VerticalLayout();
    HorizontalLayout scanLayout = new HorizontalLayout();
    VerticalLayout qrLayout = new VerticalLayout();
    Image qr_code = new Image();

    // for data binding
    Binder<User> binder = new Binder<>(User.class);
    User user = new User();
    String qrcode_data = user.getUuid() + "";
    String charset = "UTF-8";
    ByteArrayOutputStream imageBuffer = null;


    public RegisterView() throws IOException, WriterException {
        // automatic binding
        binder.bindInstanceFields(this);

        // form
        formLayout.setColspan(first_name, 2);
        formLayout.setColspan(last_name, 2);
        formLayout.add(first_name, last_name);
        formLayout.setWidth("25%");
        Button btn_ok = new Button("OK");
        btn_layout = new HorizontalLayout(btn_ok);
        add(formLayout, btn_layout);

        btn_ok.addClickListener(click_event -> {
            createNewUser();
        });

        Span tip1 = new Span("The QR code to the right is your password. Open your mobile app to scan it.");
        Span tip2 = new Span("After you've scanned it, scan your card over your phone to complete the registration.");
        Button btn_cancel = new Button("Cancel");
        btn_cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        tipsLayout.add(tip1, tip2, btn_cancel);

        // for "Dynamic Content" from: https://vaadin.com/docs/latest/advanced/dynamic-content#using-streamresource
        // found the constructor here: https://github.com/vaadin/flow/blob/main/flow-server/src/main/java/com/vaadin/flow/server/StreamResource.java
        // once you type InputStreamFactory in, it prompts for the methods to implement
        // goto vaadin book p. 91 and adjust the implementation of getStream() to create dynamic image
        StreamResource resource = new StreamResource("", new InputStreamFactory() {
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
        qr_code = new Image(resource, "QR code resource missing: failed to load dynamically");
        qrLayout.add(qr_code);

        scanLayout.add(tipsLayout, qrLayout);
        scanLayout.setVisible(false);
        add(scanLayout);
    }

    private void createNewUser() {
        formLayout.setVisible(false);
        btn_layout.setVisible(false);
        scanLayout.setVisible(true);

//        try {
//            // takes data from the form fields and matches them to the User entity fields
//            binder.writeBean(user);
//            if (binder.validate().isOk()) UserService.saveUserToDatabase(user);
//        } catch (ValidationException e) {
//            throw new RuntimeException(e);
//        }
    }
}
