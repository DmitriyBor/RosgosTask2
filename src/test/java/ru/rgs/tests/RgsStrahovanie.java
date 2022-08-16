package ru.rgs.tests;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.rgs.base.baseTests;
import ru.rgs.myannotatin.allTests;

import java.util.concurrent.TimeUnit;

public class RgsStrahovanie extends baseTests {

    @allTests
    public void test() {
        waitForPageStability(10000, 1000);

        // Проверка на наличие куки
        final String cookiePath = "//div[@class='cookie block--cookie']";
        if (elementIsExist(By.xpath(cookiePath), 2)) {
            WebElement closeCookie = driver.findElement(By.xpath(cookiePath + "//button"));
            waitAndCloseWidget();
            closeCookie.click();
        }

        waitAndCloseWidget();
        WebElement buttonCompany = driver.findElement(By.xpath("//a[@href='/for-companies' and @class='text--second']"));
        buttonCompany.click();
        wait.until(ExpectedConditions.titleIs("Страхование компаний и юридических лиц | Росгосстрах"));

        waitAndCloseWidget();
        final String pathToHealthButton = "//span[@class='padding' and text()='Здоровье']";
        WebElement buttonHealth = driver.findElement(By.xpath(pathToHealthButton));
        buttonHealth.click();
        WebElement parentButtonHealth = driver.findElement(By.xpath(pathToHealthButton + "/ancestor::li[contains(@class, active)]"));
        wait.until(ExpectedConditions.visibilityOf(parentButtonHealth));

        WebElement buttonDMS = driver.findElement(By.xpath("//a[contains(@href, 'dobrovolnoe-meditsinskoe-strakhovanie') and text()='Добровольное медицинское страхование']"));
        buttonDMS.click();
        wait.until(ExpectedConditions.titleIs("Добровольное медицинское страхование для компаний и юридических лиц в Росгосстрахе"));

        waitAndCloseWidget();
//        WebElement sendBid = driver.findElement(By.xpath("//span[text()='Отправить заявку']"));
//        sendBid.click();
        // 1. ожидать выполнения всех js скриптов
//        wait.until(completedJSPage());      // разработчики не позаботились о флаге readyState поэтому не работает
        // 2. ожидать выполнения всех ajax скриптов
        // 3. ожидать стабилизации страницы
        jsExecutor.executeScript("arguments[0].scrollIntoView(false)", driver.findElement(By.xpath("//button[text()='Свяжитесь со мной']")));
        waitForPageStability(10000, 1000);
//        jsExecutor.executeScript("arguments[0].scrollIntoView()", driver.findElement(By.xpath("//button[text()='Свяжитесь со мной']")));
        // scrollIntoView скролит до определенного элемента
        // Проверка на прокрутку экрана

        String fieldXPath = "//input[@name='%s']";
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userName"))), "Борисенков Дмитрий Владимирович");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userTel"))), "7776665555");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userEmail"))), "qwertyqwerty");
        fillInputField(driver.findElement(By.xpath("//input[@type='text' and @placeholder='Введите']")), "г Москва, Перовское шоссе, д 2");

        WebElement buttonGetIntoContactWithMe = driver.findElement(By.xpath("//button[text()='Свяжитесь со мной' and @type='submit']"));
        buttonGetIntoContactWithMe.click();

        WebElement errorEmail = driver.findElement(By.xpath("//span[text()='Введите корректный адрес электронной почты' and contains(@class, 'input__error')]"));
        wait.until(ExpectedConditions.visibilityOf(errorEmail));

    }

    public boolean elementIsExist(By by, int time) {
        try {
            driver.manage().timeouts().implicitlyWait(time, TimeUnit.SECONDS);
            driver.findElement(by);
            return true;
            // Перехватываю ошибку от Selenium, не от java.util
        } catch (NoSuchElementException ignore) {
        } finally {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
        return false;   // запись вне кетча дает вернуть фолс в любом случа, а ошибку игнорим
    }

    private void fillInputField(WebElement element, String value) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
//        element.click();
        actions.moveToElement(element).pause(200).click(element).build().perform(); // наводим мышку на элемент, кликаем, билдим и перформим
        // так работает экшон, собирает события и потом надо вызвать build и perform
        // это один из способов клика по веб элементу
        element.clear();
        element.sendKeys(value);

        boolean telNumber = element.getAttribute("name").equals("userTel");

        // проверка на телефон
        value = telNumber ? getForMaskValue(value) : value;

        boolean elemFilled = wait.until(ExpectedConditions.attributeContains(element, "value", value));

        Assertions.assertTrue(elemFilled, "Поле не заполнено");
    }

    private String getForMaskValue(String value) {
        return "+7 (" + value.substring(0, 3) + ") " + value.substring(3, 6) + "-" + value.substring(6, value.length() - 1);
    }

    private ExpectedCondition<Boolean> completedJSPage() {
        return new ExpectedCondition<Boolean>() {
            @NullableDecl
            @Override
            public Boolean apply(@NullableDecl WebDriver webDriver) {
                return jsExecutor.executeScript("return document.readyState").equals("complete");
                // документ это html станица, readystate свойство этой странички, которая говорит о том что данная страничка в статусе каком то
            }
        };
    }

    public void waitForPageStability(int maxWaitMillis, int pollDelimiter) {        // максимальное время ожидание и интервал
        double startTime = System.currentTimeMillis();     // Получаем наше время
        while (System.currentTimeMillis() < startTime + maxWaitMillis) {    // цикл ограничения по времени
            String prevState = driver.getPageSource();      // getPageSource вернуть все что видно на html страничке, весь html код
            try {
                Thread.sleep(pollDelimiter); // <-- would need to wrap in a try catch
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (prevState.equals(driver.getPageSource())) {     // если предыдущее состояние не изменилось спустя какой то
                // промежуток времени, то значит страничка статичная и проверка останавливается
                return;
            }
        }
        throw new RuntimeException("Станица не стабилизировалась за указанное время " + maxWaitMillis + "ms");
    }

    // работа с фреймом рекламы
    private void waitAndCloseWidget() {
        try {
            String xpathWidget = "//div[@data-showed-up='true' and @class='flocktory-widget-overlay']/iframe[@class='flocktory-widget']";
            String xpathClose = "//div[contains(@class, 'close js')]";
            if (elementIsExist(By.xpath(xpathWidget), 1)) {
                driver.switchTo().frame(driver.findElement(By.xpath(xpathWidget)));
                if (elementIsExist(By.xpath(xpathClose), 1)) {
                    waitForPageStability(5000, 500);
                    WebElement closeButton = driver.findElement(By.xpath(xpathClose));
                    closeButton.click();
                }
            }
        } finally {
            driver.switchTo().defaultContent();     // переключаемся на начальный драйвер, то есть к базовой странице
        }
    }
}
