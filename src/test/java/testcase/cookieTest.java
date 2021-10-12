package testcase;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class cookieTest {
    public static WebDriver driver;
    @BeforeAll
    public static void initData() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
//        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        driver = new ChromeDriver(options);
    }

    @Test
    public void loginTest(){
        try {
            driver.get("https://work.weixin.qq.com/wework_admin/frame");
//            Thread.sleep(3000);
//            System.out.println(driver.manage().getCookies());
            //[wwrtx.tmp_sid=wuXRGxHwx2ZZG59QpYNxaYFs3UfS2cEqtiwHTvUAUf6A; path=/; domain=.work.weixin.qq.com, wwrtx.refid=42100337831534138; path=/; domain=.work.weixin.qq.com, wwrtx.c_gdpr=0; expires=星期三, 12 十月 2022 12:25:34 CST; path=/; domain=.work.weixin.qq.com, wwrtx.gst=fLkIsbrd_mRSGmnQ6kAfJtyPkUl8VA4Ptbaz2Ppb_Sv-kw9m7Z4zXGd76cc0leWrOzQAbn-sNHHvYyhyDbzkkZ8kAff6n919uX7JcGIzOmSqJsstltmm6IwfH3QzJG5LiiuYIcsapD2M4yDeXKwkGYxkvd5KNE6SCJZdmM1yq_BkKJq-E45-HoQYwmUHv4V3sbEA3A2rv48kpTb0dyZxdTUg6lNxoDqX6z9tPFXQcleRiQC7Bu6SER8WyYyv81_-; path=/; domain=.work.weixin.qq.com, _gat=1; expires=星期二, 12 十月 2021 12:26:35 CST; path=/; domain=.qq.com, _ga=GA1.2.1338377693.1634012735; expires=星期四, 12 十月 2023 12:25:48 CST; path=/; domain=.qq.com, wwrtx.i18n_lan=zh; expires=星期四, 11 十一月 2021 12:25:49 CST; path=/; domain=.work.weixin.qq.com, wwrtx.ref=direct; path=/; domain=.work.weixin.qq.com, _gid=GA1.2.625104229.1634012735; expires=星期三, 13 十月 2021 12:25:48 CST; path=/; domain=.qq.com]
            //保存cookie
//            saveCookie();
            //加载添加cookie
            loadCookie();
            // 添加cookie后再登录
             driver.get("https://work.weixin.qq.com/wework_admin/frame");
             Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDown(){
        driver.quit();
    }


    public void saveCookie(){
       //声明文件读写器和缓存器来对数据io操作
        try {
            //声明文件读写器
            FileWriter fileWriter=new FileWriter("./cookies.txt");
            //声明缓冲器来将读取到的数据先全部缓冲到bufferd中,缓冲完毕在一次性写入到文件file中,减少文件操作,优化IO效率,提升性能
            BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
            //driver.manage().getCookies()得到的是一个数组,for循环该数组中的每个元素,将每个元素中我们需要的内容提取出来,写入缓冲器
            for (Cookie cookie: driver.manage().getCookies()){
                bufferedWriter.write(
                        cookie.getName()+';'+
                                cookie.getValue()+';'+
                                cookie.getDomain()+';'+
                                cookie.getPath()+';'+
                                cookie.getExpiry()+';'+
                                cookie.isSecure()
                );
                //每循环写入一次数据,就换行写入下一次循环数据
                bufferedWriter.newLine();
            }
            //循环写入结束后将缓冲器中的数据将一次性写入文件中,减少文件操作,优化io
            //写入完毕后关闭缓冲器的写入
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCookie(){
        //加载已存在的cookie的方法
        try {
            FileReader fileReader=new FileReader("./cookies.txt");
            BufferedReader bufferedReader=new BufferedReader(fileReader);
            //定义line为读取到的数据
            String line;
            //当从bufferedReader中按行读取的内容不为空时
            while ((line=bufferedReader.readLine())!=null){
                //StringTokenizer类提供了对字符串进行分解的方法,默认按空格分解,类似python中字符串的的splite分割方法
                StringTokenizer tokenizer=new StringTokenizer(line,";");//将cookie中读取到的line按照";"分割.
                //tokenizer.nextToken()方法,调用一次后下一次调用会接位偏移,此处表示分割后的字符串的各个部分
                //将分割后的第一部分赋值给String类型的name,一次类推,顺序按照saveCookie方法中保存的各部分的排列顺序读取即可
                String name=tokenizer.nextToken();
                String value=tokenizer.nextToken();
                String domain=tokenizer.nextToken();
                String path=tokenizer.nextToken();
                //先定义一个date 类型的expiry
                Date expiry=null;
                String dt=tokenizer.nextToken();
                if (!dt.equals("null")){
                    SimpleDateFormat sdf=new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyy", Locale.ENGLISH);
                    //把string转换成date类型
                    expiry=sdf.parse(dt);
                }
                //把string转换成boolean类型
                boolean isSecure=Boolean.parseBoolean(tokenizer.nextToken());
                Cookie cookie=new Cookie(name,value,domain,path,expiry,isSecure);
                //循环地将cookie添加到浏览器,循环结束之后,所有的cookie就都添加到了浏览器
                driver.manage().addCookie(cookie);
            }
            //捕获所有异常,不然上面的bufferedReader.readLine()方法会报红
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
