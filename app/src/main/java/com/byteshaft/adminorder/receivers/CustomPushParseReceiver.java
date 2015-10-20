package com.byteshaft.adminorder.receivers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.byteshaft.adminorder.AppGlobals;
import com.byteshaft.adminorder.MainActivity;
import com.byteshaft.adminorder.utils.NotificationUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomPushParseReceiver extends ParsePushBroadcastReceiver {

    private Intent parseIntent;
    private String personName;
    private String phoneNumber;
    private String address;
    private String product;
    private String from;
    private String deliveryTime;

    public CustomPushParseReceiver() {
        super();
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        if (intent == null)
            return;
        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
//            Log.e(AppGlobals.getLogTag(getClass()), "Push received: " + json);
            parseIntent = intent;
            String name = json.getString("name");
            String phone = json.getString("phone");
            String address = json.getString("address");
            String product = json.getString("product");
            // etc that departmental store
            String from = json.getString("from");
            String delivery = json.getString("delivery_time");
            AppGlobals.sSenderId = json.getString("sender_id").trim();
            sendResponseToUser(AppGlobals.sSenderId);
            System.out.println(name);
            parsePushJson(name);
            System.out.println(AppGlobals.sSenderId);
        } catch (JSONException e) {
            Log.e(AppGlobals.getLogTag(getClass()), "Push message json exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    private void parsePushJson(String message) {
        String title = "New Order";
        Intent resultIntent = new Intent(AppGlobals.getContext(), MainActivity.class);
        showNotificationMessage(title, message, resultIntent);
    }

    private void showNotificationMessage(String title, String message, Intent intent) {
        NotificationUtils notificationUtils = new NotificationUtils();
        intent.putExtras(parseIntent.getExtras());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent);
    }

    private void sendResponseToUser(String senderId) {
        ParseQuery<ParseInstallation> parseQuery = ParseQuery.getQuery(ParseInstallation.class);
        parseQuery.whereEqualTo("user", senderId);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title" , "Thank You");
            jsonObject.put("response", AppGlobals.PUSH_RESPONCE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ParsePush.sendDataInBackground(jsonObject, parseQuery);
    }
}