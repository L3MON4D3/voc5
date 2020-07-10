package com.L3MON4D3.voc5.Client;

import android.os.Parcelable;
import android.os.Parcel;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class is used to create requests to server.
 * It also saves user, password and server
 *
 * @author Simon Katz and Jan Rogge
 */
public class Voc5Client implements Parcelable {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient okClient = new OkHttpClient();

    private static Gson gson = new GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting().create();

    private static final Type vocListType = new TypeToken<ArrayList<Vocab>>(){}.getType();

    private String servername;
    private String email;
    private String password;

    private ArrayList<Vocab> vocabs;

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

    /**
     * Returns only username of email address(foobar@example returns only foobar)
     * @return String User
     */
    public String getUser(){ return this.email.split("@")[0]; }

    /**
     * Find out whether Vocabs were downloaded from server.
     * @return true if there are vocabs, false otherwise.
     */
    public boolean hasVocabs() {
        return vocabs != null;
    }

    public OkHttpClient getOkClient() { return okClient; }
    public ArrayList<Vocab> getVocabs() { return vocabs; }

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

    //Begin Parcelable implementation.
        /**
         * Dummy function.
         * @return 0, always.
         */
        public int describeContents() {
            return 0;   
        }

        /**
         * Write user, email and server to Parcel.
         * @param out Parcel.
         * @param Flags Flags, doesnt do anything right now.
         */
        public void writeToParcel(Parcel out, int Flags) {
            out.writeString(servername);
            out.writeString(email);
            out.writeString(password);
        }

        public static final Parcelable.Creator<Voc5Client> CREATOR = 
            new Parcelable.Creator<Voc5Client>() {

                public Voc5Client createFromParcel(Parcel in) {
                    return new Voc5Client(in);  
                }

                public Voc5Client[] newArray(int size) {
                    return new Voc5Client[size];
                }
            };

        private Voc5Client(Parcel in) {
            servername = in.readString();
            email = in.readString();
            password = in.readString();
        }
    //End Parcelable implementation.

    /**
     * Build Request to create a new Vocabulary.
     * @param question Word in language A.
     * @param answer Translation to language B.
     * @param lang Name/Code of language B (EN,GER,...).
     * @return Request that can be executed.
     */
    public Request newVocabRqst(Vocab voc) {
        RequestBody body = RequestBody.create(
            "{\"question\":\""+voc.getQuestion()+"\", "
            +"\"answer\":\""+voc.getAnswer()+"\", "
            +"\"language\":\""+voc.getLanguage()+"\"}", JSON);

        return newRqstBdr("/voc")
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

    public Request deleteVocRqst(Vocab voc) {
        RequestBody body = RequestBody.create(
                "{\"email\":\""+email+"\", \"password\":\""+password+"\"}", JSON);

        return newRqstBdr("/voc/"+voc.getId())
                .delete(body)
                .build();
    }

    /**
     * Get Request to get all Vocabs as JSON from Server.
     * @return Request to "download" all Vocabs.
     */
    public Request getVocsRqst() {
        return newRqstBdr("/voc").build();
    }

    /**
     * Download Vocab from Server and save them in vocabs. Execute rnble after
     * successfully Downloading Vocabs.
     * @param rnble Runnable, will be executed after vocabs has been populated.
     * Can be null, in that case nothing will be executed.
     */
    public void loadVocs(Runnable rnble, Runnable errorRnble) {
        okClient.newCall(getVocsRqst()).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                errorRnble.run();
            }

            public void onResponse(Call call, Response res) throws IOException {
                vocabs = gson.fromJson(res.body().string(), vocListType);
                //sort vocs by id.
                vocabs.sort(null); 
                if (rnble != null)
                    rnble.run();
            }
        });
    }
}
