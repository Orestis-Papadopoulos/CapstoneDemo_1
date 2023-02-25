package com.example.application.views.register;

import com.example.application.backend.entity.User;
import com.example.application.backend.service.UserService;
import com.example.application.views.MainLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@PageTitle("Register")
@Route(value = "register", layout = MainLayout.class)
public class RegisterView extends VerticalLayout {
    // form fields
    TextField first_name = new TextField("First name");
    TextField last_name = new TextField("Last name");
    // for data binding
    Binder<User> binder = new Binder<>(User.class);
    User user = new User();

    // for QR code generation
    String qrcode_data = user.getUuid() + "";
    String path = "C:\\Users\\Orestis\\IntelliJ Projects\\my-app-4\\src\\main\\resources\\META-INF\\resources\\images\\qr_data.png";
    String charset = "UTF-8";


    public RegisterView() throws IOException, WriterException, InterruptedException {
        // automatic binding
        binder.bindInstanceFields(this);

        // form
        FormLayout formLayout = new FormLayout();
        formLayout.add(first_name, last_name);
        formLayout.setWidth("50%");
        add(formLayout);

        // buttons
        Button btn_ok = new Button("OK");
        btn_ok.addClickListener(click_event -> createNewUser());
        Button btn_cancel = new Button("Cancel");
        HorizontalLayout btnLayout = new HorizontalLayout(btn_ok, btn_cancel);
        add(btnLayout);

//        Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
//        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
//        generateQRCode(qrcode_data, path, charset, hashMap, 200, 200);
        Image qrcode = new Image("images/qr_data.png", "QR image missing");
        add(qrcode);
    }

    private void createNewUser() {
        try {
            // takes data from the form fields and matches them to the User entity fields
            binder.writeBean(user);
            if (binder.validate().isOk()) UserService.saveUserToDatabase(user);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generateQRCode(String data, String path, String charset, Map map, int h, int w) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, w, h);
        MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
    }
}
