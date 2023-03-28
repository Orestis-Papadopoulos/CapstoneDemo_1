package com.example.application.views.register;

import com.example.application.backend.entity.User;
import com.example.application.views.qrcode.QRCode;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.example.application.backend.service.UserService.*;


@PageTitle("Register")
@Route(value = "register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {
    TextField first_name = new TextField("First name");
    TextField last_name = new TextField("Last name");
    VerticalLayout formAndBtnLayout = new VerticalLayout();
    FormLayout formLayout = new FormLayout();
    HorizontalLayout btnLayout;
    VerticalLayout qrCodeLayout = new VerticalLayout();
    Image qr_code = new Image();
    H2 title = new H2("Register");

    // for data binding
    Binder<User> binder = new Binder<>(User.class);
    User user = new User();
    Timer timer = new Timer();
    String qrcode_data;
    String user_uuid = UUID.randomUUID() + "";

    public RegisterView() {

        // set user id manually
        user.setUser_uuid(user_uuid);

        // the QR code is a representation of the user's id
        qrcode_data =  user.getUser_uuid() + "";

        // automatic data binding
        binder.bindInstanceFields(this);

        // form
        formLayout.setColspan(first_name, 2);
        formLayout.setColspan(last_name, 2);
        formLayout.add(first_name, last_name);

        Button btn_ok = new Button("OK");
        Button btn_cancel_1 = new Button("Cancel");
        btn_cancel_1.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnLayout = new HorizontalLayout(btn_ok, btn_cancel_1);
        formAndBtnLayout.add(formLayout, btnLayout);
        // modify the width of the outer layout
        formAndBtnLayout.setWidth("25%");

        // align layout in the middle of the page
        setHorizontalComponentAlignment(Alignment.CENTER, title, formAndBtnLayout);
        add(title, formAndBtnLayout);

        btn_ok.addClickListener(click_event -> {
            createNewUser();
        });

        btn_cancel_1.addClickListener(click_event -> {
            btn_cancel_1.getUI().ifPresent(ui ->
                    ui.navigate("home"));
        });

        // QR code layout
        qr_code = new Image(QRCode.stringToStreamResource(qrcode_data), "QR code resource missing: failed to load dynamically");
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

        //////
        Page page = UI.getCurrent().getPage();

        // query database for about 5 minutes
        TimerTask timerTask = new TimerTask() {
            int iterations = 0;
            @Override
            public void run() {

                System.out.println("I am the timerTask and I have been executed " + iterations + " times.");

                // when the user scans the registration QR code, the proximity card uuid is added to database

                User new_user = getUserByUuid(user_uuid);
                System.out.println(user_uuid);
                if (new_user != null && new_user.getProximity_card_id() != null) {
                    System.out.println("A user tried to register");
                    timer.cancel();
                }

                // this statement must be placed at the end, otherwise even after timer.cancel() code within run() will execute
                if (++iterations > 20) {
                    System.out.println("Registration session timed out");
                    getUI().ifPresent(ui -> ui.access(() -> {
                        page.setLocation("home");
                    }));
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000); // period is in milliseconds
    }

    private void createNewUser() {
        formAndBtnLayout.setVisible(false);
        qrCodeLayout.setVisible(true);

        try {
            // takes data from the form fields and matches them to the User entity fields
            binder.writeBean(user);
            if (binder.validate().isOk()) saveUserToDatabase(user);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }
}
