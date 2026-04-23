package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement loginField = $("input[name='login']");
    private final SelenideElement passwordField = $("input[name='password']");
    private final SelenideElement loginButton = $("button");
    private final SelenideElement errorNotification = $(".notification__content");

    public void open() {
        com.codeborne.selenide.Selenide.open("http://localhost:9999");
    }

    public void verifyErrorNotification(String expectedText) {
        errorNotification.shouldHave(text(expectedText)).shouldBe(visible);
    }

    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        login(info);
        return new VerificationPage();
    }

    public void login(DataHelper.AuthInfo info) {
        loginField.setValue(info.getLogin());
        passwordField.setValue(info.getPassword());
        loginButton.click();
    }

    public String getErrorText() {
        errorNotification.shouldBe(Condition.visible);
        return errorNotification.getText();
    }
}