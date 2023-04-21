package com.example.application.views;

import com.example.application.backend.entity.User;
import com.example.application.components.appnav.AppNav;
import com.example.application.components.appnav.AppNavItem;
import com.example.application.views.about.AboutView;
import com.example.application.views.accounts.AccountsView;
import com.example.application.views.guide.GuideView;
import com.example.application.views.home.HomeView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.compress.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.example.application.backend.service.UserService.saveUserToDatabase;
import static com.example.application.views.signIn.SignInView.getSignedInUser;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private static H2 viewTitle;
    // get the signed-in user from the SignInView to add the logout button if they are signed-in
    User user;
    Dialog profileDialog = new Dialog();
    TextField first_name = new TextField("First name");
    TextField last_name = new TextField("Last name");
    Image image;
    byte[] photo;
    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    Upload upload = new Upload(buffer);
    StreamResource streamResource;
    Binder<User> binder = new BeanValidationBinder<>(User.class);

    public MainLayout() throws IOException {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
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

        if (user != null && user.getSign_in_session_uuid() != null) {

            Avatar avatar = new Avatar();
            photo = user.getPhoto();

            if (photo != null) {
                streamResource = new StreamResource("", () -> new ByteArrayInputStream(photo));
                image = new Image(streamResource, "Profile photo");
                avatar.setImageResource(streamResource);
            } else {
                String initial_1 = Character.toString(user.getFirst_name().charAt(0));
                String initial_2 = Character.toString(user.getLast_name().charAt(0));
                avatar.setAbbreviation((initial_1 + initial_2).toUpperCase());
            }
            user_name.setText(user.getFirst_name() + " " + user.getLast_name());

            // "pair Avatar with Menu Bar to create a user account menu"
            MenuBar account_menu = new MenuBar();
            account_menu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

            MenuItem menuItem = account_menu.addItem(avatar);
            SubMenu subMenu = menuItem.getSubMenu();
            setUpProfileDialog();
            subMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.USER_CARD), new Span("Profile")), listener -> profileDialog.open());
            subMenu.addItem(new HorizontalLayout(new Icon(VaadinIcon.SIGN_OUT), new Span("Logout")), listener -> logout());

            // add the account_menu on the header, not the avatar
            header.add(account_menu, user_name);
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

    /**
     * Sets up the drawer (left side of layout) with the logo and app name.
     * */
    private void addDrawerContent() {
        Image logo = new Image("images/app_logo_transparent.png", "app-logo");
        logo.setHeight(40, Unit.PIXELS);
        H1 appName = new H1("Qard");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        Header header = new Header(logo, appName);
        Scroller scroller = new Scroller(createNavigation());
        addToDrawer(header, scroller, createFooter());
    }

    /**
     * Adds the navigation links to the drawer.
     * */
    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme

        AppNav nav = new AppNav();
        nav.addItem(new AppNavItem("Home", HomeView.class, new Icon(VaadinIcon.HOME)));
        nav.addItem(new AppNavItem("Accounts", AccountsView.class, new Icon(VaadinIcon.GRID_BEVEL)));
        nav.addItem(new AppNavItem("Guide", GuideView.class, new Icon(VaadinIcon.BOOK)));
        nav.addItem(new AppNavItem("About", AboutView.class, new Icon(VaadinIcon.INFO_CIRCLE_O)));
        return nav;
    }

    /**
     * Adds the footer with copyrights info to the drawer.
     * */
    private Footer createFooter() {
        Footer layout = new Footer();
        Image img_copyright = new Image("images/copyright.png", "");
        img_copyright.setHeight(15, Unit.PIXELS);
        layout.add(img_copyright, new Span("Qard. All rights reserved."));
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

    /**
     * Creates the profile dialog of the user. It has their first name, last name, and profile photo (if any).
     * The user can view and edit their profile info with this dialog.
     * */
    public void setUpProfileDialog() {
        // for data binding
        binder.bindInstanceFields(this);

        profileDialog.setHeaderTitle("Profile Info");
        VerticalLayout profileDialogLayout = new VerticalLayout();
        FormLayout profileFormlayout = new FormLayout();

        first_name.setValue(user.getFirst_name());
        last_name.setValue(user.getLast_name());

        profileFormlayout.setColspan(first_name, 2);
        profileFormlayout.setColspan(last_name, 2);
        profileFormlayout.add(first_name, last_name);

        // for uploading/editing a photo
        upload.setDropLabel(new Span("Drop photo here"));
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

        Button btn_upload = new Button("Upload photo");
        upload.setUploadButton(btn_upload);

        // if the upload button is modified, then it is not disabled when 1 file (max allowed) is uploaded
        // do this to disable/enable accordingly
        upload.getElement()
                .addEventListener("max-files-reached-changed", event -> {
                    boolean maxFilesReached = event.getEventData().getBoolean("event.detail.value");
                    btn_upload.setEnabled(!maxFilesReached);
                }).addEventData("event.detail.value");

        profileDialogLayout.add(profileFormlayout, upload);

        // put photo in avatar, if one is uploaded, otherwise put user's initials to avatar
        if (photo != null) {
            MenuBar photo_menu = new MenuBar();
            MenuItem edit_photo = photo_menu.addItem("Edit photo");
            SubMenu submenu = edit_photo.getSubMenu();
            submenu.addItem("Upload new", click_event ->
                    // see last comment at: https://github.com/vaadin/flow-components/issues/1384
                    upload.getElement().callJsFunction("shadowRoot.getElementById('addFiles').click"));
            submenu.addItem("Delete", click_event -> {
                user.setPhoto(null);
            });

            upload.setUploadButton(photo_menu);
            image.setHeight("250px");
            image.setClassName("user-photo"); // to apply CSS property only to this image
            profileDialogLayout.add(image);
        } // end of Upload component

        profileDialog.add(profileDialogLayout);

        // for saving changes to profile
        Button btn_save = new Button("Save");
        btn_save.addClickListener(click_event -> {
            if (binder.validate().isOk()) {
                try {
                    binder.writeBean(user);
                    saveUserToDatabase(user);
                    profileDialog.close();
                    UI.getCurrent().getPage().reload();
                } catch (ValidationException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Button btn_cancel = new Button("Cancel", clickEvent -> profileDialog.close());
        btn_cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        profileDialog.getFooter().add(btn_cancel, btn_save);
    }

    /**
     * Logs out the user by setting their sign in session uuid to null.
     * Only signed-in users have a non-null sign in session uuid.
     * */
    public void logout() {
        user.setSign_in_session_uuid(null);
        saveUserToDatabase(user); // you must save to update the sign in session uuid in database
        UI.getCurrent().getPage().reload();
    }
}
