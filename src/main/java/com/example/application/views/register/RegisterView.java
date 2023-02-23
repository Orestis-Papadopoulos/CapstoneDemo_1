package com.example.application.views.register;

import com.example.application.backend.entity.User;
import com.example.application.backend.service.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Register")
@Route(value = "register", layout = MainLayout.class)
public class RegisterView extends VerticalLayout {
    // form fields
    TextField serial_number = new TextField("Phone serial number");
    TextField first_name = new TextField("First name");
    TextField last_name = new TextField("Last name");
    // for data binding
    Binder<User> binder = new Binder<>(User.class);
    User user = new User();

    public RegisterView() {
        // automatic binding
        binder.bindInstanceFields(this);
        serial_number.setHelperText("You can find your serial number under Settings > System > About phone > Status");

        // form
        FormLayout formLayout = new FormLayout();
        formLayout.add(serial_number, first_name, last_name);
        formLayout.setColspan(serial_number, 2);
        formLayout.setWidth("50%");
        add(formLayout);

        // buttons
        Button btn_ok = new Button("OK");
        btn_ok.addClickListener(click_event -> createNewUser());
        Button btn_cancel = new Button("Cancel");
        HorizontalLayout btnLayout = new HorizontalLayout(btn_ok, btn_cancel);
        add(btnLayout);
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
}
