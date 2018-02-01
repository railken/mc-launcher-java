import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.json.JSONObject;

import static java.lang.System.out;

import java.io.File;
import java.util.Scanner;

import java.io.*;
import java.net.URL;
import org.json.JSONArray;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.FileUtils;

public class Launcher {

    String baseDir;
    ArrayList<String> libraries;
    Logger logger;

    static String FILE_USER = "user.json";

    public Launcher (String baseDir)
    {

        this.logger = LoggerFactory.getLogger(Launcher.class);
        this.baseDir = baseDir;
    }

    public void ini() throws Exception
    {

        Storage configFile = new Storage(baseDir+"config.json");
        JSONObject config = new JSONObject();

        if (configFile.exists()) {
            try {
                config = new JSONObject(configFile.get());
            } catch (Exception e) {
                this.logger.error("Syntax Error in config.json");
            }
        }

        String url;

        if (!config.has("modpack")) {

            this.logger.info("No config found");
            Scanner scan = new Scanner(System.in);
            System.out.print("Modpack URL: ");
            url = scan.next();

            config.put("modpack", url);
            config.put("installed", false);

        }

        // Changing path to minecraft
        this.baseDir = this.baseDir+"minecraft/";

        if (!config.getBoolean("installed")) {

            this.logger.info("No installation found");
            FileUtils.deleteDirectory(new File(this.baseDir));

            this.logger.info("Cloning repository... This might take a while");
            Git git = Git.cloneRepository()
                .setURI(config.getString("modpack"))
                .setDirectory(new File(this.baseDir))
                .call();

            config.put("installed", true);
        } else {

            Repository existingRepo = new FileRepositoryBuilder()
                    .setGitDir(new File(this.baseDir+"/.git"))
                    .build();

            Git git = new Git(existingRepo);
            git.pull();
            git
                .reset()
                .setMode(ResetCommand.ResetType.HARD)
                .setRef("origin/master")
                .call();
        }

        configFile.set(config.toString());
    }

    public User auth()
    {

        AuthService service = new AuthService();

        Storage storage = new Storage(baseDir + Launcher.FILE_USER);

        try {

            JSONObject response = null;


            if (storage.exists()) {
                response = new JSONObject(storage.get());
            }

            if (response != null) {

                try {
                    service.validate(response.getString("accessToken"));

                } catch (Exception e) {

                    try {
                        response = service.refresh(
                            response.getString("accessToken"),
                            response.getJSONObject("selectedProfile").getString("id"),
                            response.getJSONObject("selectedProfile").getString("name")
                        );
                        storage.set(response.toString());
                    } catch (Exception e1) {
                        response = null;
                    }

                }
            }


            if (response == null) {

                Scanner scan = new Scanner(System.in);

                System.out.print("Username: ");
                String username = scan.next();

                System.out.print("Password: ");
                String password = scan.next();

                response = service.authenticate(username, password);

                storage.set(response.toString());
            }

            User user = new User();
            user.setAccessToken(response.getString("accessToken"));
            user.setUsername(response.getJSONObject("selectedProfile").getString("name"));
            user.setID(response.getJSONObject("selectedProfile").getString("id"));

            return user;

        } catch (Exception e) {
            out.println("Authentication failed");
        }

        return null;
    }

    public ArrayList<String> update() throws Exception
    {


        out.println("Checking libraries");
        ArrayList<String> libraries = new ArrayList<String>();


        Storage librariesFile = new Storage(this.baseDir+"modpack/bin/1.12.2.json");

        if (!librariesFile.exists()) {
            throw new Exception("Missing file: "+ librariesFile.getFilename());
        }

        JSONObject info = new JSONObject(librariesFile.get());

        JSONArray i_libraries = info.getJSONArray("libraries");
        String path;
        String url;
        String key;

        for(int i = 0; i < i_libraries.length(); i++) {
            JSONObject downloads = i_libraries.getJSONObject(i).getJSONObject("downloads");
            for (int y = 0; y < downloads.names().length(); y++) {
                key = downloads.names().getString(y);

                if (downloads.getJSONObject(key).has("path")) {
                    path = downloads.getJSONObject(key).getString("path");
                    url = downloads.getJSONObject(key).getString("url");


                    Storage assetFile = new Storage(baseDir+"cache/"+path);
                    JSONObject o = downloads.getJSONObject(key);


                    try {

                        if(!assetFile.exists())
                            throw new Exception("file not found");

                        if (assetFile.getSize() != o.getLong("size"))
                            throw new Exception("mismatch size");

                        if (!assetFile.getChecksum("sha1").equals(o.getString("sha1")))
                            throw new Exception("mismatch sha1");

                    } catch (Exception e) {
                        this.logger.info(e.getMessage());
                        assetFile.download(new URL(url));
                        out.println("Downloaded: "+url);
                    }

                    libraries.add(assetFile.getFilename());

                }

            }

        }

        this.libraries = libraries;


        libraries.add(baseDir+"modpack/bin/modpack.jar");
        libraries.add(baseDir+"modpack/bin/minecraft.jar");


        out.println("Checking assets");
        Storage assetCache = new Storage(baseDir+"/assets/indexes/"+info.getJSONObject("assetIndex").getString("id")+".json");
        assetCache.download(new URL(info.getJSONObject("assetIndex").getString("url")));

        JSONObject infoAssets = new JSONObject(assetCache.get()).getJSONObject("objects");

        for (int y = 0; y < infoAssets.names().length(); y++) {

            key = infoAssets.names().getString(y);
            String filename = infoAssets.getJSONObject(key).getString("hash");

            filename.substring(0, 2);

            Storage assetFile = new Storage(baseDir+"/assets/objects/"+filename.substring(0, 2)+"/"+filename);

            JSONObject o = infoAssets.getJSONObject(key);


            try {

                if(!assetFile.exists())
                    throw new Exception();

                if (assetFile.getSize() != o.getLong("size"))
                    throw new Exception();

                if (!assetFile.getChecksum("sha1").equals(o.getString("hash")))
                    throw new Exception();

            } catch (Exception e) {
                url = "http://resources.download.minecraft.net/" + filename.substring(0, 2) + "/" + filename;
                assetFile.download(new URL(url));

                out.println("Downloaded: " + url);
            }


        }

        return libraries;
    }

    public void launch(User user, ArrayList<String> libraries)
    {
        String command = "java " +
                "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump " +
                "-Xms4096m -Xmx4096m " +
                "-XX:+UseG1GC -XX:MaxGCPauseMillis=4 " +
                "-Djava.library.path="+baseDir+"modpack/bin/natives " +
                "-Dfml.core.libraries.mirror=http://mirror.technicpack.net/Technic/lib/fml/%s " +
                "-Dminecraft.applet.TargetDirectory="+baseDir+"modpack " +
                "-Djava.net.preferIPv4Stack=true " +
                "-cp "+String.join(";",libraries)+" " +
                "net.minecraft.launchwrapper.Launch " +
                "--username "+user.getUsername()+" " +
                "--version 1.12.2-forge1.12.2-14.23.0.2491 " +
                "--gameDir "+baseDir+"modpack " +
                "--assetsDir "+baseDir+"assets " +
                "--assetIndex 1.12 " +
                "--uuid "+user.getID()+" " +
                "--accessToken "+user.getAccessToken()+" " +
                "--userType mojang " +
                "--tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker " +
                "--versionType Forge " +
                "--title OptiFine 1.12.2 - Forge " +
                "--icon "+baseDir+"assets/packs/optifine-1122-forge/icon.png\n";

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
