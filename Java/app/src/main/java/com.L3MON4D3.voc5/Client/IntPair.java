package com.L3MON4D3.voc5.Client;

import android.os.Parcelable;
import android.os.Parcel;

/**
 * Used to save a pair of Integers.
 * This is useful as it allows sending of
 * id and phase pairs between activities.
 *
 * @author Simon Katz
 */
public class IntPair implements Parcelable {
    public int first;
    public int second;

    /**
     * Create new IntPair.
     * @param first 
     * @param second 
     */
    public IntPair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    //
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
            out.writeInt(first);
            out.writeInt(second);
        }

        public static final Parcelable.Creator<IntPair> CREATOR = 
            new Parcelable.Creator<IntPair>() {

                public IntPair createFromParcel(Parcel in) {
                    return new IntPair(in);  
                }

                public IntPair[] newArray(int size) {
                    return new IntPair[size];
                }
            };

        private IntPair(Parcel in) {
            this.first = in.readInt();
            this.second = in.readInt();
        }
    //End Parcelable implementation.
}
