package com.roobo.baselibiray


fun test(any: Any?) {
    val string = any as? String
    string?.length;
}