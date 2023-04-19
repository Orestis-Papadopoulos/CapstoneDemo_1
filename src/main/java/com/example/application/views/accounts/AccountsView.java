package com.example.application.views.accounts;

import com.example.application.backend.entity.Account;
import com.example.application.backend.entity.User;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
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
import com.vaadin.flow.component.select.Select;
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
import java.util.*;

import static com.example.application.backend.service.AccountService.*;
import static com.example.application.views.signIn.SignInView.getSignedInUser;

@PageTitle("Accounts")
@Route(value = "accounts", layout = MainLayout.class)
@PermitAll
public class AccountsView extends VerticalLayout {

    // get the signed-in user from the SignInView to populate the accounts table with their data
    User user = getSignedInUser();
    // widgets above grid
    HorizontalLayout btnLayout = new HorizontalLayout();
    Button btn_add_account = new Button(new Icon(VaadinIcon.PLUS));
    Button btn_show_hide = new Button(new Icon(VaadinIcon.EYE));
    Button btn_delete_selected = new Button(new Icon(VaadinIcon.TRASH));
    TextField searchField = new TextField();
    // dialog
    Dialog accountDialog = new Dialog();
    Button btn_save, btn_cancel;

    List<Account> accounts = new ArrayList<>();
    GridListDataView<Account> dataView; // for filtering
    Account account = new Account();
    Long account_id;
    Span tip = new Span();

    // form (in dialog)
    FormLayout accountFormLayout = new FormLayout();
    Binder<Account> binder = new BeanValidationBinder<>(Account.class);
    Select<String> selectDefaultAccount = new Select<>();
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

    // grid headers (they are customized with CSS)
    Span header_name = new Span("NAME");
    Span header_comment = new Span("COMMENT");
    Span header_date_modified = new Span("DATE MODIFIED");

    // account options
    MenuBar menuBar;
    MenuItem item_sign_in, item_edit, item_delete;

    // default accounts
    List<Account> defaultAccounts = new ArrayList<>();
    Account blackboard = new Account();
    Account myACG = new Account();
    Account netflix = new Account();
    Account github = new Account();
    // for deletion of multiple accounts
    Set<Account> selectedAccounts;

    public AccountsView() {
        binder.bindInstanceFields(this);
        setupDefaultAccounts();

        // set class name to modify only these Spans with CSS
        header_name.setClassName("header");
        header_comment.setClassName("header");
        header_date_modified.setClassName("header");

        // create grid
        Grid.Column<Account> nameColumn = grid.addColumn(Account::getAccount_name).setHeader(header_name).setSortable(true);
        Grid.Column<Account> commentColumn = grid.addColumn(Account::getComment).setHeader(header_comment).setSortable(true);
        Grid.Column<Account> dateModifiedColumn = grid.addColumn(Account::getDate_modified).setHeader(header_date_modified).setSortable(true);
        // do the following in case the browser window gets very narrow
        grid.setMinWidth(700, Unit.PIXELS);
        grid.setWidth("75%");
        grid.setAllRowsVisible(true);
        grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
        if (user != null && user.getSign_in_session_uuid() != null) {
            grid.setSelectionMode(Grid.SelectionMode.MULTI);
        }

        // implement multi-select to delete many items at once
        btn_delete_selected.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btn_delete_selected.setTooltipText("Delete the accounts you selected");
        btn_delete_selected.setEnabled(false);
        grid.addSelectionListener(selection -> {
            selectedAccounts = selection.getAllSelectedItems();
            btn_delete_selected.setEnabled(selectedAccounts.size() > 0);
        });
        btn_delete_selected.addClickListener(click_event -> deleteSelectedAccounts());

        // account options on right-most column
        grid.addComponentColumn(selectedAccount -> {
            menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);

            item_sign_in = menuBar.addItem("");
            item_sign_in.setEnabled(user != null && user.getSign_in_session_uuid() != null);
            item_sign_in.addComponentAsFirst(new HorizontalLayout(new Icon(VaadinIcon.ARROW_RIGHT), new Span("Sign In")));
            item_sign_in.addClickListener(event -> {
                // for better UX, a message saying something similar to "Redirecting...", and a loading icon should
                // be displayed; I tried to do that but the message is shown after the auto login has taken place
                try {
                    loginToAccount(selectedAccount);
                } catch (URISyntaxException | IOException e) {
                    throw new RuntimeException(e);
                }
            });

            item_edit = menuBar.addItem("");
            item_edit.setEnabled(user != null && user.getSign_in_session_uuid() != null);
            item_edit.addComponentAsFirst(new HorizontalLayout(new Icon(VaadinIcon.EDIT), new Span("Edit")));
            item_edit.addClickListener(event -> {
                setForm(selectedAccount);
                editAccount();
            });

            item_delete = menuBar.addItem("");
            item_delete.setEnabled(user != null && user.getSign_in_session_uuid() != null);
            item_delete.addComponentAsFirst(new HorizontalLayout(new Icon(VaadinIcon.TRASH), new Span("Delete")));
            item_delete.addClickListener(event -> {
                account_id = selectedAccount.getAccount_id();
                deleteAccount();
            });

            return menuBar;
        }).setWidth("70px").setFlexGrow(0);

