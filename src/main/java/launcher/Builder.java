package launcher;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.json.JSONObject;

import java.io.File;
import java.util.Scanner;

import java.io.*;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.FileUtils;

import components.UserComponent;
import components.UpdaterComponent;
import components.ConfigComponent;
import services.Storage;
import model.User;


public class Builder {

    public String baseDir;
    public ArrayList<String> libraries;
    public Logger logger;


    public Builder (String baseDir)
    {
        this.logger = LoggerFactory.getLogger(Builder.class);
        this.baseDir = baseDir;
    }

}
