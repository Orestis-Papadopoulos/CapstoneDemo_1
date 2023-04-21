package com.example.application.views.about;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * A view which aims to inform about:
 * >> the application (functionality, prerequisites, name, and logo),
 * >> the course,
 * >> and the developer.
 * */

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@AnonymousAllowed
public class AboutView extends VerticalLayout {

    Accordion accordion = new Accordion();
    VerticalLayout functionalityLayout = new VerticalLayout();
    VerticalLayout prerequisitesLayout = new VerticalLayout();
    VerticalLayout nameAndLogoLayout = new VerticalLayout();
    VerticalLayout courseLayout = new VerticalLayout();
    VerticalLayout developerLayout = new VerticalLayout();

    public AboutView() {

        // to set background image
        addClassName("aboutView");

        accordion.setWidth("50%");
        // do the following in case the browser window gets very narrow
        accordion.setMinWidth(600, Unit.PIXELS);

        // functionality
        Span about_functionality = new Span("Qard offers a unique way of signing in, by using your smartphone and a " +
                "proximity card. By doing so you can organize your online account credentials and sign in to the " +
                "account of your choice automatically. Add the account information in the relevant table and enjoy " +
                "your online experience without typing usernames and passwords.");
        about_functionality.getStyle().set("text-indent", "40px");
        about_functionality.getStyle().set("text-align", "justify");
        functionalityLayout.add(about_functionality);

        // prerequisites
        prerequisitesLayout.add(new Span("1. An Android smartphone with NFC"),
                                new Span("2. An Internet connection, both on your PC and smartphone"),
                                new Span("3. A MIFARE Classic proximity card"),
                                new Span("4. The Qard authentication app installed on your smartphone"),
                                new Span("5. The Firefox web browser"));

        // name and logo
        Span about_name = new Span("One of the earliest efforts to create programmable machines was made " +
                "by the French weaver and merchant Joseph Marie Jacquard. His invention, the Jacquard machine, allowed " +
                "looms to weave patterns in textiles in an automated fashion. To praise his contribution, this application " +
                "is named Qard. The name combines the word \"Card\" (since the app uses a proximity card) and the letter " +
                "\"Q\" from Jacquard's name.");
        about_name.getStyle().set("text-indent", "40px");
        about_name.getStyle().set("text-align", "justify");

        Span about_logo = new Span("Jacquard's machine used punch cards to program the loom into weaving " +
                "the desired pattern. These were paper cards with holes encoding the data. The application's logo " +
                "represents such a card which encodes 01, 01, 11 (if turned clockwise 90 degrees). In decimal, " +
                "this is equivalent to 1, 1, 3. The letter \"Q\", for Qard, in ASCII in 113.");
        about_logo.getStyle().set("text-indent", "40px");
        about_logo.getStyle().set("text-align", "justify");

        nameAndLogoLayout.add(about_name, about_logo);
        nameAndLogoLayout.add(new Image("images/empty.png", "")); // for spacing last Span and images

        // loom cards image with caption
        Image img_loom_cards = new Image("images/loom_cards.jpg", "");
        img_loom_cards.addClassName("loomCards");
        img_loom_cards.setMinWidth(300, Unit.PIXELS);
        img_loom_cards.setWidth("25%");

        Span caption1 = new Span("Fig. 1: Loom Punch Cards");
        caption1.getStyle().set("font-style", "italic");

        // app logo image with caption
        Image img_logo = new Image("images/app_logo_with_ones.png", "");
        img_logo.setMinWidth(150, Unit.PIXELS);
        img_logo.setWidth("25%");

        Span caption2 = new Span("Fig. 2: App Logo Encoding");
        caption2.getStyle().set("font-style", "italic");

        HorizontalLayout imgLayout = new HorizontalLayout(
                new VerticalLayout(caption1, img_loom_cards),
                new VerticalLayout(caption2, img_logo)
        );
        nameAndLogoLayout.add(imgLayout);

        // course
        Span about_syllabus = new Span("The course is the capstone experience for students following the\n" +
                "Software Development pathway of the IT programme. Students are\n" +
                "required to develop a complete solution to a given problem, based\n" +
                "on the knowledge and the skills acquired during their studies.\n" +
                "Moreover, students will gain the experience of working on a realtime\n" +
                "basis and combine academic with professional practices,\n" +
                "including domain-specific research, gradual progression, revisiting,\n" +
                "and evaluation.");
        about_syllabus.getStyle().set("text-indent", "40px");
        about_syllabus.getStyle().set("text-align", "justify");

        courseLayout.add(new Span("ITC4918 Software Development Capstone Project"),
                new Span("Spring 2023"),
                new Span("Ioannis Christou, Ph.D."),
                about_syllabus
        );

        // developer
        developerLayout.add(new Span("Orestis Papadopoulos 241394, Undergraduate student"),
                            new Span("DEREE - The American College of Greece"),
                            new Span("Information Technology (Software Development)"),
                            new Span("o.papadopoulos@acg.edu"));

        // set accordion panel names
        AccordionPanel functionalityPanel = accordion.add("Functionality", functionalityLayout);
        functionalityPanel.addThemeVariants(DetailsVariant.FILLED);

        AccordionPanel prerequisitesPanel = accordion.add("Prerequisites", prerequisitesLayout);
        prerequisitesPanel.addThemeVariants(DetailsVariant.FILLED);

        AccordionPanel nameAndLogoPanel = accordion.add("Name and Logo", nameAndLogoLayout);
        nameAndLogoPanel.addThemeVariants(DetailsVariant.FILLED);

        AccordionPanel coursePanel = accordion.add("Course", courseLayout);
        coursePanel.addThemeVariants(DetailsVariant.FILLED);

        AccordionPanel developerPanel = accordion.add("Developer", developerLayout);
        developerPanel.addThemeVariants(DetailsVariant.FILLED);

        // add an empty image after the accordion so that the background
        // image is not cut when all accordion tabs are closed
        add(accordion, new Image("images/empty.png", ""));
    }
}
