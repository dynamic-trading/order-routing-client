package dynamic.trading.order.routing.client.config;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Configuration {
    private static final String CONFIG = "order-routing-client.config";
    private Config config;

    private static class Holder{
        private static final Configuration INSTANCE = new Configuration();
    }

    public static Configuration getInstance(){
        return Holder.INSTANCE;
    }

    private Configuration() { }

    public void load(final String path, final String configName){
        try{
            String pathname = path+configName;
            open(pathname);
        }catch (Exception ex){
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public void load(){
        try{
            String rootPath = new File(".").getCanonicalPath();

            String fileSuffix = rootPath.contains("/") ? "/" : "\\src\\main\\resources\\";
            String path = rootPath + fileSuffix;

            String pathname = path+CONFIG;
            open(pathname);
        }catch (Exception ex){
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private void open(final String pathname) throws Exception{
        File ff = new File(pathname);
        BufferedReader reader = new BufferedReader(new FileReader(ff));

        Gson gson = new Gson();
        this.config = gson.fromJson(reader, Config.class);
    }

    public Config getConfig() {
        return config;
    }
}
