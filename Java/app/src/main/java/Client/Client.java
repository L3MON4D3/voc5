import okhttp3.Request;

public class Client {
    private String servername;
    private String email;
    private String password;

    /**
     * Construct new Client using supplied login-Credentials.
     * @param servername Name of server, "https://api.voc5.org" for example.
     * @param email E-Mail of registered user.
     * @param password Users Password.
     */
    public Client(String servername, String email, String password) {
        this.servername = servername;
        this.email = email ;
        this.password = password;
    }

    /**
     * Construct new Client using supplied login-Credentials.
     * @param email E-Mail of registered user.
     * @param password Users Password.
     */
    public Client(String email, String password) {
        this("https://api.voc5.org", email, password);
    }

    /**
     * Builds new Request using given Path and email+password.
     * Use login() to login.
     * @param path Path on Website. To access "website.com/test" pass "/test".
     * @return Request.Builder on servername/path with headers email and password.
     */
    public Request.Builder newRqstBdr(String path) {
        return new Request.Builder()
            .url(servername + path)
            .header("email", email)
            .header("password", password);
    }

    /**
     * Build login-Request.
     * @return Login-Request.
     */
    public Request loginRqst() {
        return newRqstBdr("/login").build();
    }
}
