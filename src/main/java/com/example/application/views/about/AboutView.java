package com.example.application.views.about;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * A view which aims to inform about the application, the course, and the developer.
 * */

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@AnonymousAllowed
public class AboutView extends VerticalLayout {

    Accordion accordion = new Accordion();
    VerticalLayout functionalityLayout = new VerticalLayout();
    VerticalLayout prerequisitesLayout = new VerticalLayout();
    VerticalLayout courseLayout = new VerticalLayout();
    VerticalLayout developerLayout = new VerticalLayout();

    public AboutView() {

        accordion.setWidth("50%");
        // do the following in case the browser window gets very narrow
        accordion.setMinWidth(600, Unit.PIXELS);

        // layouts
        functionalityLayout.add(new Span("Qard offers a unique way of signing in, by using your smartphone and a " +
                "proximity card. By doing so you can organize your online account credentials and sign in to the " +
                "account of your choice automatically. Add the account information in the relevant table and enjoy " +
                "your online experience without typing usernames and passwords."));

        prerequisitesLayout.add(new Span("1. An Android smartphone with NFC"),
                                new Span("2. An Internet connection, both on your PC and smartphone"),
                                new Span("3. A MIFARE Classic proximity card"),
                                new Span("4. The Qard authentication app installed on your smartphone"),
                                new Span("5. The Firefox web browser"));

        courseLayout.add(new Span("ITC4918 Software Development Capstone Project"),
                new Span("Spring 2023"),
                new Span("Ioannis Christou, Ph.D."),
                new Span("The course is the capstone experience for students following the\n" +
                        "Software Development pathway of the IT programme. Students are\n" +
                        "required to develop a complete solution to a given problem, based\n" +
                        "on the knowledge and the skills acquired during their studies.\n" +
                        "Moreover, students will gain the experience of working on a realtime\n" +
                        "basis and combine academic with professional practices,\n" +
                        "including domain-specific research, gradual progression, revisiting,\n" +
                        "and evaluation."));

        developerLayout.add(new Span("Orestis Papadopoulos 241394, Undergraduate student"),
                            new Span("DEREE - The American College of Greece"),
                            new Span("Information Technology (Software Development)"),
                            new Span("o.papadopoulos@acg.edu"));

        AccordionPanel functionalityPanel = accordion.add("Functionality", functionalityLayout);
        functionalityPanel.addThemeVariants(DetailsVariant.FILLED);

        AccordionPanel prerequisitesPanel = accordion.add("Prerequisites", prerequisitesLayout);
        prerequisitesPanel.addThemeVariants(DetailsVariant.FILLED);

        AccordionPanel coursePanel = accordion.add("Course", courseLayout);
        coursePanel.addThemeVariants(DetailsVariant.FILLED);

        AccordionPanel developerPanel = accordion.add("Developer", developerLayout);
        developerPanel.addThemeVariants(DetailsVariant.FILLED);

        add(accordion);
    }
}
