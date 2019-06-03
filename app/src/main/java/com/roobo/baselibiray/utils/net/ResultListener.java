package com.roobo.baselibiray.utils.net;

/**
 * Created by HP on 2019/3/8.
 */

public interface ResultListener {

    void onSuccess(String result);

    void onFail(int code, String message);
}
