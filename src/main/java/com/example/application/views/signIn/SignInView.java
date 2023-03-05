package com.example.application.views.signIn;

import com.example.application.backend.entity.User;
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

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@PageTitle("Sign In")
@Route(value = "signIn", layout = MainLayout.class)
public class SignInView extends VerticalLayout {

    VerticalLayout tipsLayout = new VerticalLayout();
    HorizontalLayout scanLayout = new HorizontalLayout();
    VerticalLayout qrLayout = new VerticalLayout();
    Image qr_code;
    String sign_in_session_uuid = UUID.randomUUID() + "";
    User user;

    public SignInView() {

        Span tip1 = new Span("To sign in, open your mobile app and scan the QR code to the right.");
        Span tip2 = new Span("Make sure you've scanned your card over your phone to complete the sign in process.");
        Button btn_cancel = new Button("Cancel");
        btn_cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btn_cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        tipsLayout.add(tip1, tip2, btn_cancel);

        qr_code = new Image(QRCode.stringToStreamResource(sign_in_session_uuid), "QR code resource missing: failed to load dynamically");
        qrLayout.add(qr_code);

        scanLayout.add(tipsLayout, qrLayout);
        add(scanLayout);

        // query database for about 5 minutes
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            int iterations = 0;
            @Override
            public void run() {
                System.out.println("I am the timerTask and I have executed.");
                // user = getUserBySignInSessionUuid(sign_in_session_uuid); // method defined in UserRepository
                // if (user != null) signUserIn(); // method defined in AuthorizationService ?

                // this statement must be placed at the end, otherwise even after timer.cancel() code within run() will execute
                if (++iterations > 5) {
                    System.out.println("Sign in session timed out.");
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000); // period is in milliseconds
    }
}
