package components;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.json.JSONObject;

import java.io.Console;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.System.out;
import services.Storage;
import launcher.Builder;

public class ConfigComponent extends BaseComponent {

    public ConfigComponent (Builder builder)
    {
        super(builder);
    }

    public void execute() throws Exception
    {

        Storage configFile = new Storage(this.builder.baseDir+"config.json");
        JSONObject config = new JSONObject();

        if (configFile.exists()) {
            try {
                config = new JSONObject(configFile.get());
            } catch (Exception e) {
                this.builder.logger.error("Syntax Error in config.json");
            }
        }

        String url;

        if (!config.has("modpack")) {

            Scanner scan = new Scanner(System.in);
            System.out.print("Modpack URL: ");
            url = scan.next();
            config.put("modpack", url);

        }

        if (!config.has("installed")) {
            config.put("installed", false);
        }

        if (!config.has("command")) {
            config.put("command", "-Xms4096m -Xmx4096m -XX:+UseG1GC -XX:MaxGCPauseMillis=4");
        }


        // Changing path to minecraft
        this.builder.baseDir = this.builder.baseDir+"minecraft/";

        if (!config.getBoolean("installed")) {

            this.builder.logger.info("No installation found");
            FileUtils.deleteDirectory(new File(this.builder.baseDir));

            this.builder.logger.info("Cloning repository... This might take a while");
            Git git = Git.cloneRepository()
                    .setURI(config.getString("modpack"))
                    .setDirectory(new File(this.builder.baseDir))
                    .call();

            config.put("installed", true);
        } else {

            Repository existingRepo = new FileRepositoryBuilder()
                    .setGitDir(new File(this.builder.baseDir+"/.git"))
                    .build();

            Git git = new Git(existingRepo);
            git.pull();
            git
                    .reset()
                    .setMode(ResetCommand.ResetType.HARD)
                    .setRef("origin/master")
                    .call();
        }

        this.builder.config = config;

        configFile.set(config.toString());
    }
}
