package io.qyi.e5.string;

import org.junit.jupiter.api.Test;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-03-05 17:09
 **/
public class dome01 {
    @Test
    public void test01(){
        String[] s = new String[]{"CompactToken validation", "Access token has expired.", "Access token validation failure"};
        String msg = "Access token has expired.";

        System.out.println(s[1]);
        System.out.println(msg.indexOf(s[1]));
    }
}
