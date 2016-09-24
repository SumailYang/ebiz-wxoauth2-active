package jar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Random;



import com.saic.framework.web.wechat.util.AESUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
//import weixin.popular.util.JsUtil;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }

    public void testvoid() {
        Method[] type = AppTest.class.getMethods();
        for (Method method : type) {
            Type[] types = method.getGenericParameterTypes();
            if (types.length > 0) {
                for (Type type2 : types) {
                    Class<?> clazz = (Class<?>) type2;
                    System.out.print(type2 + " " + (clazz == String.class) + "  | ");
                }
                System.out.println("");
            }

        }
    }

    public void testAes() {

        // 11000146

        String data = AESUtils.decryptData("e7tZwuMmJu1nWXwC+16DCg==");
        System.out.println(data);

        // String params =
        // "source=2&uid=cW6TsQt3mYoheAUwjtz1PQ==&userId=840890&cityName=%E4%B8%8A%E6%B5%B7&longitude=121.428947&latitude=31.206791";
        // Map<String, String> paramsMap = FrameworkUtil.splitURLParams(params);
        // System.out.println(paramsMap.toString());

    }

    public void gestAes1() throws IOException {
    }

    public void testa() {
        for (int i = 0; i < 1; i++) {
            String data = AESUtils.encryptData(String.valueOf(createbaweishu()));
            if (data.indexOf("%") > -1) {
                System.out.println(data);
                return;
            }
        }
    }

    public Long createbaweishu() {
        Random random1 = new Random();
        Long result = 0L;
        for (int i = 1; i < 8; i++) {
            int num = 1;
            Long ten = Math.round(Math.pow(10, i));
            if (i != 1) {
                num = random1.nextInt(9);
            }
            result += num * ten;
        }
        return result;

    }

    public void testb() {
        // String param = "12+34";
        // byte[] s1 = org.castor.core.util.Base64Decoder.decode(param);
        // byte[] s2 = org.castor.core.util.Base64Decoder.decode(new String(s1));
        // System.out.println(new String(s1));
        // System.out.println(new String(s2));

        String p = "msKcvSo7zdeI4DAQpyM0DA==";

        String encode = URLEncoder.encode(p);
        System.out.println(encode);
    }
}
