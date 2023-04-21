package com.example.application.views.signIn;

import com.example.application.backend.entity.User;
import com.example.application.views.qrcode.QRCode;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.example.application.backend.service.UserService.getUserBySignInSessionUuid;

/**
 * Displays the uuid corresponding to a sign in session encoded as a QR code.
 * A thread waits for 5 minutes for the user to scan the QR code to sing in.
 * If the QR code is not scanned in that interval, the uuid is deleted from the
 * database and the UI redirects to the Home page.
 * */

@PageTitle("Sign In")
@Route(value = "signIn")
@AnonymousAllowed
public class SignInView extends VerticalLayout {
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
        Span message = new Span("Redirecting to Home page...");
        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        dialogLayout.add(message, progressBar);
        dialog.add(dialogLayout);
        add(dialog);

        qr_code = new Image(QRCode.stringToStreamResource(sign_in_session_uuid), "QR code resource missing: failed to load dynamically");
        Span scan_tip = new Span("Open your mobile app and scan me to sign in.");
        Button btn_cancel = new Button("Cancel");
        btn_cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);

        btn_cancel.addClickListener(e -> {
            timer.cancel(); // stop the thread that is waiting for scan
            btn_cancel.getUI().ifPresent(ui ->
                    ui.navigate("home"));
        });

        qrCodeLayout.setMinWidth(300, Unit.PIXELS); // do this in case the browser window gets very narrow
        qrCodeLayout.setWidth("25%");
        qrCodeLayout.setHorizontalComponentAlignment(Alignment.CENTER, title, qr_code, scan_tip, btn_cancel);
        qrCodeLayout.add(qr_code, scan_tip, btn_cancel);

        setHorizontalComponentAlignment(Alignment.CENTER, qrCodeLayout); // align qrCodeLayout at page center
        add(title, qrCodeLayout);

        Page page = UI.getCurrent().getPage();

        /*
          Starts a background thread which waits for about 5 minutes for the user to scan the displayed QR code.
          If the QR code is scanned, the mobile app used for the scan will add the sign in session id to the
          database, thus completing the sign-in process. Note that the QR code represents a uuid for the sign in
          session. The thread searches for any user who has their sign_in_session_uuid field equal to the value
          the QR code has.
          */
        TimerTask timerTask = new TimerTask() {
            int iterations = 0; // how many times the thread will run
            @Override
            public void run() {

                System.out.println("I am the timerTask and I have been executed " + iterations + " times.");

                // for 5 minutes, set to 150
                if (iterations < 150) {
                    user = getUserBySignInSessionUuid(sign_in_session_uuid); // method defined in UserService class

                    if (user != null) {
                        System.out.println("A user tried to sign in");

                        // see "Asynchronous Updates" at: https://vaadin.com/docs/v14/flow/advanced/tutorial-push-access
                        // on how to navigate in the web app from a background thread
                        getUI().ifPresent(ui -> ui.access(() -> {
                            page.setLocation("home");
                        }));
                        timer.cancel();
                    }
                }

                // for 5 minutes, set to 150
                if (iterations == 150) {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        qrCodeLayout.setVisible(false);
                        title.setVisible(false);
                        dialog.open();
                    }));
                }

                // this statement must be placed at the end, otherwise even after timer.cancel() code within run() will execute
                // set to 150 + 3, to show message for 3 * period = 6 seconds
                if (++iterations > 153) {
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

    /**
     * @return The user who has signed in (it can be null).
     * */
    public static User getSignedInUser() {
        return user;
    }
}
