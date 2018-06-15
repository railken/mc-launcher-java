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

    public void ini() throws Exception
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

            this.builder.logger.info("No config found");
            Scanner scan = new Scanner(System.in);
            System.out.print("Modpack URL: ");
            url = scan.next();

            config.put("modpack", url);
            config.put("installed", false);

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

        configFile.set(config.toString());
    }
}
