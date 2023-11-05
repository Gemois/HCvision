package com.hcvision.hcvisionserver.mail;

import org.springframework.stereotype.Service;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
public class EmailValidator implements Predicate<String> {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    @Override
    public boolean test(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        return pattern.matcher(s).matches();
    }
}
