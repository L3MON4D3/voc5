package com.L3MON4D3.voc5;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Voc5Client {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final OkHttpClient okClient = new OkHttpClient();

    private static Gson gson = new GsonBuilder()
    .serializeNulls()
    .setPrettyPrinting().create();

    private String servername;
    private String email;
    private String password;

    /**
     * Construct new Client using supplied login-Credentials.
     * @param servername Name of server, "https://api.voc5.org" for example.
     * @param email E-Mail of registered user.
     * @param password Users Password.
     */
    public Voc5Client(String servername, String email, String password) {
        this.servername = servername;
        this.email = email ;
        this.password = password;
    }

    public OkHttpClient getOkClient() { return okClient; }

    /**
     * Construct new Client using supplied login-Credentials.
     * @param email E-Mail of registered user.
     * @param password Users Password.
     */
    public Voc5Client(String email, String password) {
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

    /**
     * Build Register-Request.
     * @return Register-Request.
     */
    public Request registerRqst() {
        RequestBody body = RequestBody.create(
            "{\"email\":\""+email+"\", \"password\":\""+password+"\"}", JSON);

        return newRqstBdr("/register")
            .post(body)
            .build();
    }

    /**
     * Build Request to create a new Vocabulary.
     * @param question Word in language A.
     * @param answer Translation to language B.
     * @param lang Name/Code of language B (EN,GER,...).
     * @return Request that can be executed.
     */
    public Request newVocabRqst(String question, String answer, String lang) {
        RequestBody body = RequestBody.create(
            "{\"question\":\""+question+"\", "
            +"\"answer\":\""+answer+"\", "
            +"\"language\":\""+lang+"\"}", JSON);

        return newRqstBdr("/vocab")
            .post(body)
            .build();
    }

    /**
     * Send Changed Vocab to Server.
     * @param voc Vocab with changed fields.
     * @return Request to execute.
     */
    public Request updateVocRqst(Vocab voc) {
        RequestBody body = RequestBody.create(gson.toJson(voc), JSON);

        return newRqstBdr("/voc/"+voc.getId())
            .patch(body)
            .build();
    }
}
