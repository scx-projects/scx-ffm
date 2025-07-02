package cool.scx.ffm.test;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CTest {

    public static void main(String[] args) {
        test1();
    }

    @Test
    public static void test1() {
        long a = C.C.strlen("abc123456");
        int a1 = "abc123456".length();
        Assert.assertEquals(a, a1);

        long b = C.C.abs(-123);
        long b1 = Math.abs(-123);
        Assert.assertEquals(b, b1);

        var c = C.C.sin(12);
        var c1 = Math.sin(12);
        Assert.assertEquals(c, c1);

        var d = C.C.sqrt(88);
        var d1 = Math.sqrt(88);
        Assert.assertEquals(d, d1);

    }

}
