package utils;

import org.testng.annotations.DataProvider;

import tests.ExcelDataTest;

public class TestDataProvider {

    @DataProvider(name = "loginData")
    public static Object[][] loginDataProvider() throws Exception {
        String excelPath = "src/test/resources/testdata.xlsx";
        return ExcelDataTest.getTestData(excelPath, "Sheet1");
    }
}
