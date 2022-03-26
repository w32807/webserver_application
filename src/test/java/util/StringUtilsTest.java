package util;


import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void splitString() throws Exception{
        // given
        String line = "GET /index.html HTTP/1.1";
        // when
        String[] strings = line.split(" ");
        // then
        Assert.assertEquals(strings[1], "/index.html");
    }
}
