package io.qyi.e5.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

/**
 * @author yellowcong
 * 创建日期:2018/02/06
 * 验证码信息
 */
public class VerifyCode {

    //宽度
    private static final int CAPTCHA_WIDTH = 100;
    //高度
    private static final int CAPTCHA_HEIGHT = 35;
    //数字的长度
    private static final int NUMBER_CNT = 6;
    //图片类型
    private static final String IMAGE_TYPE = "JPEG";

    private Random r = new Random();
    //  字体
//  private String[] fontNames = { "宋体", "华文楷体", "黑体", "华文新魏", "华文隶书", "微软雅黑", "楷体_GB2312" };
    private String[] fontNames = {"宋体", "黑体", "微软雅黑"};

    // 可选字符
    private String codes = "23456789abcdefghjkmnopqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";

    // 背景色,白色
    private Color bgColor = new Color(255, 255, 255);

    // 验证码上的文本
    private String text;

    private static VerifyCode utils = null;

    /**
     * 实例化对象
     *
     * @return
     */
    public static VerifyCode getInstance() {
        if (utils == null) {
            synchronized (VerifyCode.class) {
                if (utils == null) {
                    utils = new VerifyCode();
                }
            }
        }
        return utils;
    }

    /**
     * 创建验证码
     *
     * @param path 路径地址
     * @return
     * @throws Exception
     */
    public String getCode(String path) throws Exception {
        BufferedImage bi = utils.getImage();
        output(bi, new FileOutputStream(path));
        return this.text;
    }

    /**
     * 创建日期:2018年2月6日<br/>
     * 创建时间:下午7:22:36<br/>
     * 创建用户:yellowcong<br/>
     * 机能概要:生成图片对象，并返回
     *
     * @return
     * @throws Exception
     */
    public CaptchaCode getCode() throws Exception {
        BufferedImage img = utils.getImage();

        //返回验证码对象
        CaptchaCode code = new CaptchaCode();
        code.setText(this.text);
        code.setData(this.copyImage2Byte(img));
        return code;
    }

    /**
     * 创建日期:2018年2月6日<br/>
     * 创建时间:下午7:17:28<br/>
     * 创建用户:yellowcong<br/>
     * 机能概要:将图片转化为 二进制数据
     *
     * @param img
     * @return
     * @throws Exception
     */
    public byte[] copyImage2Byte(BufferedImage img) throws Exception {
        //字节码输出流
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        //写数据到输出流中
        ImageIO.write(img, IMAGE_TYPE, bout);

        //返回数据
        return bout.toByteArray();
    }

    /**
     * 创建日期:2018年2月6日<br/>
     * 创建时间:下午7:20:50<br/>
     * 创建用户:yellowcong<br/>
     * 机能概要:将二进制数据转化为文件
     *
     * @param data
     * @param file
     * @throws Exception
     */
    public boolean copyByte2File(byte[] data, String file) throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        FileOutputStream out = new FileOutputStream(file);
        try {
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = in.read(buff)) > -1) {
                out.write(buff, 0, len);
            }
            out.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            out.close();
            in.close();
        }
    }

    /**
     * 创建日期:2018年2月6日<br/>
     * 创建时间:下午7:03:57<br/>
     * 创建用户:yellowcong<br/>
     * 机能概要:生成随机的颜色
     *
     * @return
     */
    private Color randomColor() {
        int red = r.nextInt(150);
        int green = r.nextInt(150);
        int blue = r.nextInt(150);
        return new Color(red, green, blue);
    }

    /**
     * 创建日期:2018年2月6日<br/>
     * 创建时间:下午7:03:20<br/>
     * 创建用户:yellowcong<br/>
     * 机能概要:生成随机的字体
     *
     * @return
     */
    private Font randomFont() {
        int index = r.nextInt(fontNames.length);
        String fontName = fontNames[index];// 生成随机的字体名称
        int style = r.nextInt(4);// 生成随机的样式, 0(无样式), 1(粗体), 2(斜体), 3(粗体+斜体)
        int size = r.nextInt(5) + 24; // 生成随机字号, 24 ~ 28
        return new Font(fontName, style, size);
    }

    // 画干扰线
    private void drawLine(BufferedImage image) {
        int num = 9;// 一共画9条
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        for (int i = 0; i < num; i++) {// 生成两个点的坐标，即4个值
            int x1 = r.nextInt(CAPTCHA_WIDTH);
            int y1 = r.nextInt(CAPTCHA_HEIGHT);
            int x2 = r.nextInt(CAPTCHA_WIDTH);
            int y2 = r.nextInt(CAPTCHA_HEIGHT);
            g2.setStroke(new BasicStroke(1.5F));
            g2.setColor(randomColor()); // 随机生成干扰线颜色
            g2.drawLine(x1, y1, x2, y2);// 画线
        }
    }

    // 随机生成一个字符
    private char randomChar() {
        int index = r.nextInt(codes.length());
        return codes.charAt(index);
    }

    // 创建BufferedImage
    private BufferedImage createImage() {
        BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.setColor(this.bgColor);
        g2.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
        return image;
    }

    // 调用这个方法得到验证码
    public BufferedImage getImage() {
        BufferedImage image = createImage();// 创建图片缓冲区
        Graphics2D g2 = (Graphics2D) image.getGraphics();// 得到绘制环境
        StringBuilder sb = new StringBuilder();// 用来装载生成的验证码文本
        // 向图片中画4个字符
        for (int i = 0; i < NUMBER_CNT; i++) {// 循环四次，每次生成一个字符
            String s = randomChar() + "";// 随机生成一个字母
            sb.append(s); // 把字母添加到sb中
            float x = i * 1.0F * CAPTCHA_WIDTH / NUMBER_CNT; // 设置当前字符的x轴坐标
            g2.setFont(randomFont()); // 设置随机字体
            g2.setColor(randomColor()); // 设置随机颜色
            g2.drawString(s, x, CAPTCHA_HEIGHT - 5); // 画图
        }
        this.text = sb.toString(); // 把生成的字符串赋给了this.text
        drawLine(image); // 添加干扰线
        return image;
    }

    /**
     * 创建日期:2018年2月6日<br/>
     * 创建时间:下午7:09:49<br/>
     * 创建用户:yellowcong<br/>
     * 机能概要:
     *
     * @return 返回验证码图片上的文本
     */
    public String getText() {
        return text;
    }

    // 保存图片到指定的输出流
    public static void output(BufferedImage image, OutputStream out) throws IOException {
        ImageIO.write(image, IMAGE_TYPE, out);
    }

    //图片验证码对象
    static class CaptchaCode {
        //验证码文字信息
        private String text;
        //验证码二进制数据
        private byte[] data;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }
}