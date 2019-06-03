package com.roobo.baselibiray;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by HP on 2019/3/11.
 */

public class HttpConstants {

    public static final int TIMEOUT_DEFAULT = 90; //请求超时时间

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_DEFAULT_ERROR = -1;
    public static final int CODE_ERROR_JSON = -2;
    public static final int CODE_ERROR_NET = -3;
    public static final int CODE_ERROR_EMPTY_URL = -4;
    public static final int CODE_ERROR_URL_NOT_CORRECT = -5;

    public static final int CODE_TOKEN_EXPIRE = 401;
    public static final String MSG_TOKEN_EXPIRE = "token失效，请重新登录";

    public static final String MSG_NO_RESPONSE = "无结果返回";
    public static final String MSG_DEFAULT = "未知错误";
    public static final String MSG_ERROR_NET = "当前网络不可用";
    public static final String MSG_ERROR_EMPTY_URL = "url为空";
    public static final String MSG_ERROR_URL_NOT_CORRECT = "url不合法";

    public static final String KEY_AUTHORIZATION = "Authorization";

    public static final String KEY_AGENT = "agent";

    public static final String AGENT_ANDROID = "and";

    public static final String KEY_DATA = "data";

    public static final String DEFAULT_BASE_URL = "https://api.github.com/";

    public static final String HOST_URL_PREFIX_1 = "http://";

    public static final String HOST_URL_PREFIX_2 = "https://";

    public static final String SEPARATOR_1 = "?";

    public static final String SEPARATOR_2 = "=";

    public static final String SEPARATOR_3 = "&";

    public static final String SEPARATOR_4 = "/";

    public static final String URL_DEFAULT_HOST = "http://47.95.182.157:8090";

    public static String URL_LOGIN;
    public static String URL_LOGOUT;

    public static String URL_QUERY_MEETING_LIST;
    public static String URL_BOOK_MEETING_INFO;
    public static String URL_MODIFY_MEETING_INFO;
    public static String URL_OPERATOR_MEETING;

    public static String URL_MEMBER_LIST;
    public static String URL_QUERY_DEVICE_LIST;

    public static String URL_UPLOAD_REGISTER_VOICE;

    public static String URL_REGISTER_DEVICE;

    public static String URL_MODIFY_DEVICE_INFO;

    public static String URL_MODIFY_USER_PWD;

    public static String URL_MODIFY_USER_INFO;

    public static String URL_GET_DEVICE_MATCH_NET_RESULT;


    public static void initUrl(String hostUrl) {
        URL_LOGIN = hostUrl + "/signin";
        URL_LOGOUT = hostUrl + "/logout";

        URL_QUERY_MEETING_LIST = hostUrl + "/api/user/meetings";
        URL_BOOK_MEETING_INFO = hostUrl + "/api/user/meetings";
        URL_MODIFY_MEETING_INFO = hostUrl + "/api/user/meeting";
        URL_OPERATOR_MEETING = hostUrl + "/api/user/meetings/operation";

        URL_MEMBER_LIST = hostUrl + "/api/admin/users";
        URL_QUERY_DEVICE_LIST = hostUrl + "/api/admin/devices";

        URL_UPLOAD_REGISTER_VOICE = hostUrl + "/api/user/vpr";

        URL_REGISTER_DEVICE = hostUrl + "/api/device/signin";


        URL_MODIFY_DEVICE_INFO = hostUrl + "/api/user/device/name";
        URL_MODIFY_USER_PWD = hostUrl + "/api/user/password";
        URL_MODIFY_USER_INFO = hostUrl + "/api/user/info";

        URL_GET_DEVICE_MATCH_NET_RESULT = hostUrl + "/api/user/device/link?";

    }

    public static String getUrlParams(HashMap map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        Set<Map.Entry<String, Object>> set = map.entrySet();
        StringBuffer buffer = new StringBuffer();
        buffer.append(SEPARATOR_1);
        int index = 0;
        for (Map.Entry entry : set) {
            if (index != 0) {
                buffer.append(SEPARATOR_3);
            }
            buffer.append(entry.getKey());
            buffer.append(SEPARATOR_2);
            buffer.append(entry.getValue());
            index++;
        }
        return buffer.toString();
    }

}
