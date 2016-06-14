package com.carlisle.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengxin on 1/6/16.
 */
public class Domain {
    public static final String DRIBLE_AUTH_BASE = "https://dribbble.com/oauth/authorize";
    public static final String DRIBLE_CALL_BACK = "walker://www.tuesda.watch";
    public static final String DRIBLE_CLIENT_ID = "75349c965ebf2921cf1aebb3e3e442692441f49df73136f3483b2e0fcd55410d";
    public static final String DRIBLE_SECRET = "33528ad1f9a36832eda52ab7d19e3c382c5d6c4ff993e079a5a4f8aca09ab388";
    public static String state;

    private static final String DRIBLE_LOGIN_URL = DRIBLE_AUTH_BASE
            + "?"
            + "client_id="
            + DRIBLE_CLIENT_ID
            + "&redirect_uri="
            + DRIBLE_CALL_BACK
            + "&scope="
            + "public write comment upload"
            + "&state="
            + state;

    public enum DomainType {
        LOGIN("login"),
        DOMAIN("domain"),
        DRIBLE("drible"),
        SEARCH("search");

        public String type;

        DomainType(String type) {
            this.type = type;
        }
    }

    static private Map<String, String> DRIBLE = new HashMap<String, String>() {
        {
            put(DomainType.LOGIN.type, DRIBLE_LOGIN_URL);
            put(DomainType.DOMAIN.type, "https://dribbble.com/");
            put(DomainType.DRIBLE.type, "https://api.dribbble.com/");
            put(DomainType.SEARCH.type, "https://dribbble.com/search/");
        }
    };

    static private Map<String, String> DRIBLE_TEST = new HashMap<String, String>() {
        {
            put(DomainType.LOGIN.type, DRIBLE_LOGIN_URL);
            put(DomainType.DOMAIN.type, "https://dribbble.com");
            put(DomainType.DRIBLE.type, "https://api.dribbble.com/v1");
            put(DomainType.SEARCH.type, "https://dribbble.com/search/");
        }
    };

    private static Map<String, String> DOMAIN = DRIBLE_TEST;

    static private boolean isDebug = true;

    public static void setIsDebug(boolean isDebug) {
        Domain.isDebug = isDebug;
    }

    public static void setDOMAIN() {
        if (isDebug) {
            DOMAIN = DRIBLE_TEST;
        } else {
            DOMAIN = DRIBLE;
        }
    }

    public static String get(DomainType domainType) {
        return DOMAIN.get(domainType.type);
    }
}
