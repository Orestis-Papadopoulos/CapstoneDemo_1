package com.example.application.testing;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlDivision;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSubmitInput;

import java.io.IOException;
import java.net.URL;

public class Test2 {

    public void openURLWithHTMLUnit() throws IOException {
        WebClient webClient = new WebClient();
        HtmlPage page = webClient.getPage("google.com"); // gives an error

//        HtmlInput searchBox = page.getElementByName("q");
//        searchBox.setValueAttribute("htmlunit");
//
//        HtmlSubmitInput googleSearchSubmitButton =
//                page.getElementByName("btnG"); // sometimes it's "btnK"
//        page=googleSearchSubmitButton.click();
//
//        HtmlDivision resultStatsDiv =
//                page.getFirstByXPath("//div[@id='resultStats']");

//        System.out.println(resultStatsDiv.asText()); // About 309,000 results
//        webClient.closeAllWindows();
    }
}
