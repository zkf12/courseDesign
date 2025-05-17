package org.example.coursedesign;

public class AppConstants {
    public static final String IMAGE_DIRECTORY = System.getProperty("user.dir") + "/uploads/";

    public static final Integer LOGIN_SUCCESS = 201;
    public static final Integer REGISTER_SUCCESS = 202;
    public static final Integer LOGOUT_SUCCESS = 203;
    public static final Integer GET_USER_SUCCESS = 204;
    public static final Integer GET_LOG_SUCCESS = 205;

    public static final Integer UPDATE_SUCCESS = 301;
    public static final Integer CHANGE_PASSWORD_SUCCESS = 302;
    public static final Integer DELETE_USER_SUCCESS = 303;
    public static final Integer DELETE_LOG_SUCCESS = 304;

    public static final Integer GET_CATEGORIES_SUCCESS = 401;
    public static final Integer GET_PRODUCT_SUCCESS = 402;
    public static final Integer GET_CART_SUCCESS = 403;
    public static final Integer DELETE_PRODUCT_SUCCESS = 404;
    public static final Integer INSERT_PRODUCT_SUCCESS = 405;
    public static final Integer UPDATE_PRODUCT_SUCCESS = 406;
    public static final Integer RECORD_VIEW_SUCCESS = 407;
    public static final Integer GET_RECORD_VIEW_SUCCESS = 408;

    public static final Integer UPDATE_Cart_SUCCESS = 501;
    public static final Integer DELETE_CART_SUCCESS = 502;
    public static final Integer PURCHASE_SUCCESS = 503;
    public static final Integer GET_PURCHASE_HISTORY_SUCCESS = 504;
    public static final Integer GET_PURCHASE_HISTORY_DETAIL_SUCCESS = 505;
    public static final Integer GET_MOST_PRICE_SUCCESS = 506;
    public static final Integer GET_MOST_QUANTITY_SUCCESS = 506;

    public static final Integer GET_SHOP_SUCCESS = 601;
    public static final Integer APPLY_SUCCESS = 602;
    public static final Integer PERMIT_SUCCESS = 603;

    public static final Integer LOGIN_ERROR = 2001;
    public static final Integer REGISTER_ERROR = 2002;
    public static final Integer LOGOUT_ERROR = 2003;
    public static final Integer GET_USER_ERROR = 2004;


    public static final Integer UPDATE_ERROR = 3001;
    public static final Integer CHANGE_PASSWORD_ERROR = 3002;
    public static final Integer DELETE_LOG_ERROR = 3003;


    public static final Integer GET_CATEGORIES_ERROR = 4001;
    public static final Integer GET_PRODUCT_ERROR = 4002;
    public static final Integer NO_SUCH_PRODUCT_ERROR = 4003;
    public static final Integer GET_CART_ERROR = 4004;
    public static final Integer DELETE_CART_ERROR = 4005;


    public static final Integer PURCHASE_ERROR = 5001;
    public static final Integer UPDATE_CART_ERROR = 5002;

    public static final Integer PERMIT_ERROR = 6001;

    public static final Integer UNKNOWN_ERROR = 100000;
}
