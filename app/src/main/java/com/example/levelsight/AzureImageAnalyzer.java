package com.example.levelsight;// AzureImageAnalyzer.java

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AzureImageAnalyzer {
    public static final String TAG = AzureImageAnalyzer.class.getSimpleName();
   // private static final String TAG = AzureImageAnalyzer.class.getSimpleName();
    private static final String API_KEY = "497ca231da984902b86f539e1ec928bc";
    private static final String ENDPOINT = "https://eastus.api.cognitive.microsoft.com/";
    private static final String ANALYZE_URL = ENDPOINT + "/vision/v4.0/analyze";

    private final RequestQueue requestQueue;

    public AzureImageAnalyzer(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void analyzeImage(String imageUrl, Response.Listener<JSONObject> successListener, Response.ErrorListener errorListener) {
        String requestUrl = ANALYZE_URL + "?visualFeatures=Categories,Description,Color";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                requestUrl,
                createRequestBody(imageUrl),
                successListener,
                errorListener
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Ocp-Apim-Subscription-Key", API_KEY);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private JSONObject createRequestBody(String imageUrl) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("url", imageUrl);
            return requestBody;
        } catch (Exception e) {
            Log.e(TAG, "Error creating request body: " + e.getMessage());
            return null;
        }
    }
}
