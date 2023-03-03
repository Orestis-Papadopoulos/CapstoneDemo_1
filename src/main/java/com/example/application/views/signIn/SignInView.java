package com.example.application.views.signIn;

import com.example.application.views.MainLayout;
import com.example.application.views.qrcode.QRCode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.UUID;

@PageTitle("Sign In")
@Route(value = "signIn", layout = MainLayout.class)
public class SignInView extends VerticalLayout {

    // add instructions and QR code

    // query database for 5 minutes

    // sign in user if session id was added to database

    VerticalLayout tipsLayout = new VerticalLayout();
    HorizontalLayout scanLayout = new HorizontalLayout();
    VerticalLayout qrLayout = new VerticalLayout();
    Image qr_code = new Image();

    public SignInView() {

        Span tip1 = new Span("To sign in, open your mobile app and scan the QR code to the right.");
        Span tip2 = new Span("Make sure you've scanned your card over your phone to complete the sign in process.");
        Button btn_cancel = new Button("Cancel");
        btn_cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        tipsLayout.add(tip1, tip2, btn_cancel);

        String data = UUID.randomUUID() + "";
        Notification.show(data);
        qr_code = new Image(QRCode.stringToStreamResource(data), "QR code resource missing: failed to load dynamically");
        qrLayout.add(qr_code);

        scanLayout.add(tipsLayout, qrLayout);
        add(scanLayout);
    }
}
