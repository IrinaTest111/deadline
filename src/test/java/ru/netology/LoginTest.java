package ru.netology;

import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;

import ru.netology.page.LoginPage;
import ru.netology.page.VerificationPage;


import static com.codeborne.selenide.Selenide.open;

public class LoginTest {
    private LoginPage loginPage;


    @BeforeEach
    void setUp() {

        loginPage = open("http://localhost:9999", LoginPage.class);
    }

    @AfterAll
    static void tearDownAll() {
        SQLHelper.cleanDatabase();
    }

    @BeforeEach
    void tearDown() {
        SQLHelper.cleanAuthCodes();
    }


    @Test
    @DisplayName("Should successfully login to dashboard with exist login and password from sut test data")
    void shouldSuccessfullyLogin() throws InterruptedException {
        DataHelper.AuthInfo authInfo = DataHelper.getAuthInfoWithTestData();
        VerificationPage verificationPage = loginPage.validLogin(authInfo);

        Thread.sleep(3000);

        String verificationCode = SQLHelper.getVerificationCode();  // Теперь возвращает String
        verificationPage.validVerify(verificationCode);
    }

    @Test
    @DisplayName("Should get error notification if user is not exist in base")
    void shouldGetErrorNotificationIfLoginWithRandomUserWithoutAddingToBase() {
        DataHelper.AuthInfo authInfo = DataHelper.generateRandomUser();
        loginPage.login(authInfo);
        loginPage.verifyErrorNotification("Ошибка! Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("Should get error notification if login with exist in base and active user and random verification code")
    void shouldGetErrorNotificationIfLoginWithExistUserAndRandomVerificationCode() throws InterruptedException {

        DataHelper.AuthInfo authInfo = DataHelper.getAuthInfoWithTestData();
        VerificationPage verificationPage = loginPage.validLogin(authInfo);

        Thread.sleep(3000);

        String randomCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(randomCode);

        verificationPage.verifyErrorNotification("Ошибка! Неверно указан код! Попробуйте ещё раз.");
    }


    @Test
    @DisplayName("Should get error notification with invalid verification code")
    void shouldGetErrorNotificationWithInvalidVerificationCode() {

        DataHelper.AuthInfo authInfo = DataHelper.getAuthInfoWithTestData();
        VerificationPage verificationPage = loginPage.validLogin(authInfo);

        String randomCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(randomCode);

        verificationPage.verifyErrorNotification("Ошибка! Неверно указан код! Попробуйте ещё раз.");
    }

    @Test
    @DisplayName("Should block user after three invalid login attempts")
    void shouldBlockUserAfterThreeInvalidLoginAttempts() {

        DataHelper.AuthInfo invalidAuthInfo = DataHelper.generateRandomUser();

        for (int i = 0; i < 3; i++) {
            loginPage.login(invalidAuthInfo);

            loginPage.verifyErrorNotification("Ошибка! Неверно указан логин или пароль");
        }

        loginPage.login(invalidAuthInfo);

        loginPage.verifyErrorNotification("Пользователь заблокирован");

    }

}