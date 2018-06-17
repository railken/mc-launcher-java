package components;

import launcher.Builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.System.out;

public class MinecraftComponent extends BaseComponent {

    public MinecraftComponent (Builder builder)
    {
        super(builder);
    }

    public void execute()
    {
        String command = "java " +
                "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump " +
                " "+builder.config.getString("command")+" "+
                "-Djava.library.path="+builder.baseDir+"modpack/bin/natives " +
                "-Djava.net.preferIPv4Stack=true " +
                "-cp "+String.join(";",builder.libraries)+" " +
                "net.minecraft.launchwrapper.Launch " +
                "--username "+builder.user.getUsername()+" " +
                "--gameDir "+builder.baseDir+"modpack " +
                "--assetsDir "+builder.baseDir+"assets " +
                "--assetIndex 1.12 " +
                "--uuid "+builder.user.getID()+" " +
                "--accessToken "+builder.user.getAccessToken()+" " +
                "--userType mojang " +
                "--tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker " +
                "--versionType Forge " +
                "--title Custom Launcher ";
                //"--icon "+builder.baseDir+"assets/packs/optifine-1122-forge/icon.png\n";

        System.out.println(command);
        this.executeCommand(command);
    }

    private void executeCommand(String command) {

        try {
            Runtime runTime = Runtime.getRuntime();
            Process proc = runTime.exec(command);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            out.println(ex.toString());
        }
    }
}
