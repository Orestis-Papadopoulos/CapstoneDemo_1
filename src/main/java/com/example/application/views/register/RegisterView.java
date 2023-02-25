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
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.lang.model.element.Element;
import java.io.File;
import java.io.IOException;

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
    Image qr_code = new Image("images/qr_data.png", "Missing");

    // for data binding
    Binder<User> binder = new Binder<>(User.class);
    User user = new User();

    // for QR code generation
    String qrcode_data = user.getUuid() + "";
    String path = "C:\\Users\\Orestis\\IntelliJ Projects\\my-app-4\\src\\main\\resources\\META-INF\\resources\\images\\qr_data.png";
    String charset = "UTF-8";

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

        //generateQRCode(qrcode_data, path, charset, 300, 300);

        Span tip1 = new Span("The QR code to the right is your password. Open your mobile app to scan it.");
        Span tip2 = new Span("After you've scanned it, scan your card over your phone to complete the registration.");
        Button btn_cancel = new Button("Cancel");
        btn_cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        tipsLayout.add(tip1, tip2, btn_cancel);

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

    public static void generateQRCode(String data, String path, String charset, int h, int w) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, w, h);
        MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
    }
}
