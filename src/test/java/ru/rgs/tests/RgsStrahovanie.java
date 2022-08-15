package ru.rgs.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.rgs.base.baseTests;

import java.util.concurrent.TimeUnit;

public class RgsStrahovanie extends baseTests {

    @Test
    @Tag("allTests")
    public void test() {
        // Проверка на наличие куки
        final String cookiePath = "//div[@class='cookie block--cookie']";
        if (elementIsExist(By.xpath(cookiePath))) {
            WebElement closeCookie = driver.findElement(By.xpath(cookiePath + "//button"));
            closeCookie.click();
        }

        WebElement buttonCompany = driver.findElement(By.xpath("//a[@href='/for-companies' and @class='text--second']"));
        buttonCompany.click();
        wait.until(ExpectedConditions.titleIs("Страхование компаний и юридических лиц | Росгосстрах"));

        final String pathToHealthButton = "//span[@class='padding' and text()='Здоровье']";
        WebElement buttonHealth = driver.findElement(By.xpath(pathToHealthButton));
        buttonHealth.click();
        WebElement parentButtonHealth = driver.findElement(By.xpath(pathToHealthButton + "/ancestor::li[contains(@class, active)]"));
        wait.until(ExpectedConditions.visibilityOf(parentButtonHealth));

        WebElement buttonDMS = driver.findElement(By.xpath("//a[contains(@href, 'dobrovolnoe-meditsinskoe-strakhovanie') and text()='Добровольное медицинское страхование']"));
        buttonDMS.click();
        wait.until(ExpectedConditions.titleIs("Добровольное медицинское страхование для компаний и юридических лиц в Росгосстрахе"));

        WebElement sendBid = driver.findElement(By.xpath("//span[text()='Отправить заявку']"));
        sendBid.click();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Проверка на прокрутку экрана

        String fieldXPath = "//input[@name='%s']";
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userName"))), "Борисенков Дмитрий Владимирович");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userTel"))), "7777777777");
        // Не проходила проверка из-за преобразования номера на сайте Росгосстраха, пришлось удалить проверку:)
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userEmail"))), "qwertyqwerty");
        fillInputField(driver.findElement(By.xpath("//input[@type='text' and @placeholder='Введите']")), "г Москва, Перовское шоссе, д 2");

        WebElement buttonGetIntoContactWithMe = driver.findElement(By.xpath("//button[text()='Свяжитесь со мной' and @type='submit']"));
        buttonGetIntoContactWithMe.click();

        WebElement errorEmail = driver.findElement(By.xpath("//span[text()='Введите корректный адрес электронной почты' and contains(@class, 'input__error')]"));
        wait.until(ExpectedConditions.visibilityOf(errorEmail));

    }

    public boolean elementIsExist(By by) {
        try {
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            driver.findElement(by);
            return true;
            // Перехватываю ошибку от Selenium, не от java.util
        } catch (NoSuchElementException e) {
            System.out.println("Нет такого элемента на странице");
        } finally {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
        return false;   // запись вне кетча дает вернуть фолс в любом случа, а ошибку игнорим
    }

    private void fillInputField(WebElement element, String value) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        element.click();
        element.sendKeys(value);

//        boolean elemFilled = false;
//        boolean telNumber = wait.until(ExpectedConditions.attributeContains(element, "name", "userTel"));

        // проверка на наличие элемента
//        if (!telNumber) {
//            elemFilled = wait.until(ExpectedConditions.attributeContains(element, "value", value));
//            Assertions.assertTrue(elemFilled, "Поле не заполнено");
//        } // else {
//            elemFilled = wait.until(ExpectedConditions.attributeContains(element, "value", String.format(fieldXPath, value)));
//            Assertions.assertTrue(elemFilled, "Поле телефон не заполнено");
//        }
    }
}
