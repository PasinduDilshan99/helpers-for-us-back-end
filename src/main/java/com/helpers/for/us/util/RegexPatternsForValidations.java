package com.you.and.me.util;

public class RegexPatternsForValidations {
    // Only alphabetic characters (letters) - no numbers, no special characters
    public static final String ONLY_ALPHABETS = "^[a-zA-Z]+$";

    // Only numbers - no letters, no special characters
    public static final String ONLY_NUMBERS = "^[0-9]+$";

    // Alphanumeric with hyphens and underscores only
    public static final String ALPHANUMERIC_WITH_HYPHEN_UNDERSCORE = "^[a-zA-Z0-9_-]+$";

    // Contains special characters (anything other than alphanumeric and basic punctuation)
    public static final String CONTAINS_SPECIAL_CHARS = ".*[^a-zA-Z0-9\\s\\.,!?_-].*";

    // Alphanumeric (letters and numbers only)
    public static final String ALPHANUMERIC = "^[a-zA-Z0-9]+$";

    // No special characters (only letters, numbers, spaces, and basic punctuation)
    public static final String NO_SPECIAL_CHARS = "^[a-zA-Z0-9\\s\\.,!?_-]*$";

    // IPv4 address pattern
    public static final String IPV4_PATTERN =
            "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
                    + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

    // IPv6 address pattern (basic validation)
    public static final String IPV6_PATTERN =
            "([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}"
                    + "|::1|::|([0-9a-fA-F]{1,4}:){1,7}:"
                    + "|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}";

    // General IP address pattern (IPv4 or IPv6)
    public static final String IP_ADDRESS_PATTERN =
            "^(" + IPV4_PATTERN + "|" + IPV6_PATTERN + ")$";


    public static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
}
