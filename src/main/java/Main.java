
import static java.lang.System.out;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
public class Main{

    public static void main(final String[] arguments) throws Exception {

        String baseDir = null;

        if (arguments.length == 1) {
            baseDir = arguments[0];
        }

        if (arguments.length == 0) {
            baseDir = Paths.get(".").toAbsolutePath().normalize().toString()+"/";
        }

        Launcher launcher = new Launcher(baseDir);

        launcher.ini();

        User user = launcher.auth();

        out.println("Welcome back " + user.toString());

        ArrayList<String> libraries = launcher.update();
        launcher.launch(user, libraries);
        out.println("The end");
        
    }

}
