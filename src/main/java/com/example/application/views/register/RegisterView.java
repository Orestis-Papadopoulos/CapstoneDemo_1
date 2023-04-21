package com.example.application.views.register;

import com.example.application.backend.entity.User;
import com.example.application.views.qrcode.QRCode;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.apache.commons.compress.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.example.application.backend.service.UserService.*;

/**
 * Displays the newly created user's uuid encoded as a QR code.
 * A thread waits for 5 minutes for the user to scan the QR code to complete the registration.
 * If the QR code is not scanned in that interval, the user is deleted from the
 * database and the UI redirects to the Home page.
 * */

@PageTitle("Register")
@Route(value = "register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {
    H2 title = new H2("Register");
    // the form fields (that is, the variable names) must have the same names
    // as the entity columns for data binding to work
    TextField first_name = new TextField("First name");
    TextField last_name = new TextField("Last name");
    //
    VerticalLayout parentLayout = new VerticalLayout();
    FormLayout txtfieldsLayout = new FormLayout();
    HorizontalLayout buttonsLayout;
    VerticalLayout qrCodeLayout = new VerticalLayout();
    Image qr_code = new Image();
    //
    Binder<User> binder = new BeanValidationBinder<>(User.class); // for data binding
    Timer timer = new Timer();
    String user_uuid = UUID.randomUUID() + "";
    User user = new User(user_uuid);
    Dialog dialog = new Dialog();
    // for profile photo
    byte[] photo;
    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    Upload upload = new Upload(buffer);

    public RegisterView() {
        // session timed out dialog
        dialog.setHeaderTitle("Registration Session Timed Out");
        VerticalLayout dialogLayout = new VerticalLayout();
        Span message = new Span("Redirecting to Home page...");
        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        dialogLayout.add(message, progressBar);
        dialog.add(dialogLayout);
        add(dialog);

        // automatic data binding
        binder.bindInstanceFields(this);

        // for photo
        upload.setMaxFiles(1);
        upload.setAcceptedFileTypes("image/jpeg", ".jpeg", ".jpg",
                                    "image/png", ".png");
        upload.setDropLabel(new Span("Drop photo here"));
        Button btn_upload = new Button("Upload Photo");
        upload.setUploadButton(btn_upload);

        // if the upload button is modified, then it is not disabled when 1 file (max allowed) is uploaded
        // do this to disable/enable accordingly
        upload.getElement()
                .addEventListener("max-files-reached-changed", event -> {
                    boolean maxFilesReached = event.getEventData()
                            .getBoolean("event.detail.value");
                    btn_upload.setEnabled(!maxFilesReached);
                }).addEventData("event.detail.value");

        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream inputStream = buffer.getInputStream(fileName);
            try {
                photo = IOUtils.toByteArray(inputStream);
                user.setPhoto(photo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // form
        txtfieldsLayout.setColspan(first_name, 2);
        txtfieldsLayout.setColspan(last_name, 2);
        txtfieldsLayout.add(first_name, last_name);

        Button btn_ok = new Button("OK");
        Button btn_cancel_form = new Button("Cancel");
        btn_cancel_form.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonsLayout = new HorizontalLayout(btn_ok, btn_cancel_form);

        Span file_size_tip = new Span("You can upload one profile photo up to 10MB large.");
        Span file_type_tip = new Span("Accepted file types: (.jpg), (.jpeg), (.png)");
        parentLayout.add(txtfieldsLayout, file_size_tip, file_type_tip, upload, buttonsLayout);

        parentLayout.setMinWidth(300, Unit.PIXELS); // do this in case the browser window gets very narrow
        parentLayout.setWidth("25%");

        // align layout in the middle of the page
        setHorizontalComponentAlignment(Alignment.CENTER, title, parentLayout);
        add(title, parentLayout);

        btn_ok.addClickListener(click_event -> {
            if (binder.validate().isOk()) {
                createUser();
                waitForQRScan();
            }
        });

        btn_cancel_form.addClickListener(click_event -> {
            btn_cancel_form.getUI().ifPresent(ui ->
                    ui.navigate("home"));
        });

        // QR code layout
        qr_code = new Image(QRCode.stringToStreamResource(user_uuid), "QR code resource missing: failed to load dynamically");
        Span scan_tip = new Span("Open your mobile app and scan me to complete the registration.");

        Button btn_cancel_scan = new Button("Cancel");
        btn_cancel_scan.addThemeVariants(ButtonVariant.LUMO_ERROR);

        btn_cancel_scan.addClickListener(click_event -> {
            timer.cancel(); // stop the thread that is waiting for scan
            deleteUserFromDatabase(user);
            btn_cancel_form.getUI().ifPresent(ui ->
                    ui.navigate("home"));
        });

        qrCodeLayout.setWidth("50%");
        qrCodeLayout.setHorizontalComponentAlignment(Alignment.CENTER, qr_code, scan_tip, btn_cancel_scan);
        setHorizontalComponentAlignment(Alignment.CENTER, qrCodeLayout);
        qrCodeLayout.add(qr_code, scan_tip, btn_cancel_scan);
        qrCodeLayout.setVisible(false);
        add(qrCodeLayout);
    }

    /**
     * Hides the form layout and shows the QR code.
     * Creates a user in the database with the first name, last name, and photo (if one was uploaded) the user gave.
     * Note that this database row will be deleted if the user does not scan the QR code in the designated time
     * interval (set to 5 minutes).
     * */
    private void createUser() {
        parentLayout.setVisible(false);
        qrCodeLayout.setVisible(true);
        try {
            // takes data from the form fields and matches them to the User entity fields
            binder.writeBean(user);
            saveUserToDatabase(user);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts a background thread which waits for about 5 minutes for the user to scan the displayed QR code.
     * If the QR code is scanned, the mobile app used for the scan will add the proximity card id of the user
     * to the database, thus completing the registration. Note that the QR code represents the user's uuid.
     * This is how the mobile app knows to which row to add the proximity card id.
     * */
    public void waitForQRScan() {
        Page page = UI.getCurrent().getPage();

        TimerTask timerTask = new TimerTask() {
            int iterations = 0; // how many times the thread will run
            @Override
            public void run() {
                System.out.println("I am the timerTask and I have been executed " + iterations + " times.");

                // for 5 minutes, set to 150 (thread execution period is set to 2 seconds; 150 * 2 = 300 seconds = 5 minutes)
                if (iterations < 150) {
                    user = getUserByUuid(user_uuid);

                    // if the proximity card id is null, then the user did not scan the QR code
                    if (user.getProximity_card_id() != null && !user.getProximity_card_id().equals("")) {
                        System.out.println("A user tried to register");

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
                    // show timeout message
                    getUI().ifPresent(ui -> ui.access(() -> {
                        qrCodeLayout.setVisible((false));
                        title.setVisible(false);
                        dialog.open();
                    }));
                }

                // this statement must be placed at the end, otherwise even after timer.cancel() code within run() will execute
                // set to 150 + 3, to show message for 3 * period = 6 seconds
                if (++iterations > 153) {
                    System.out.println("Registration session timed out");
                    deleteUserFromDatabase(user);
                    getUI().ifPresent(ui -> ui.access(() -> {
                        page.setLocation("home");
                    }));
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 2000); // period is in milliseconds
    }
}
