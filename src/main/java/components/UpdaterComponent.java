package components;

import launcher.Builder;
import org.json.JSONArray;
import org.json.JSONObject;
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
        out.println("Checking libraries");
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
                            throw new Exception("mismatch size");

                        if (!assetFile.getChecksum("sha1").equals(o.getString("sha1")))
                            throw new Exception("mismatch sha1");

                    } catch (Exception e) {
                        this.builder.logger.info(e.getMessage());
                        assetFile.download(new URL(url));
                        out.println("Downloaded: "+url);
                    }

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

                out.println("Downloaded: " + url);
            }


        }

        this.builder.libraries = libraries;
    }
}
