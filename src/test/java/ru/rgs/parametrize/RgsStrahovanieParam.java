package ru.rgs.parametrize;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.rgs.base.baseTests;
import org.junit.jupiter.params.ParameterizedTest;

public class RgsStrahovanieParam extends baseTests {


    // Надо проставить дравер на нужную страницу
    @ParameterizedTest(name = "Проверка имени = {0}")
    @Tag("allTests")
    @ValueSource(strings = {"Борисенков Дмитрий Владимирович", "0EXYwPCa", "dmq"})
    void testUserName(String value) {
        WebElement field = driver.findElement(By.xpath("//input[@name='userName']"));
        fillInputField(field, value);
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(field, "value", value));
        Assertions.assertTrue(checkFlag, "Поле было заполнено некорректно");
    }

    private void fillInputField(WebElement element, String value) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        element.click();
        element.sendKeys(value);
    }
}
