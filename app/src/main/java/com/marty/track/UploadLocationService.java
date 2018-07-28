package com.marty.track;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Marty on 12/20/2017.
 */

public class UploadLocationService extends IntentService{

    private static final String TAG = "UploadLocationService";
    // Firebase bijwerken
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<Location> points;

    public UploadLocationService(){
        super("UploadLocationService");
    }
    public UploadLocationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent !=null) {
            Bundle b = intent.getExtras();
            if (b != null) {
                points = intent.getParcelableArrayListExtra("points");
                URL url;
                try {
                        url = new URL("http://localhost:8000/trackdrivertrip/update"); // set your server url
                        sendLocations();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendLocations(){
        try {
            String jsonResp;
            int code = 0;

            Map<String, Object> locRec = new HashMap<>();
            locRec.put("id", "userid");

            // JSONObject jsonObject = new JSONObject();
            // jsonObject.put("id","userid");
            // JSONArray pointsArray = new JSONArray();
            for (int i = 0; i < points.size(); i++) {
                locRec.put("getLongitude", points.get(i).getLongitude());
                locRec.put("getLatitude", points.get(i).getLatitude());
                locRec.put("getTime", points.get(i).getTime());
                locRec.put("hasSpeed", points.get(i).hasSpeed());
                locRec.put("getSpeed", points.get(i).getSpeed());
                locRec.put("hasAccuracy", points.get(i).hasAccuracy());
                locRec.put("getAccuracy", points.get(i).getAccuracy());
                // locRec.put("getExtras", points.get(i).getExtras());
                // locRec.put("getProvider", points.get(i).getProvider());
                // pointsArray.put(new JSONArray().put(points.get(i).getLongitude()).put(points.get(i).getLatitude()));
            }
            // jsonObject.put("coordinates",pointsArray);
            Log.d("data sent", locRec.toString());

            // Add a new document with a generated ID
            db.collection("gps")
                    .add(locRec)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });


//            if (pointsArray.length() != 0) {
//                jsonResp = ServiceCall.doServerCall("POST", url, pointsArray.toString(), token);
//                if (jsonResp != null && jsonResp.equals(ServiceCall.TIME_OUT)) {
//                    code = 500;
//                    return;
//                } else if (jsonResp == null) {
//                    code = 500;
//                    return;
//                }
////                Log.d("resp", jsonResp);
////                JSONObject json = new JSONObject(jsonResp);
////                code = Integer.parseInt(json.getString("code"));
//            } else {
//                code = 100;
//            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
