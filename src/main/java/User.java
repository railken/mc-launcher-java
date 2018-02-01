public class User {

    private String accessToken;
    private String username;
    private String ID;

    public User setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;

        return this;
    }

    public String getAccessToken()
    {
        return this.accessToken;
    }

    public User setUsername(String username)
    {
        this.username = username;

        return this;
    }

    public String getUsername()
    {
        return this.username;
    }

    public User setID(String ID)
    {
        this.ID = ID;

        return this;
    }

    public String getID()
    {
        return this.ID;
    }

    public String toString()
    {
        return this.username;
    }
}
