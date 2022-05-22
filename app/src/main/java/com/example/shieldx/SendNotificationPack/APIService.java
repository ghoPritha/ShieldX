package com.example.shieldx.SendNotificationPack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAIXb2qLs:APA91bGSZxm4IKQKU4ZatZnPoNnXERjk8znhq-jOOWw4IlKbLAfj7tWmFn-965Y_Ag_ZAXZ4T8IoZbSqWFs5RaPzGBzUE-yGwi-3R1ySqHDueLunfA0apaOsBH4ZgOiCmEwNh4F8hOTQ" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

