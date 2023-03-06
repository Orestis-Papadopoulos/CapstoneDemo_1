package com.example.application.views.accounts;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Accounts")
@Route(value = "accounts", layout = MainLayout.class)
public class AccountsView extends VerticalLayout {
}
