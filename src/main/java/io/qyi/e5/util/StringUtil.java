package io.qyi.e5.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: msgpush
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-20 23:59
 **/
public class StringUtil {
    public static Map<String, String> ParsingUrl(String url){
        String[] split = url.split("&");
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < split.length; i++) {
            String[] split1 = split[i].split("=");
            if (split1.length > 1) {
                System.out.println(split1[0] + " --- " + split1[1]);
                map.put(split1[0], split1[1]);
            } else {
                map.put(split1[0], "");
            }
        }
        return map;
    }

    public static String readTxt(File file) throws IOException {
        String s = "";
        InputStreamReader in = new InputStreamReader(new FileInputStream(file),"UTF-8");
        BufferedReader br = new BufferedReader(in);
        StringBuffer content = new StringBuffer();
        while ((s=br.readLine())!=null){
            content = content.append(s);
        }
        return content.toString();
    }
}
