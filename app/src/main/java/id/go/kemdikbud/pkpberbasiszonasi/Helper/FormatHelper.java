package id.go.kemdikbud.pkpberbasiszonasi.Helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatHelper {

    public boolean isEmail(String email){
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
