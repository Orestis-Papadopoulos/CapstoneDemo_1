package com.example.application.views.about;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@AnonymousAllowed
public class AboutView extends VerticalLayout {

    Accordion accordion = new Accordion();
    VerticalLayout prerequisitesLayout = new VerticalLayout();
    VerticalLayout courseLayout = new VerticalLayout();

    public AboutView() {

        accordion.setWidth("50%");

        // layouts
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

        AccordionPanel functionalityPanel = accordion.add("Functionality", new Span("This is what the app does."));
        functionalityPanel.addThemeVariants(DetailsVariant.FILLED);

        AccordionPanel prerequisitesPanel = accordion.add("Prerequisites", prerequisitesLayout);
        prerequisitesPanel.addThemeVariants(DetailsVariant.FILLED);

        AccordionPanel coursePanel = accordion.add("Course", courseLayout);
        coursePanel.addThemeVariants(DetailsVariant.FILLED);

        AccordionPanel developerPanel = accordion.add("Developer", new Span("About me."));
        developerPanel.addThemeVariants(DetailsVariant.FILLED);

        add(accordion);
    }
}
