package io.korigan.whosthatguy.model;

import org.parceler.Parcel;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by guillaume on 29/04/15.
 */
@Parcel
public class MDBActorCreditsList {

    private static final int MAX_LENGTH = 6;
    private MDBActorCredit[] cast;

    public String getFormattedAppearance(){
        String formatted = "";
        int length = 0;
        boolean more = false;
        if(cast.length > MAX_LENGTH){
            length = MAX_LENGTH;
            more = true;
        }
        else{
            length = cast.length;
        }
        for(int i=0; i<length;i++){
            formatted += "-"+cast[i].getTitle();
            if(i != length - 1){
                formatted += "\n";
            }
        }
        if(more){
            formatted += "\n...And more!";
        }
        return formatted;
    }

    public int getAppearanceCount(){
        return cast.length;
    }

    public List<MDBActorCredit> getAppearances(){
        return new LinkedList<MDBActorCredit>(Arrays.asList(cast));
    }

    @Parcel
    public static class MDBActorCredit{
        public String id;
        private String character;
        private String original_title;
        private String original_name;
        public String media_type;
        public Date release_date;
        public String poster_path;


        public String getTitle(){
            return MDBUtils.getTitle(original_title, original_name, media_type);
        }

        public String getCharacter(){
            return character;
        }

        public String getReleaseYear(){
            Calendar cal = new GregorianCalendar();
            cal.setTime(release_date);
            return String.valueOf(cal.get(Calendar.YEAR));
        }

        public String getTitleWithYear(){
            if(release_date != null) {
                return getTitle() + " (" + getReleaseYear() + ")";
            }
            else{
                return getTitle();
            }
        }
    }
}
