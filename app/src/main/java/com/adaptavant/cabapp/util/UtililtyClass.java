package com.adaptavant.cabapp.util;

/**
 * Created by user on 01/06/17.
 */

public class UtililtyClass {

    public static final String CONTACT_CODE = "code";
    public static final String COMPANY_KEY = "companyKey";
    public static final String USER_INTENT = "usreIntent";
    /**
     * user Details
     */
    public static final String USER_JSON = "user_json";
    public static final String MY_SHARED_PREFRENCE = "My_Pref";
    public static final String USER_NAME = "user_name";
    public static final String USER_KEY = "user_key";
    public static final String USER_STATUS = "user_status";
    public static final String USER_PREFERED_SERVICE = "user_prefred_service";
    public static final String USRE_PREFERED_STAFF = "user_prefred_staff";
    public static final String USER_LOGIN_ID = "user_login_id";
    public static final String USER_FIRST_NAME = "first_name";
    public static final String USER_LAST_NAME = "last_name";
    public static final String USER_VEHICAL_STATUS = "vehical_status";
    public static final String USER_IMAGE_URL = "image_url";
    public static final String IS_USER_LOGED_IN = "login_status";
    public static final String IS_VALID_USER = "is_valid_user";
    public static final String IS_DATA_RETRIVED_COMPLETED = "is_data_retrived";
    public static final String ACCESS_TOKEN = "access_token";


    /**
     * Urls
     */
    public static final String CONTACT_URL = "http://adaptiveufest.appspot.com/getContact";
    public static final String ROUT_URL = "http://adaptiveufest.appspot.com/getAllRoutes?companyKey=084adf34-b48d-4bb7-8795-6a827025a57c";
    public static final String TIMING_URL = "http://adaptiveufest.appspot.com/getAllTimings?companyKey=084adf34-b48d-4bb7-8795-6a827025a57c";
    public static final String BOOKING_URL = "http://adaptiveufest.appspot.com/saveBooking?companyKey=084adf34-b48d-4bb7-8795-6a827025a57c";
    public static final String CANCELLING_URL = "http://adaptiveufest.appspot.com/cancelBooking";
    public static final String FEED_BACK_URL = "http://my.loopto.do/forms/process?json=1&t=1492437716399";
    public static final String GET_BOOKING_TODAY_URL = "http://adaptiveufest.appspot.com/getBooking";
    public static final String PROFILE_PIC_URL = "https://www.googleapis.com/plus/v1/people/me?access_token=";
    /**
     * BroadCast Receiver
     */
    public static final String ROUT_RECIVER = "rout_receiver";
    public static final String CAB_BOOKING_RECIVER = "cab_booking_receiver";
    /**
     * Booking Cab
     */
    public static final String CAB_AVAILABLE = "available";
    public static final String CAB_IS_NOT_AVAILABLE = "not_available";
    public static final String CAB_IS_BOOKED = "booked";
    public static final String IS_CAB_BOOKED = "is_cab_booked";
    public static final String IS_CAB_BOOKED_TODAY = "is_cab_booked_today";

    /**
     * Location Booking Location
     */
    public static final String ROUTE_NAME = "route_name";
    public static final String ROUTE_ID = "route_id";
    public static final String COMPANY_LOCATION = "Full Creative (Ascendas)";
    public static final String CHOOSE_LOCATION = "choose_location";
    public static final String FROM_LOCATION = "from_location";
    public static final String TO_LOCATION = "to_location";
    /**
     * Booking cab Details
     */
    public static final String CAB_BOOK_APPOINTMENT = "appointment";
    public static final String CAB_BOOK_STAR_TIME_LONG = "startTimeLong";
    public static final String CAB_BOOK_STATUS = "status";
    public static final String CAB_BOOK_START_TIME_STRING = "startTimeString";
    public static final String CAB_BOOK_STAFF_KEY = "staffKey";
    public static final String CAB_BOOK_SERVICE_KEY = "serviceKey";
    public static final String CAB_BOOK_KEY = "key";
    public static final String CAB_BOOK_CUSTOMER_KEY = "customerKey";
    public static final String CAB_BOOK_END_TIME_STRING = "endTimeString";
    public static final String CAB_BOOK_F_KEY = "f_Key";
    public static final String CAB_BOOK_DATE = "date";
    public static final String CAB_BOOK_END_TIME_LONG = "endTimeLong";
    public static final String CUSTOMER = "customer";
    public static final String CUSTOMER_NAME = "name";
    public static final String CUSTOMER_PHONE = "phone";
    public static final String CUSTOMER_LOGIN_ID = "loginId";
    public static final String IS_TIME_SELCTED = "is_time_selected";
    public static final String IS_LOCATION_SELECTED = "is_location_selected";
    public static final String CAB_BOOKING_INTENT = "booking_intent";
    public static final String BOOKING_CAB = "booking_cab";
    public static final String CANCEL_BOOKING = "cancel_booking";
    public static final String CLEAR_BOOKING = "clear_booking";
    public static final String GET_TODAY_BOOKING = "get_today_booking";
    public static final String USER_REMINDER_TIME="reminder_time";
    public static final String BOOKIN_DATE="date";
    public static final String BOOKING_CONFIRMED="Booking confirmed successfully";
    public static final String BOOKING_CANCELLED="Booking cancelled successfully";


    /**
     * set the reminder
     */
    public static final int NOTIFY_TO_BOOK_CODE = 12;
    public static final int NOTIFY_TO_LEAVE_CODE = 13;
    public static final int CLEAR_BOOKING_CODE = 14;
    public static final int NOTIFICATION_ID = 3;
    public static final String IS_REMINDER_ON = "is_reminder_on";
    public static final String REMINDER_NOTIFACTION="reminder_notification";
    public static final String NOTIFICATION_TO_BOOK ="Time to book the cab";
    public static final String IS_NOTIFICATION_SENT ="is_notification_sent";
    public static final String NOTIFICATION_TO_GO_TO_CAB="Cab is waiting for you";
    public static final String NOTIFICATION_CLEAR_BOOKING_DETAILS="clear_booking_details";
    public static final String NOTYFYING_TIME="notifying_time";

    /**
     * http header
     */
    public static final String HEADER_URL_ENCODED_CONTENT_TYEPE = "application/x-www-form-urlencoded";
    public static final String HEADER_JSON_CONTENT_TYPE = "application/json";

}

