package com.L3MON4D3.voc5;

import android.os.Parcelable;
import android.os.Parcel;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class Voc5Client implements Parcelable {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final OkHttpClient okClient = new OkHttpClient();

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
}