        // 'New' btn
        btn_add_account.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btn_add_account.setTooltipText("Add a new account");
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
        searchField.setEnabled(false);

        // populate grid
        if (user != null && user.getSign_in_session_uuid() != null) {

            accounts = getAccountsByUuid(user.getUser_uuid());
            dataView = grid.setItems(accounts);
            if (accounts.isEmpty()) tip.setText("There are no accounts in the table.");

            // dialog setup
            setUpAccountDialog();

            // filtering: put it inside the 'if', otherwise the grid
            // is populated with accounts even when a user is not signed in
            searchField.addValueChangeListener(e -> dataView.refreshAll());
            searchField.setEnabled(true);

            dataView.addFilter(account -> {
                String searchTerm = searchField.getValue().trim();
                if (searchTerm.isEmpty()) return true;
                boolean matchesName = matches(account.getAccount_name(), searchTerm);
                boolean matchesComment = matches(account.getComment(), searchTerm);
                boolean matchesDateModified = matches(account.getDate_modified(), searchTerm);
                return matchesName || matchesComment || matchesDateModified;
            });

        } else {
            tip.setText("This is a preview. Sign in to add your accounts.");

            // add default accounts to grid
            grid.setItems(defaultAccounts);
        }

        btnLayout.add(searchField, btn_add_account, btn_delete_selected, btn_show_hide);
        add(btnLayout, grid, tip, accountDialog);
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

