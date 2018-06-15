
import static java.lang.System.out;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import model.User;
import launcher.Builder;
import components.ConfigComponent;
import components.UserComponent;
import components.UpdaterComponent;
import components.MinecraftComponent;

public class Main{

    public static void main(final String[] arguments) throws Exception {

        String baseDir = null;

        if (arguments.length == 1) {
            baseDir = arguments[0];
        }

        if (arguments.length == 0) {
            baseDir = Paths.get(".").toAbsolutePath().normalize().toString()+"/";
        }

        Builder launcherBuilder = new Builder(baseDir);

        // Component:Configuration
        ConfigComponent configComponent = new ConfigComponent(launcherBuilder);
        configComponent.execute();

        // Component:Authentication
        UserComponent userComponent = new UserComponent(launcherBuilder);
        userComponent.execute();

        if (launcherBuilder.user == null) {
            return;
        }

        out.println("Welcome back " + launcherBuilder.user.toString());

        // Component:Updating
        UpdaterComponent updaterComponent = new UpdaterComponent(launcherBuilder);
        updaterComponent.execute();

        // Component:Launching
        MinecraftComponent minecraftComponent = new MinecraftComponent(launcherBuilder);
        minecraftComponent.execute();

        out.println("Launching minecraft ...");


    }

}
