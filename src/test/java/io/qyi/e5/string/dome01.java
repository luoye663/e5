package io.qyi.e5.string;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-03-05 17:09
 **/
public class dome01 {

    @Test
    public void test01() {
        String[] s = new String[]{"CompactToken validation", "Access token has expired.", "Access token validation failure"};
        String msg = "Access token has expired.";

        System.out.println(s[1]);
        System.out.println(msg.indexOf(s[1]));
    }

    @Test
    public void test() {
        JsonObject jsonObject = new Gson().fromJson("", JsonObject.class);
        JsonArray data = jsonObject.getAsJsonArray("data");
        for (JsonElement j : data) {
            int pid = j.getAsJsonObject().get("pid").getAsInt();
        }
        for (int i = 0; i < data.size(); i++) {
            JsonObject JsonObject = data.get(i).getAsJsonObject();
            JsonObject.get("pid").getAsInt();
        }
    }
}
