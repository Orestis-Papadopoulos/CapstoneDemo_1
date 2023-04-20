package com.example.application.views.home;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    VerticalLayout outerLayout = new VerticalLayout();
    HorizontalLayout imagesLayout = new HorizontalLayout();

    VerticalLayout cardLayout = new VerticalLayout();
    VerticalLayout phoneLayout = new VerticalLayout();
    VerticalLayout desktopLayout = new VerticalLayout();

    Image card, phone, desktop;
    Span cardSpan, phoneSpan, desktopSpan;

    public HomeView() {

        // card, phone, and desktop images
        card = new Image("images/scan_RFID.png", "");
        card.setHeight(180, Unit.PIXELS);
        card.setClassName("card-img");
        phone = new Image("images/smartphone.png", "");
        phone.setHeight(180, Unit.PIXELS);
        phone.setClassName("phone-img");
        desktop = new Image("images/desktop.png", "");
        desktop.setHeight(180, Unit.PIXELS);
        desktop.setClassName("desktop-img");

        // Spans below images
        cardSpan = new Span("Use your proximity card as an identifier and forget about passwords. Only MIFARE Classic cards are supported.");
        cardSpan.addClassName("card-span");
        phoneSpan = new Span("Use your phone to scan your proximity card. Scan the displayed QR code to register and sign in. Your device must have NFC.");
        phoneSpan.addClassName("phone-span");
        desktopSpan = new Span("Access and edit your online accounts with one click. No more typing usernames and passwords. Only the Firefox web browser is supported.");
        desktopSpan.addClassName("desktop-span");

        cardLayout.add(card, cardSpan);
        phoneLayout.add(phone, phoneSpan);
        desktopLayout.add(desktop, desktopSpan);

        cardLayout.setAlignItems(Alignment.CENTER);
        phoneLayout.setAlignItems(Alignment.CENTER);
        desktopLayout.setAlignItems(Alignment.CENTER);
        imagesLayout.add(cardLayout, phoneLayout, desktopLayout);

        // wavy title
        H2 title = new H2();
        title.addClassName("wave");
        title.add(new Span("W"), new Span("e"), new Span("l"), new Span("c"), new Span("o"), new Span("m"), new Span("e "),
                new Span("t"), new Span("o "),
                new Span("Q"), new Span("a"), new Span("r"), new Span("d"));

        outerLayout.add(title,
                            new Image("images/decorative_line_divider.png", ""),
                            new H3("A place where you authenticate once, login everywhere."),
                            new Image("images/empty.png", ""),
                imagesLayout);
        outerLayout.setAlignItems(Alignment.CENTER);
        add(outerLayout);
    }
}
