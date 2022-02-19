package com.nft.cloud.Models;

public class MessageNotificationModel {
    public String to;
    public String type;

    public Notification notification = new Notification();
    public Data data = new Data();

    public static class Notification {
        public String title;
        public String body;
        public String type;
    }

    public static class Data {
        public String title;
        public String body;
        public String type;
    }
}
