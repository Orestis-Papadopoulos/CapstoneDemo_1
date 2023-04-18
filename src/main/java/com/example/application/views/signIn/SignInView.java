package com.example.application.views.signIn;

import com.example.application.backend.entity.User;
import com.example.application.views.MainLayout;
import com.example.application.views.home.HomeView;
import com.example.application.views.qrcode.QRCode;
import com.helger.commons.callback.IThrowingRunnable;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.History;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.example.application.backend.service.UserService.getUserBySignInSessionUuid;

@PageTitle("Sign In")
@Route(value = "signIn")
@AnonymousAllowed
public class SignInView extends VerticalLayout {
//    VerticalLayout tipsLayout = new VerticalLayout();
//    HorizontalLayout scanLayout = new HorizontalLayout();
//    VerticalLayout qrLayout = new VerticalLayout();
    VerticalLayout qrCodeLayout = new VerticalLayout();
    Image qr_code;
    String sign_in_session_uuid = UUID.randomUUID() + "";
    public static User user;
    Timer timer = new Timer();
    H2 title = new H2("Sign In");
    Dialog dialog = new Dialog();

    public SignInView() {
        // session timed out dialog
        dialog.setHeaderTitle("Sign In Session Timed Out");
        VerticalLayout dialogLayout = new VerticalLayout();
        Span message = new Span("You will be redirected to Home page shortly.");
        dialogLayout.add(message);
        dialog.add(dialogLayout);
        add(dialog);

        qr_code = new Image(QRCode.stringToStreamResource(sign_in_session_uuid), "QR code resource missing: failed to load dynamically");
        Span tip = new Span("Open your mobile app and scan me to sign in.");
        Button btn_cancel = new Button("Cancel");
        btn_cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);

        btn_cancel.addClickListener(e -> {
            timer.cancel();
            btn_cancel.getUI().ifPresent(ui ->
                    ui.navigate("home"));
        });

        qrCodeLayout.setWidth("25%");
        qrCodeLayout.setHorizontalComponentAlignment(Alignment.CENTER, title, qr_code, tip, btn_cancel);
        setHorizontalComponentAlignment(Alignment.CENTER, qrCodeLayout);
        qrCodeLayout.add(qr_code, tip, btn_cancel);
        add(title, qrCodeLayout);

        Page page = UI.getCurrent().getPage();

        // query database for about 5 minutes
        // for production: period = 2000; iterations values above = [147, 150] (2 sec * 150 iterations = 300 sec = 5 min)
        TimerTask timerTask = new TimerTask() {
            int iterations = 0;
            @Override
            public void run() {

                System.out.println("I am the timerTask and I have been executed " + iterations + " times.");

                if (iterations < 147) {
                    user = getUserBySignInSessionUuid(sign_in_session_uuid); // method defined in UserService

                    if (user != null) {
                        System.out.println("A user tried to sign in");

                        // see: "Asynchronous Updates" (https://vaadin.com/docs/v14/flow/advanced/tutorial-push-access)
                        getUI().ifPresent(ui -> ui.access(() -> {
                            page.setLocation("home");
                        }));
                        timer.cancel();
                    }
                }

                if (iterations == 147) {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        qrCodeLayout.setVisible(false);
                        title.setVisible(false);
                        dialog.open();
                    }));
                }

                // this statement must be placed at the end, otherwise even after timer.cancel() code within run() will execute
                if (++iterations > 150) {
                    System.out.println("Sign in session timed out");
                    getUI().ifPresent(ui -> ui.access(() -> {
                        page.setLocation("home");
                    }));
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 2000); // period is in milliseconds
    }

    public static User getSignedInUser() {
        return user;
    }
}
