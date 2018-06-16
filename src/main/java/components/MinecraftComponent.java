package components;

import launcher.Builder;
import java.io.IOException;

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
                "-Dfml.core.libraries.mirror=http://mirror.technicpack.net/Technic/lib/fml/%s " +
                "-Dminecraft.applet.TargetDirectory="+builder.baseDir+"modpack " +
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
            Process process = runTime.exec(command);
        } catch (IOException ex) {

        }
    }
}
