package launcher;

import org.json.JSONObject;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import model.User;


public class Builder {

    public String baseDir;
    public ArrayList<String> libraries;
    public Logger logger;
    public User user;
    public JSONObject config;


    public Builder (String baseDir)
    {
        this.logger = LoggerFactory.getLogger(Builder.class);
        this.baseDir = baseDir;
    }

}
