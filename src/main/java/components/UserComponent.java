package components;

import launcher.Builder;
import org.json.JSONObject;
import java.io.Console;
import java.util.Arrays;
import static java.lang.System.out;
import services.AuthService;
import model.User;
import services.Storage;

public class UserComponent extends BaseComponent {

    static String FILE_USER = "user.json";

    public UserComponent (Builder builder)
    {
        super(builder);
    }

    public void execute() {
        AuthService service = new AuthService();

        Storage storage = new Storage(this.builder.baseDir + this.FILE_USER);

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

                Console console = System.console();

                String username = console.readLine("Enter username: ");
                char[] password = console.readPassword("Enter password: ");
                response = service.authenticate(username, String.valueOf(password));
                Arrays.fill(password, ' ');


                storage.set(response.toString());
            }

            User user = new User();
            user.setAccessToken(response.getString("accessToken"));
            user.setUsername(response.getJSONObject("selectedProfile").getString("name"));
            user.setID(response.getJSONObject("selectedProfile").getString("id"));

            this.builder.user = user;

        } catch (Exception e) {
            out.println("Authentication failed");
        }

    }
}
