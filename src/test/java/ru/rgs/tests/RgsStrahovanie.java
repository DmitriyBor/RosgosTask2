package ru.rgs.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import ru.rgs.base.baseTests;

import java.util.concurrent.TimeUnit;

public class RgsStrahovanie extends baseTests {

    @Test
    @Tag("fullTests")
    public void test() {
        // Проверка на наличие куки
        final String cookiePath = "//div[@class='cookie block--cookie']";
        if (elementIsExist(By.xpath(cookiePath))) {
            WebElement closeCookie = driver.findElement(By.xpath(cookiePath + "//button"));
            closeCookie.click();
        }


    }

    public boolean elementIsExist(By by){
        try {
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            driver.findElement(by);
            return true;
            // Перехватываю ошибку от Selenium, не от juva.util
        } catch (NoSuchElementException e) {
            System.out.println("Нет такого элемента на странице");
        }
        finally {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
        return false;   // запись вне кетча дает вернуть фолс в любом случа, а ошибку игнорим
    }
}
