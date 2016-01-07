package com.carlisle.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengxin on 1/6/16.
 */
public class Domain {
    public enum DomainType {
        DRIBLE_AUTH_BASE("drible_auth_base"),
        DRIBLE_TOKEN_URL("drible_token_url"),
        DRIBLE_SEARCH_URL("drible_search_url"),
        REQUEST_USER_URL("request_user_url"),
        REQUEST_MY_INFO("request_my_info"),
        REQUEST_ONE_SHOT_URL("REQUEST_ONE_SHOT_URL"),
        CHECK_IF_ME_FOLLOW_URL("check_if_me_follow_url"),
        REQUEST_BUCKETS_URL("request_buckets_url"),

        DOMAIN("domain"),
        DRIBLE("drible"),
        ;

        public String type;

        DomainType(String type) {
            this.type = type;
        }

    }

    static private Map<String, String> DRIBLE = new HashMap<String, String>() {{
        put(DomainType.DRIBLE_AUTH_BASE.type, "https://dribbble.com/oauth/authorize");
        put(DomainType.DRIBLE_TOKEN_URL.type, "https://dribbble.com/oauth/token");
        put(DomainType.DRIBLE_SEARCH_URL.type, "https://dribbble.com/search/");
        put(DomainType.REQUEST_USER_URL.type, "https://api.dribbble.com/v1/users/");
        put(DomainType.REQUEST_MY_INFO.type, "https://api.dribbble.com/v1/user");
        put(DomainType.REQUEST_ONE_SHOT_URL.type, "https://api.dribbble.com/v1/shots/");
        put(DomainType.CHECK_IF_ME_FOLLOW_URL.type, "https://api.dribbble.com/v1/user/following/");
        put(DomainType.REQUEST_BUCKETS_URL.type, "https://api.dribbble.com/v1/buckets/");


        put(DomainType.DOMAIN.type, "https://dribbble.com/");
        put(DomainType.DRIBLE.type, "https://api.dribbble.com/");
    }};

    static private Map<String, String> DRIBLE_TEST = new HashMap<String, String>() {{
        put(DomainType.DRIBLE_AUTH_BASE.type, "https://dribbble.com/oauth/authorize");
        put(DomainType.DRIBLE_TOKEN_URL.type, "https://dribbble.com/oauth/token");
        put(DomainType.DRIBLE_SEARCH_URL.type, "https://dribbble.com/search/");
        put(DomainType.REQUEST_USER_URL.type, "https://api.dribbble.com/v1/users/");
        put(DomainType.REQUEST_MY_INFO.type, "https://api.dribbble.com/v1/user");
        put(DomainType.REQUEST_ONE_SHOT_URL.type, "https://api.dribbble.com/v1/shots/");
        put(DomainType.CHECK_IF_ME_FOLLOW_URL.type, "https://api.dribbble.com/v1/user/following/");
        put(DomainType.REQUEST_BUCKETS_URL.type, "https://api.dribbble.com/v1/buckets/");

        put(DomainType.DOMAIN.type, "https://dribbble.com");
        put(DomainType.DRIBLE.type, "https://api.dribbble.com/v1");
    }};

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
