package components;

import org.json.JSONObject;

import java.io.Console;
import java.util.Arrays;
import static java.lang.System.out;
import launcher.Builder;


public class BaseComponent {

    Builder builder;

    public BaseComponent (Builder builder)
    {
        this.builder = builder;
    }

    public Builder getBuilder()
    {
        return this.builder;
    }
}
