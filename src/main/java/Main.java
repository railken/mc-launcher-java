
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

        // Configuration
        ConfigComponent configComponent = new ConfigComponent(launcherBuilder);
        configComponent.ini();

        // Authentication
        UserComponent userComponent = new UserComponent(launcherBuilder);
        User user = userComponent.auth();

        if (user == null) {
            return;
        }

        out.println("Welcome back " + user.toString());

        // Updating
        UpdaterComponent updaterComponent = new UpdaterComponent(launcherBuilder);

        ArrayList<String> libraries = updaterComponent.update();

        // Launching
        MinecraftComponent minecraftComponent = new MinecraftComponent(launcherBuilder);
        minecraftComponent.launch(user, libraries);

        out.println("Launching minecraft ...");


    }

}
