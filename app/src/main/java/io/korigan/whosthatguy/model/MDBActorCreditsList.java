package io.korigan.whosthatguy.model;

/**
 * Created by guillaume on 29/04/15.
 */
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

    public static class MDBActorCredit{
        private String character;
        private String original_title;
        private String original_name;
        private String media_type;

        public String getTitle(){
            return MDBUtils.getTitle(original_title, original_name, media_type);
        }
    }
}
