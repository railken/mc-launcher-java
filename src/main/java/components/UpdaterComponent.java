package components;

import launcher.Builder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import static java.lang.System.out;
import services.Storage;

public class UpdaterComponent extends BaseComponent {

    public UpdaterComponent (Builder builder)
    {
        super(builder);
    }

    public void execute() throws Exception
    {
        this.builder.logger.info("Checking libraries");
        ArrayList<String> libraries = new ArrayList<String>();


        Storage librariesFile = new Storage(this.builder.baseDir+"modpack/bin/1.12.2.json");



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


                    Storage assetFile = new Storage(this.builder.baseDir+"cache/"+path);
                    JSONObject o = downloads.getJSONObject(key);


                    try {

                        if(!assetFile.exists())
                            throw new Exception("file not found");

                        if (assetFile.getSize() != o.getLong("size"))
                            throw new Exception("mismatch size: " + assetFile.getSize() + " != " + o.getLong("size"));

                        if (!assetFile.getChecksum("sha1").equals(o.getString("sha1")))
                            throw new Exception("mismatch sha1" + assetFile.getChecksum("sha1") + " != " + o.getString("sha1"));

                    } catch (Exception e) {
                        this.builder.logger.info(e.getMessage());
                        assetFile.download(new URL(url));
                        this.builder.logger.info("Downloaded: "+url);
                    }

                    this.builder.logger.info("Adding library: " + assetFile.getFilename());
                    libraries.add(assetFile.getFilename());

                }

            }

        }


        libraries.add(this.builder.baseDir+"modpack/bin/modpack.jar");
        libraries.add(this.builder.baseDir+"modpack/bin/minecraft.jar");


        out.println("Checking assets");
        Storage assetCache = new Storage(this.builder.baseDir+"/assets/indexes/"+info.getJSONObject("assetIndex").getString("id")+".json");
        assetCache.download(new URL(info.getJSONObject("assetIndex").getString("url")));

        JSONObject infoAssets = new JSONObject(assetCache.get()).getJSONObject("objects");

        for (int y = 0; y < infoAssets.names().length(); y++) {

            key = infoAssets.names().getString(y);
            String filename = infoAssets.getJSONObject(key).getString("hash");

            filename.substring(0, 2);

            Storage assetFile = new Storage(this.builder.baseDir+"/assets/objects/"+filename.substring(0, 2)+"/"+filename);

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

                this.builder.logger.info("Downloaded: " + url);
            }


        }

        this.builder.libraries = libraries;

        this.updateMods();
    }

    private void updateMods() throws Exception
    {
        Storage modpackFile = new Storage(this.builder.baseDir+"modpack.json");

        if (!modpackFile.exists()) {
            throw new Exception("Missing file: "+ modpackFile.getFilename());
        }

        JSONObject info = new JSONObject(modpackFile.get());

        JSONArray mods = info.getJSONArray("mods");

        ArrayList<String> enabled = new ArrayList<String>();

        for(int i = 0; i < mods.length(); i++) {
            JSONObject mod = mods.getJSONObject(i);
            String url = mod.getString("url");
            String name = mod.getString("name");
            String filename = name + ".jar";

            Storage assetFile = new Storage(this.builder.baseDir+"/modpack/mods/" + filename);

            enabled.add(filename);

            this.builder.logger.info("Checking mod: " + name);

            if( !mod.has("hash")) {
                this.builder.logger.info("Checksum not found. Adding automatically: "+assetFile.getChecksum("sha1")   );

                mod.put("hash", assetFile.getChecksum("sha1"));
            }

            try {

                if(!assetFile.exists())
                    throw new Exception("Mod " + name + " doesn't exist");


                if (!assetFile.getChecksum("sha1").equals(mod.getString("hash")))
                    throw new Exception("Mod " + name + " mismatch hash" + assetFile.getChecksum("sha1") + " != " + mod.getString("hash"));

            } catch (Exception e) {
                this.builder.logger.info(e.toString());
                this.builder.logger.info("Downloading from: " + url);
                assetFile.download(new URL(url));

            }

        }

        modpackFile.set(info.toString(4));

        File folder = new File(this.builder.baseDir+"/modpack/mods/");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {

            File file = listOfFiles[i];
            if (!enabled.contains(file.getName())) {
                this.builder.logger.info("Removing: " + file.getName());
                file.delete();
            }
        }

    }
}
