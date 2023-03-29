package com.example.application.views;

import com.example.application.backend.entity.User;
import com.example.application.components.appnav.AppNav;
import com.example.application.components.appnav.AppNavItem;
import com.example.application.views.about.AboutView;
import com.example.application.views.accounts.AccountsView;
import com.example.application.views.home.HomeView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.example.application.backend.service.UserService.saveUserToDatabase;
import static com.example.application.views.signIn.SignInView.getSignedInUser;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    // I have to get the user from the SignInView to add logout btn if they are signed-in
    private static H2 viewTitle;
    User user;

    public MainLayout() throws IOException {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() throws IOException {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        // view title in header
        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        HorizontalLayout header = new HorizontalLayout(toggle, viewTitle);

        Span user_name = new Span();
        user = getSignedInUser();

        Button btn_signIn = new Button("Sign In");
        btn_signIn.addClickListener(e -> {
            btn_signIn.getUI().ifPresent(ui ->
                    ui.navigate("signIn"));
        });

        Button btn_register = new Button("Register");
        btn_register.addClickListener(e -> {
            btn_register.getUI().ifPresent(ui ->
                    ui.navigate("register"));
        });

        Button btn_logout = new Button("Logout");
        btn_logout.addClickListener(e -> {

            // set the sign_in_session_uuid null; only signed-in users should have non-null sign in session uuid
            user.setSign_in_session_uuid(null);
            saveUserToDatabase(user); // you must save to update the sign in session uuid in database

            UI.getCurrent().getPage().reload();
        });

        if (user != null && user.getSign_in_session_uuid() != null) {

            Avatar avatar = new Avatar();
            byte[] uploaded_photo = user.getPhoto();

            if (uploaded_photo != null) {
                StreamResource streamResource = new StreamResource("", () -> new ByteArrayInputStream(uploaded_photo));
                avatar.setImageResource(streamResource);
            } else {
                String initial_1 = Character.toString(user.getFirst_name().charAt(0));
                String initial_2 = Character.toString(user.getLast_name().charAt(0));
                avatar.setAbbreviation((initial_1 + initial_2).toUpperCase());
            }

            user_name.setText(user.getFirst_name() + " " + user.getLast_name());
            header.add(avatar, user_name, btn_logout);
        } else {
            header.add(btn_signIn, btn_register);
        }

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        // make the buttons appear on the far right
        header.expand(viewTitle);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);
    }

    private void addDrawerContent() {
        H1 appName = new H1("My App 4");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        nav.addItem(new AppNavItem("Home", HomeView.class, "la la-globe"));
        nav.addItem(new AppNavItem("About", AboutView.class, "la la-file"));
        nav.addItem(new AppNavItem("Accounts", AccountsView.class, "la la-file"));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
