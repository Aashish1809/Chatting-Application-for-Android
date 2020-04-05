package Fragments;

import Notification.Response;
import Notification.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAUOJKGA0:APA91bEIweQU6eEj9X_evRl4GBcVKBzRx7nBLpd_RTB49vpDL9jVBNu6HbkNGzY4k2PcOvHgtOoREenNDwNdcn_V9Azu_0uj3D7qHxYOYY_UONQR6s54WrR9WN7RiK5MeoCz3UgNHYGz"
            }
    )
    @POST("fcm/send")
    Call<Response> sendNotification (@Body Sender body);
}
