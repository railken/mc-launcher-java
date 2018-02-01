import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class Storage {

    protected String filename;

    public Storage(String filename)
    {
        this.filename = filename;
    }

    public void set(String content) {

        try {
            PrintWriter out = new PrintWriter(this.filename);
            out.println(content);
            out.close();
        } catch (Exception e) {

        }
    }

    public boolean exists()
    {
        File f = new File(this.filename);
        return f.exists() && !f.isDirectory();
    }

    public String get()
    {
        try {
            return new String(Files.readAllBytes(Paths.get(this.filename)));
        } catch (Exception e) {
            return null;
        }
    }

    public void download(URL url) throws IOException {

        InputStream in = url.openStream();
        File file = new File(filename);
        file.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(file);

        int length = -1;
        byte[] buffer = new byte[1024];

        while ((length = in.read(buffer)) > -1) {
            fos.write(buffer, 0, length);
        }

        fos.close();
        in.close();
    }

    public String getFilename()
    {
        return this.filename;
    }

    public long getSize()
    {
        return new File(this.filename).length();
    }

    public String getChecksum(String encode)
    {

        try {
            MessageDigest digest = MessageDigest.getInstance(encode);

            //Get file input stream for reading the file content
            FileInputStream fis = new FileInputStream(this.filename);

            //Create byte array to read data in chunks
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;

            //Read file data and update in message digest
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            };

            //close the stream; We don't need it now.
            fis.close();

            //Get the hash's bytes
            byte[] bytes = digest.digest();

            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            //return complete hash
            return sb.toString();
        } catch (Exception e) {
            return new String("");
        }
    }
}
