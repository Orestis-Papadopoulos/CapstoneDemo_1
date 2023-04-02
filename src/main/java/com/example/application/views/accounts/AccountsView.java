package com.example.application.views.accounts;

import com.example.application.backend.entity.Account;
import com.example.application.backend.entity.User;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.example.application.backend.service.AccountService.getAccountsByUuid;
import static com.example.application.backend.service.AccountService.saveAccountToDatabase;
import static com.example.application.views.signIn.SignInView.getSignedInUser;

@PageTitle("Accounts")
@Route(value = "accounts", layout = MainLayout.class)
@PermitAll
public class AccountsView extends VerticalLayout {

    // I have to get the signed-in user from the SignInView to populate the accounts table with their data
    User user = getSignedInUser();
    HorizontalLayout btnLayout = new HorizontalLayout();
    FormLayout accountFormLayout = new FormLayout();
    Button btn_add_account = new Button(new Icon(VaadinIcon.PLUS));
    Button btn_show_hide = new Button(new Icon(VaadinIcon.EYE));
    Dialog accountDialog = new Dialog();
    Button btn_save, btn_cancel;
    List<Account> accounts;
    Account account = new Account();
    Span tip = new Span();

    // fields
    Binder<Account> binder = new BeanValidationBinder<>(Account.class);
    TextField account_name = new TextField("Name");
    TextField comment = new TextField("Comment");
    TextField login_page_url = new TextField("Login Page URL");
    TextField username_css_selector = new TextField("Username CSS Selector");
    TextField password_css_selector = new TextField("Password CSS Selector");
    TextField btn_login_css_selector = new TextField("Login Button CSS Selector");
    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);

    public AccountsView() {
        binder.bindInstanceFields(this);

        // create grid
        Grid<Account> grid = new Grid<>(Account.class, false);
        Grid.Column<Account> nameColumn = grid.addColumn(Account::getAccount_name).setHeader("Name");
        Grid.Column<Account> commentColumn = grid.addColumn(Account::getComment).setHeader("Comment");
        Grid.Column<Account> dateModifiedColumn = grid.addColumn(Account::getDate_modified).setHeader("Date Modified");
        grid.setWidth("75%");

        // dialog setup
        setupDialog();
        btn_save = new Button("Save", click_event -> createAccount());
        btn_cancel = new Button("Cancel", click_event -> accountDialog.close());
        btn_cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        accountDialog.getFooter().add(btn_cancel, btn_save);

        // 'New' btn
        btn_add_account.setText("New");
        if (user != null) btn_add_account.setEnabled(user.getSign_in_session_uuid() != null);
        else btn_add_account.setEnabled(false);
        btn_add_account.addClickListener(click_event -> {
            accountDialog.setHeaderTitle("New Account");
            accountDialog.open();
        });

        // 'show/hide' btn
        btn_show_hide.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btn_show_hide.setTooltipText("Select which columns are shown");
        ColumnToggleContextMenu columnToggleContextMenu = new ColumnToggleContextMenu(btn_show_hide);
        columnToggleContextMenu.addColumnToggleItem("Name", nameColumn);
        columnToggleContextMenu.addColumnToggleItem("Comment", commentColumn);
        columnToggleContextMenu.addColumnToggleItem("Date Modified", dateModifiedColumn);

        // populate grid
        if (user != null && user.getSign_in_session_uuid() != null) {
            accounts = getAccountsByUuid(user.getUser_uuid());
            grid.setItems(accounts);
            if (accounts.isEmpty()) tip.setText("There are no accounts in the table.");

        } else tip.setText("Only signed in users can add accounts.");

        btnLayout.add(btn_add_account, btn_show_hide);
        add(btnLayout, grid, tip, accountDialog);
    }

    // for 'show/hide' btn
    private static class ColumnToggleContextMenu extends ContextMenu {
        public ColumnToggleContextMenu(Component target) {
            super(target);
            setOpenOnClick(true);
        }

        void addColumnToggleItem(String label, Grid.Column<Account> column) {
            MenuItem menuItem = this.addItem(label, e -> {
                column.setVisible(e.getSource().isChecked());
            });
            menuItem.setCheckable(true);
            menuItem.setChecked(column.isVisible());
        }
    }

    public void setupDialog() {

        VerticalLayout dialogLayout = new VerticalLayout();
        accountFormLayout.setColspan(login_page_url, 2);
        accountFormLayout.setColspan(btn_login_css_selector, 2);
        accountFormLayout.add(account_name, comment, login_page_url, username_css_selector, password_css_selector, btn_login_css_selector);

        dialogLayout.add(accountFormLayout);
        accountDialog.add(dialogLayout);
        accountDialog.setWidth("50%");
    }

    public void createAccount() {
        account.setUser_uuid(user.getUser_uuid());
        account.setDate_modified(dateFormat.format(new Date()));

        if (binder.validate().isOk()) {
            try {
                binder.writeBean(account);
                saveAccountToDatabase(account);
                accountDialog.close();
                UI.getCurrent().getPage().reload();

            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
