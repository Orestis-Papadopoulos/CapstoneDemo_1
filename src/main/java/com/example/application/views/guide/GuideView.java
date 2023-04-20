package com.example.application.views.guide;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Guide")
@Route(value = "guide", layout = MainLayout.class)
public class GuideView extends VerticalLayout {

    public GuideView() {

        add(new H3("How to Add a New Account"));
        add(new H4("1. Make sure you are using Firefox as your web browser on your computer."));
        add(new H4("2. Make sure you've registered and signed in."));

        // step 3
        add(new H4("3. Navigate to \"Accounts\", and click the cross icon to add a new account."));
        Image img_new_account = new Image("images/new_account.png", "");
        img_new_account.setWidth("100%");
        add(img_new_account, getDividerLayout());

        // step 4
        add(new H4("4. If your account does not exist in the list of default accounts, navigate to the login page of the " +
                "account you want to add and copy its URL. Then paste it in the respective field of the opened dialog."));
        Image img_copy_url = new Image("images/copy_url.png", "");
        img_copy_url.setWidth("100%");
        add(img_copy_url, getDividerLayout());

        // step 5
        add(new H4("5. Make sure you have deleted the cookies for this site. When the automatic sign in is performed " +
                "the \"OK\" button for cookie consent also needs to be clicked."));

        // step 6
        add(new H4("6. Right click on the cookie button >> click \"Inspect\""));
        Image img_inspect = new Image("images/inspect.png", "");
        img_inspect.setWidth("100%");
        add(img_inspect, getDividerLayout());

        // step 7
        add(new H4("7. Right click on the blue area >> go to \"Copy\" >> click \"CSS Selector\""));
        Image img_copy_css_selector = new Image("images/copy_css_selector.png", "");
        img_copy_css_selector.setWidth("100%");
        add(img_copy_css_selector, getDividerLayout());

        // step 8
        add(new H4("8. Paste the CSS selector in the respective field. Notice also the URL."));
        Image img_paste_css_selector = new Image("images/paste_ok_btn_css_selector.png", "");
        img_paste_css_selector.setWidth("100%");
        add(img_paste_css_selector, getDividerLayout());

        // step 9
        add(new H4("9. Repeat steps 6, 7, and 8 for the username, password, and login button."));
        Image img_repeat_steps = new Image("images/repeat_steps.png", "");
        img_repeat_steps.setWidth("100%");
        add(img_repeat_steps, getDividerLayout());

        // step 10
        add(new H4("10. Add the \"Name\" and \"Comment\" fields with the information of your choice. Add your username and password " +
                "corresponding to the online account you are adding. Then click \"Save\"."));
        Image img_name_comment_credentials = new Image("images/name_comment_credentials.png", "");
        img_name_comment_credentials.setWidth("100%");
        add(img_name_comment_credentials, getDividerLayout());

        // step 11
        add(new H4("11. Note that in case of Blackboard, it is one of the default accounts. If you select it, most fields are " +
                "filled automatically and disabled. You need only provide your credentials and save."));
        Image img_fields_disabled = new Image("images/fields_disabled.png", "");
        img_fields_disabled.setWidth("100%");
        add(img_fields_disabled, getDividerLayout());

        // step 12
        add(new H4("12. After the account is created, it can be seen in the table. Click the table's right-most icon " +
                "in the row of the account. Click on \"Sign in\". Wait for Firefox to open a new window where it will " +
                "sign you in to the account."));
        Image img_sign_in = new Image("images/sign_in.png", "");
        img_sign_in.setWidth("100%");
        add(img_sign_in, getDividerLayout());

        add(new H4("Notice how the search bar is red. This is the case when a URL is opened programmatically with Selenium. " +
                "Also, the web app signed in to Blackboard as \"Orestis Papadopoulos\"."));
        Image img_blackboard_signed_in = new Image("images/blackboard_signed_in.png", "");
        img_blackboard_signed_in.setWidth("100%");
        add(img_blackboard_signed_in, getDividerLayout());
    }

    /**
     * Creates a new instance of a vertical layout with a divider image.
     * An instance of a component cannot be added more than once in the UI.
     * A new instance of the component must be created every time you want to add it.
     * */
    public VerticalLayout getDividerLayout() {
        Image img_divider = new Image("images/divider.png", "");
        img_divider.setWidth("10%");
        VerticalLayout dividerLayout = new VerticalLayout(img_divider);
        dividerLayout.setAlignItems(Alignment.CENTER);
        return dividerLayout;
    }
}
