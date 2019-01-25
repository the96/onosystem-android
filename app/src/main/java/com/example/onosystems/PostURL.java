package com.example.onosystems;

public class PostURL {
    public static final String ORIGIN = "https://www.onosystems.work/aws/";
    public static final int COURIER_USER = 1;
    public static final int CUSTOMER_USER = 2;
    public static String getLoginURL() {
        return ORIGIN + "Login";
    }
    public static String getRegisterAccountURL() {
        return ORIGIN + "RegisterAccount";
    }
    public static String getTopCourierURL() {
        return getDeliveriesURL(COURIER_USER);
    }
    public static String getTopCustomerURL() {
        return getDeliveriesURL(CUSTOMER_USER);
    }
    public static String getDeliveriesURL(int usertype) {
        switch (usertype) {
            case COURIER_USER:
                return ORIGIN + "TopCourier";
            case CUSTOMER_USER:
                return ORIGIN + "TopCustomer";
            default:
                System.out.println("Invalid user type");
                return null;
        }
    }
    public static String getSettingCourierURL() {
        return getSettingUserInfoURL(COURIER_USER);
    }
    public static String getSettingCustomerURL() {
        return getSettingUserInfoURL(CUSTOMER_USER);
    }
    public static String getSettingUserInfoURL(int usertype) {
        switch (usertype) {
            case COURIER_USER:
                return ORIGIN + "SettingCourier";
            case CUSTOMER_USER:
                return ORIGIN + "SettingCustomer";
            default:
                System.out.println("Invalid user type");
                return null;
        }
    }
    public static String getChangeTimeCourierURL() {
        return getChangeTimeURL(COURIER_USER);
    }
    public static String getChangeTimeCustomerURL() {
        return getChangeTimeURL(CUSTOMER_USER);
    }
    public static String getChangeTimeURL(int usertype) {
        switch (usertype) {
            case COURIER_USER:
                return ORIGIN + "ChangeTimeCourier";
            case CUSTOMER_USER:
                return ORIGIN + "ChangeTimeCustomer";
            default:
                System.out.println("Invalid user type");
                return null;
        }
    }
    public static String getCompleteCourierURL() {
        return ORIGIN + "CompleteCourier";
    }
    public static String getReceiveCustomerURL() {
        return ORIGIN + "ReceiveCustomer";
    }
    public static String getInformationCourierURL() {
        return getUserInfoURL(COURIER_USER);
    }
    public static String getInformationCustomerURL() {
        return  getUserInfoURL(CUSTOMER_USER);
    }
    public static String getUserInfoURL(int usertype) {
        switch (usertype) {
            case COURIER_USER:
                return ORIGIN + "InformationCourier";
            case CUSTOMER_USER:
                return ORIGIN + "InformationCustomer";
            default:
                System.out.println("Invalid user type");
                return null;
        }
    }
    public static String getNotificationURL() {
        return ORIGIN + "Notification";
    }
}
