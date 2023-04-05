package com.example.application.views.accounts;

import com.example.application.backend.entity.Account;
import com.example.application.backend.entity.User;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.security.PermitAll;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import static com.example.application.backend.service.AccountService.*;
import static com.example.application.views.signIn.SignInView.getSignedInUser;

@PageTitle("Accounts")
@Route(value = "accounts", layout = MainLayout.class)
@PermitAll
public class AccountsView extends VerticalLayout {

    // get the signed-in user from the SignInView to populate the accounts table with their data
    User user = getSignedInUser();
    // buttons above grid
    HorizontalLayout btnLayout = new HorizontalLayout();
    Button btn_add_account = new Button(new Icon(VaadinIcon.PLUS));
    Button btn_show_hide = new Button(new Icon(VaadinIcon.EYE));
    TextField searchField = new TextField();
    // dialog
    Dialog accountDialog = new Dialog();
    Button btn_save, btn_cancel;
    List<Account> accounts;
    GridListDataView<Account> dataView;
    Account account = new Account();
    Long account_id;
    Span tip = new Span();

    // form
    FormLayout accountFormLayout = new FormLayout();
    Binder<Account> binder = new BeanValidationBinder<>(Account.class);
    TextField account_name = new TextField("Name");
    TextField comment = new TextField("Comment");
    TextField username = new TextField("Username");
    PasswordField password = new PasswordField("Password");
    TextField login_page_url = new TextField("Login Page URL");
    TextField username_css_selector = new TextField("Username CSS Selector");
    TextField password_css_selector = new TextField("Password CSS Selector");
    TextField btn_cookies_css_selector = new TextField("Cookies Button CSS Selector");
    TextField btn_login_css_selector = new TextField("Login Button CSS Selector");

    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);
    Grid<Account> grid = new Grid<>(Account.class, false);

    // redirect dialog
    Dialog redirectDialog = new Dialog();
    VerticalLayout redirectLayout = new VerticalLayout();
    ProgressBar progressBar = new ProgressBar();

    // grid headers
    Span header_name = new Span("NAME");
    Span header_comment = new Span("COMMENT");
    Span header_date_modified = new Span("DATE MODIFIED");

    public AccountsView() {
        // set class name to modify only these Spans with css
        header_name.setClassName("header");
        header_comment.setClassName("header");
        header_date_modified.setClassName("header");

        binder.bindInstanceFields(this);

        // create grid
        Grid.Column<Account> nameColumn = grid.addColumn(Account::getAccount_name).setHeader(header_name).setSortable(true);
        Grid.Column<Account> commentColumn = grid.addColumn(Account::getComment).setHeader(header_comment).setSortable(true);
        Grid.Column<Account> dateModifiedColumn = grid.addColumn(Account::getDate_modified).setHeader(header_date_modified).setSortable(true);
        grid.setWidth("75%");
        grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);

        //grid.asSingleSelect().addValueChangeListener(event -> setForm(event.getValue()));

        // account options on right-most column
        grid.addComponentColumn(selectedAccount -> {
            MenuBar menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
            menuBar.addItem("", event -> {
                        //redirectDialog.open(); // OPENS AFTER THE AUTO LOGIN HAS TAKEN PLACE, AND PREVENTS NAVIGATING TO THE NEW WINDOW
                        try {
                            loginToAccount(selectedAccount);
                        } catch (URISyntaxException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).addComponentAsFirst(new HorizontalLayout(new Icon(VaadinIcon.ARROW_RIGHT), new Span("Sign In")));

            menuBar.addItem("", event -> {
                        setForm(selectedAccount);
                        editAccount();
                    }).addComponentAsFirst(new HorizontalLayout(new Icon(VaadinIcon.EDIT), new Span("Edit")));

            menuBar.addItem("", event -> {
                        account_id = selectedAccount.getAccount_id();
                        deleteAccount();
                    }).addComponentAsFirst(new HorizontalLayout(new Icon(VaadinIcon.TRASH), new Span("Delete")));

            return menuBar;
        }).setWidth("70px").setFlexGrow(0);

        // dialog setup
        setupDialog();

        // 'New' btn
        btn_add_account.setText("New");
        if (user != null) btn_add_account.setEnabled(user.getSign_in_session_uuid() != null);
        else btn_add_account.setEnabled(false);

        btn_add_account.addClickListener(click_event -> {
            accountDialog.setHeaderTitle("New Account");
            resetForm();
            accountDialog.open();
        });

        // 'show/hide' btn
        btn_show_hide.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btn_show_hide.setTooltipText("Select which columns are shown");
        ColumnToggleContextMenu columnToggleContextMenu = new ColumnToggleContextMenu(btn_show_hide);
        columnToggleContextMenu.addColumnToggleItem("Name", nameColumn);
        columnToggleContextMenu.addColumnToggleItem("Comment", commentColumn);
        columnToggleContextMenu.addColumnToggleItem("Date Modified", dateModifiedColumn);

        // for filtering
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        // populate grid
        if (user != null && user.getSign_in_session_uuid() != null) {
            accounts = getAccountsByUuid(user.getUser_uuid());
            dataView = grid.setItems(accounts);
            if (accounts.isEmpty()) tip.setText("There are no accounts in the table.");

            // filtering: put it inside the 'if', otherwise the grid
            // is populated with accounts even when a user is not signed in
            accounts = getAccountsByUuid(user.getUser_uuid());
            dataView = grid.setItems(accounts);
            searchField.addValueChangeListener(e -> dataView.refreshAll());

            dataView.addFilter(account -> {
                String searchTerm = searchField.getValue().trim();
                if (searchTerm.isEmpty()) return true;
                boolean matchesName = matches(account.getAccount_name(), searchTerm);
                boolean matchesComment = matches(account.getComment(), searchTerm);
                boolean matchesDateModified = matches(account.getDate_modified(), searchTerm);
                return matchesName || matchesComment || matchesDateModified;
            });

        } else tip.setText("Sign in to add your accounts.");

        // redirect
        progressBar.setIndeterminate(true);
        //redirectDialog.setHeaderTitle("Redirecting ...");
        redirectLayout.add(new Span("Redirecting to account..."), progressBar);
        redirectDialog.add(redirectLayout);

        btnLayout.add(btn_add_account, searchField, btn_show_hide);
        add(btnLayout, grid, tip, accountDialog, redirectDialog);
    }

    // for filtering
    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
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

        btn_cookies_css_selector.setHelperText("Leave this empty if the website does not ask for cookies.");

        VerticalLayout dialogLayout = new VerticalLayout();
        accountFormLayout.setColspan(login_page_url, 2);
        accountFormLayout.add(account_name, comment,
                username, password, login_page_url,
                username_css_selector, password_css_selector,
                btn_cookies_css_selector, btn_login_css_selector);

        dialogLayout.add(accountFormLayout);
        accountDialog.add(dialogLayout);
        accountDialog.setWidth("50%");

        btn_save = new Button("Save", click_event -> updateAccounts());
        btn_cancel = new Button("Cancel", click_event -> {
            account_id = null;
            accountDialog.close();
        });
        btn_cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        accountDialog.getFooter().add(btn_cancel, btn_save);
    }

    public void updateAccounts() {
        if (account_id != null) account = getAccountByAccountId(account_id);
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

    private static void loginToAccount(Account selectedAccount) throws URISyntaxException, IOException {

        String url = selectedAccount.getLogin_page_url();
        String username = selectedAccount.getUsername();
        String password = selectedAccount.getPassword();
        String username_css_selector = selectedAccount.getUsername_css_selector();
        String password_css_selector = selectedAccount.getPassword_css_selector();
        String btn_cookies_css_selector = selectedAccount.getBtn_cookies_css_selector();
        String btn_login_css_selector = selectedAccount.getBtn_login_css_selector();

        // driver setup
        WebDriverManager.firefoxdriver().setup();
        WebDriver driver = new FirefoxDriver();

        // opens URL in another window, cannot open in new tab
        driver.get(url);

        // this is the window where this app is opened
        String originalWindow = driver.getWindowHandle();

        // wait until the page loads, and then click the 'OK' button for cookies
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        if (!btn_cookies_css_selector.equals("")) {
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(btn_cookies_css_selector)));
            driver.findElement(By.cssSelector(btn_cookies_css_selector)).click();
        } else {
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(username_css_selector)));
        }

        // fill username
        driver.findElement(By.cssSelector(username_css_selector)).sendKeys(username);
        // fill password
        driver.findElement(By.cssSelector(password_css_selector)).sendKeys(password);
        // click login button
        driver.findElement(By.cssSelector(btn_login_css_selector)).click();

        // navigate to the browser window opened by Selenium
        for (String windowHandle : driver.getWindowHandles()) {
            if(!originalWindow.contentEquals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
    }

    private void editAccount() {
        accountDialog.setHeaderTitle("Edit Account");
        accountDialog.open();
    }

    private void deleteAccount() {
        account = getAccountByAccountId(account_id);
        // open confirm dialog
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Confirm Account Deletion");
        confirmDialog.setText("Are you sure you want to delete your \"" + account.getAccount_name() + "\" account?");
        confirmDialog.setConfirmText("Yes, delete");

        // delete account on confirm click
        confirmDialog.addConfirmListener(click_event -> {
            deleteAccountFromDatabase(account);
            UI.getCurrent().getPage().reload();
        });

        confirmDialog.setRejectable(true);
        confirmDialog.setRejectText("No, cancel");
        confirmDialog.addRejectListener(click_event -> confirmDialog.close());
        confirmDialog.open();
    }

    public void setForm(Account selectedAccount) {
        account_id = selectedAccount.getAccount_id();
        account_name.setValue(selectedAccount.getAccount_name());
        comment.setValue(selectedAccount.getComment());
        username.setValue(selectedAccount.getUsername());
        password.setValue(selectedAccount.getPassword());
        login_page_url.setValue(selectedAccount.getLogin_page_url());
        username_css_selector.setValue(selectedAccount.getUsername_css_selector());
        password_css_selector.setValue(selectedAccount.getPassword_css_selector());
        btn_cookies_css_selector.setValue(selectedAccount.getBtn_cookies_css_selector());
        btn_login_css_selector.setValue(selectedAccount.getBtn_login_css_selector());
    }

    public void resetForm() {
        account_name.clear();
        account_name.setInvalid(false);
        comment.clear();
        comment.setInvalid(false);
        username.clear();
        username.setInvalid(false);
        password.clear();
        password.setInvalid(false);
        login_page_url.clear();
        login_page_url.setInvalid(false);
        username_css_selector.clear();
        username_css_selector.setInvalid(false);
        password_css_selector.clear();
        password_css_selector.setInvalid(false);
        btn_cookies_css_selector.clear();
        btn_cookies_css_selector.setInvalid(false);
        btn_login_css_selector.clear();
        btn_login_css_selector.setInvalid(false);
    }
}
