package com.flowrithm.todtracker.WebApi;

public class Api {

    public static String Domain="http://TODTracker.sharpnettechnology.com/api/";

    public static String HOST="http://TODTracker.sharpnettechnology.com";

    public static String Login=Domain+"Account/Login";
    public static String GET_TRANSPORTS=Domain+"Transports/GetTransports";
    public static String GET_TRANSPORT_DETAIL=Domain+"Transports/GetTransportDetail";
    public static String GET_TRANSPORT_HISTORY=Domain+"Transports/GetTransportHistory";

    public static String POST_BLOCK_UNBLOCK=Domain+"Transports/UpdateTransportStatuWithFile";
    public static String POST_SEND_OTP_REGISTRATION=Domain+"Account/SendOTPForRegistration";
    public static String POST_SEND_OTP_FORGOT_PASSWORD=Domain+"Account/SendOTPForgotPassword";
    public static String POST_FORGOT_PASSWORD=Domain+"Account/ForgotPassword";
    public static String POST_CHANGE_PASSWORD=Domain+"Account/ChangePassword";

    public static String POST_REGISTER=Domain+"Account/Register";
}