    public void setUpAccountDialog() {

        selectDefaultAccount.setLabel("You can select from a list of default accounts to save some time from filling the fields.");
        selectDefaultAccount.setEmptySelectionAllowed(true);
        selectDefaultAccount.setEmptySelectionCaption("No account selected");

        // add to the list only the accounts the user does not have
        // e.g., if a user has a Blackboard account, do not add Blackboard to the list
        List<String> userAccountUrls = new ArrayList<>();
        for (Account account : accounts) userAccountUrls.add(account.getLogin_page_url());

        List<String> defaultAccountsToShow = new ArrayList<>();
        for (Account account: defaultAccounts) {
            if (!userAccountUrls.contains(account.getLogin_page_url())) {
                defaultAccountsToShow.add(account.getAccount_name());
            }
        }
        if (defaultAccountsToShow.isEmpty()) {
            selectDefaultAccount.setEmptySelectionCaption("All default accounts have been added");
            selectDefaultAccount.setEnabled(false);
        } else selectDefaultAccount.setItems(defaultAccountsToShow);

        // Select listener
        selectDefaultAccount.addValueChangeListener(item -> {

            Account defaultSelectedAccount = null;
            for (Account account : defaultAccounts) {
                if (account.getAccount_name().equals(item.getValue())) {
                    defaultSelectedAccount = new Account(account);
                    break;
                }
            }
            if (defaultSelectedAccount == null) {
                resetForm();
                username.setPlaceholder("");
                password.setPlaceholder("");
                return;
            }

            // remind the user to add username and password
            username.setPlaceholder("Add your username");
            password.setPlaceholder("Add your password");

            // fill the fields
            account_name.setValue(defaultSelectedAccount.getAccount_name());
            comment.setValue(defaultSelectedAccount.getComment());
            login_page_url.setValue(defaultSelectedAccount.getLogin_page_url());
            username_css_selector.setValue(defaultSelectedAccount.getUsername_css_selector());
            password_css_selector.setValue(defaultSelectedAccount.getPassword_css_selector());
            btn_cookies_css_selector.setValue(defaultSelectedAccount.getBtn_cookies_css_selector() == null ? "" : defaultSelectedAccount.getBtn_cookies_css_selector());
            btn_login_css_selector.setValue(defaultSelectedAccount.getBtn_login_css_selector());

            // disable everything but the select, username, password
            account_name.setEnabled(false);
            comment.setEnabled(false);
            login_page_url.setEnabled(false);
            username_css_selector.setEnabled(false);
            password_css_selector.setEnabled(false);
            btn_cookies_css_selector.setEnabled(false);
            btn_login_css_selector.setEnabled(false);
        });

        btn_cookies_css_selector.setHelperText("Leave this empty if the website does not ask for cookies.");

        VerticalLayout dialogLayout = new VerticalLayout();
        accountFormLayout.setColspan(selectDefaultAccount, 2);
        accountFormLayout.setColspan(login_page_url, 2);
        accountFormLayout.add(selectDefaultAccount,
                account_name, comment,
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
        confirmDialog.setText("Are you sure you want to delete your \"" + account.getAccount_name() + "\" account from this application?");
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
        selectDefaultAccount.clear();
        List<TextField> dialogTextFields = Arrays.asList(account_name, comment, username, login_page_url,
                username_css_selector, password_css_selector, btn_cookies_css_selector, btn_login_css_selector);

        for (TextField field : dialogTextFields) {
            field.clear();
            field.setInvalid(false);
            field.setEnabled(true);
        }

        // this is a PasswordField, not a TextField
        password.clear();
        password.setInvalid(false);
        password.setEnabled(true);
    }

    public void setupDefaultAccounts() {
        blackboard.setAccount_name("Blackboard");
        blackboard.setComment("Deree courses");
        blackboard.setDate_modified(dateFormat.format(new Date()));
        blackboard.setLogin_page_url("https://blackboard.acg.edu/");
        blackboard.setUsername_css_selector("#user_id");
        blackboard.setPassword_css_selector("#password");
        blackboard.setBtn_cookies_css_selector("#agree_button");
        blackboard.setBtn_login_css_selector("#entry-login");

        myACG.setAccount_name("myACG");
        myACG.setComment("Deree account");
        myACG.setDate_modified(dateFormat.format(new Date()));
        myACG.setLogin_page_url("https://campusweb.acg.edu/ics");
        myACG.setUsername_css_selector("#userName");
        myACG.setPassword_css_selector("#password");
        myACG.setBtn_login_css_selector("#siteNavBar_btnLogin");

        netflix.setAccount_name("Netflix");
        netflix.setComment("Movie streaming");
        netflix.setDate_modified(dateFormat.format(new Date()));
        netflix.setLogin_page_url("https://www.netflix.com/gr-en/login?nextpage=https%3A%2F%2Fwww.netflix.com%2Fbrowse");
        netflix.setUsername_css_selector("#id_userLoginId");
        netflix.setPassword_css_selector("#id_password");
        netflix.setBtn_login_css_selector(".btn");

        github.setAccount_name("GitHub");
        github.setComment("Version control");
        github.setDate_modified(dateFormat.format(new Date()));
        github.setLogin_page_url("https://github.com/login");
        github.setUsername_css_selector("#login_field");
        github.setPassword_css_selector("#password");
        github.setBtn_login_css_selector(".btn");

        defaultAccounts = Arrays.asList(blackboard, myACG, netflix, github);
    }

    public void deleteSelectedAccounts() {
        // open confirm dialog
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Confirm Account Deletion");
        confirmDialog.setText("Are you sure you want to delete the selected account(s) from this application?");
        confirmDialog.setConfirmText("Yes, delete");

        // delete account on confirm click
        confirmDialog.addConfirmListener(click_event -> {
            for (Account account: selectedAccounts) deleteAccountFromDatabase(account);
            UI.getCurrent().getPage().reload();
        });

        confirmDialog.setRejectable(true);
        confirmDialog.setRejectText("No, cancel");
        confirmDialog.addRejectListener(click_event -> confirmDialog.close());
        confirmDialog.open();
    }
}
