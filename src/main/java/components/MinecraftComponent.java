package components;

import launcher.Builder;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.System.out;
import services.AuthService;
import model.User;
import services.Storage;

public class MinecraftComponent extends BaseComponent {

    public MinecraftComponent (Builder builder)
    {
        super(builder);
    }

    public void launch(User user, ArrayList<String> libraries)
    {
        String command = "java " +
                "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump " +
                "-Xms4096m -Xmx4096m " +
                "-XX:+UseG1GC -XX:MaxGCPauseMillis=4 " +
                "-Djava.library.path="+builder.baseDir+"modpack/bin/natives " +
                "-Dfml.core.libraries.mirror=http://mirror.technicpack.net/Technic/lib/fml/%s " +
                "-Dminecraft.applet.TargetDirectory="+builder.baseDir+"modpack " +
                "-Djava.net.preferIPv4Stack=true " +
                "-cp "+String.join(";",libraries)+" " +
                "net.minecraft.launchwrapper.Launch " +
                "--username "+user.getUsername()+" " +
                "--version 1.12.2-forge1.12.2-14.23.0.2491 " +
                "--gameDir "+builder.baseDir+"modpack " +
                "--assetsDir "+builder.baseDir+"assets " +
                "--assetIndex 1.12 " +
                "--uuid "+user.getID()+" " +
                "--accessToken "+user.getAccessToken()+" " +
                "--userType mojang " +
                "--tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker " +
                "--versionType Forge " +
                "--title OptiFine 1.12.2 - Forge " +
                "--icon "+builder.baseDir+"assets/packs/optifine-1122-forge/icon.png\n";

        System.out.println(command);
        this.executeCommand(command);
    }

    private void executeCommand(String command) {

        try {
            Runtime runTime = Runtime.getRuntime();
            Process process = runTime.exec(command);
        } catch (IOException ex) {

        }
    }
}
