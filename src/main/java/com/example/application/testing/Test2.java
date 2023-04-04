package com.example.application.testing;

import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlDivision;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSubmitInput;

import java.io.IOException;
import java.net.URL;

public class Test2 {

    // HTML Unit simulates a browser for testing purposes and is intended to be used within another testing framework such as JUnit or TestNG
    //
    public void openURLWithHTMLUnit() throws IOException {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX);
        HtmlPage page = webClient.getPage("https://google.com"); // gives an error

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
