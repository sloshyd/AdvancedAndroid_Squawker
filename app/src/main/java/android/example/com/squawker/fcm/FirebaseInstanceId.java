package android.example.com.squawker.fcm;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Darren on 08/10/2017.
 */

class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    /*
    Method is called when a token is Refreshed or first created and is where get token.
     */
    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        sendTokenToServer(token);
    }

    public void sendTokenToServer (String token){
        //method is blank but would be method to communicate the newly generated token to the server
    }
}
