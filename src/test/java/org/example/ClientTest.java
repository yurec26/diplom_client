package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;


class ClientTest {

    @Test
    void choosePort_file_file_NOT_found() throws IOException {
        File file = new File("test.test.test");
        //
        boolean expected = false;
        boolean actual = Client.choosePort(file);
        //
        Assertions.assertEquals(actual, expected);
    }
}