package com.serverless.infra;

import com.serverless.infra.FileHelper;
import org.junit.Test;

import static org.junit.Assert.*;

public class FileHelperTest {

    @Test
    public void getProjectName() {
        FileHelper fileHelper = new FileHelper();
        String result = fileHelper.getProjectName("https://github.com/abigail830/order-service.git");
        assertEquals("order-service", result);
    }
}