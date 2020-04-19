package io.qyi.e5.string;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    @Test
    public void r(){
        for (int i = 0; i < 30; i++) {
            System.out.println(getRandom(3600,7200));
        }
    }

    @Test
    public void ScheduledExecutor(){

    }



    public String getRandom(int start, int end){
        Random r = new Random();
        String Expiration = String.valueOf((r.nextInt(end-start +1) + start) );
        return Expiration;
    }
}
