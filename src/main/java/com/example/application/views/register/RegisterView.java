package com.example.application.views.register;

import com.example.application.backend.entity.User;
import com.example.application.views.qrcode.QRCode;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
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


@PageTitle("Register")
@Route(value = "register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {
    H2 title = new H2("Register");
    // the form fields must have the same names as the entity columns
    TextField first_name = new TextField("First name");
    TextField last_name = new TextField("Last name");
    byte[] photo;
    //
    VerticalLayout verticalLayout = new VerticalLayout();
    FormLayout txtfieldsLayout = new FormLayout();
    HorizontalLayout buttonsLayout;
    //
    VerticalLayout qrCodeLayout = new VerticalLayout();
    Image qr_code = new Image();

    // for data binding
    Binder<User> binder = new BeanValidationBinder<>(User.class);
    Timer timer = new Timer();
    String user_uuid = UUID.randomUUID() + "";
    User user = new User(user_uuid);
    Dialog dialog = new Dialog();
    // photo
    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    Upload upload = new Upload(buffer);

    public RegisterView() {
        // session timed out dialog
        dialog.setHeaderTitle("Registration Session Timed Out");
        VerticalLayout dialogLayout = new VerticalLayout();
        Span message = new Span("You will be redirected to Home page shortly.");
        dialogLayout.add(message);
        dialog.add(dialogLayout);
        add(dialog);

        // automatic data binding
        binder.bindInstanceFields(this);
        //binder.setBean(user);

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
        Button btn_cancel_1 = new Button("Cancel");
        btn_cancel_1.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonsLayout = new HorizontalLayout(btn_ok, btn_cancel_1);
        Span size_tip = new Span("You can upload one profile photo up to 10MB large.");
        Span type_tip = new Span("Accepted file types: (.jpg), (.jpeg), (.png)");
        verticalLayout.add(txtfieldsLayout, size_tip, type_tip, upload, buttonsLayout);
        // modify the width of the outer layout
        verticalLayout.setWidth("25%");

        // align layout in the middle of the page
        setHorizontalComponentAlignment(Alignment.CENTER, title, verticalLayout);
        add(title, verticalLayout);

        btn_ok.addClickListener(click_event -> {
            if (binder.validate().isOk()) {
                createUser();
                waitForQRScan();
            }
        });

        btn_cancel_1.addClickListener(click_event -> {
            btn_cancel_1.getUI().ifPresent(ui ->
                    ui.navigate("home"));
        });

        // QR code layout
        qr_code = new Image(QRCode.stringToStreamResource(user_uuid), "QR code resource missing: failed to load dynamically");
        Span tip = new Span("Open your mobile app and scan me to complete registration.");
        Button btn_cancel_2 = new Button("Cancel");

        btn_cancel_2.addThemeVariants(ButtonVariant.LUMO_ERROR);

        btn_cancel_2.addClickListener(click_event -> {
            timer.cancel();
            deleteUserFromDatabase(user);
            btn_cancel_1.getUI().ifPresent(ui ->
                    ui.navigate("home"));
        });

        qrCodeLayout.setWidth("25%");
        qrCodeLayout.setHorizontalComponentAlignment(Alignment.CENTER, qr_code, tip, btn_cancel_2);
        setHorizontalComponentAlignment(Alignment.CENTER, qrCodeLayout);
        qrCodeLayout.add(qr_code, tip, btn_cancel_2);
        qrCodeLayout.setVisible(false);
        add(qrCodeLayout);
    }

    private void createUser() {
        verticalLayout.setVisible(false);
        qrCodeLayout.setVisible(true);
        try {
            // takes data from the form fields and matches them to the User entity fields
            binder.writeBean(user);
            saveUserToDatabase(user);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public void waitForQRScan() {
        Page page = UI.getCurrent().getPage();

        // query database for about 5 minutes
        // for production: period = 2000; iterations values above = [147, 150] (2 sec * 150 iterations = 300 sec = 5 min)
        TimerTask timerTask = new TimerTask() {
            int iterations = 0;
            @Override
            public void run() {
                System.out.println("I am the timerTask and I have been executed " + iterations + " times.");
                // when the user scans the registration QR code, the proximity card uuid is added to database

                if (iterations < 6) {
                    user = getUserByUuid(user_uuid);

                    if (user.getProximity_card_id() != null && user.getProximity_card_id() != "") {
                        System.out.println("A user tried to register");
                        System.out.println("user uuid = " + user.getUser_uuid());
                        System.out.println("card id = " + user.getProximity_card_id());
                        System.out.println("user first name = " + user.getFirst_name());
                        getUI().ifPresent(ui -> ui.access(() -> {
                            page.setLocation("home");
                        }));
                        timer.cancel();
                    }
                }

                // with iterations == 147, message wil show for 6 secs
                if (iterations == 6) {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        qrCodeLayout.setVisible((false));
                        title.setVisible(false);
                        dialog.open();
                    }));
                }

                // this statement must be placed at the end, otherwise even after timer.cancel() code within run() will execute
                if (++iterations > 12) {
                    System.out.println("Registration session timed out");
                    deleteUserFromDatabase(user);
                    getUI().ifPresent(ui -> ui.access(() -> {
                        page.setLocation("home");
                    }));
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000); // period is in milliseconds
    }
}
