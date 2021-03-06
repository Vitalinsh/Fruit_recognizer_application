package com.recognitron.fruitrecognizer.client;

import android.util.Log;
import okhttp3.*;
import java.io.IOException;
import java.util.Random;

class RequestSender {
    private String base_url;
    private OkHttpClient client;

    private static final String BAD_DATA_MSG = "Unable to proceed: bad data";
    private static final String BAD_RESPONSE_MSG = "Bad response format";


    RequestSender(String base_url){
        this.base_url = base_url;
        client = new OkHttpClient();
    }

    PostResponse postWithByteData(
            String url, byte[] bytes, String name, String filename, String mediaType
    ) throws IOException {

        if (bytes == null || base_url == null || url == null || name == null || filename == null
                || mediaType == null || HttpUrl.parse(base_url + url) == null) {
            return new PostResponse(BAD_DATA_MSG, false);
        }

        RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(name, filename, RequestBody.create(MediaType.parse(mediaType), bytes))
                    .build();

        String fullUrl = base_url + url;
        Response response = client.newCall(
                new Request.Builder().url(fullUrl).post(body).build()
        ).execute();

        try {
            if (response.code() != 200) {
                return new PostResponse("Error " + response.code(), false);
            } else {
                if (response.body() != null)
                    return new PostResponse(response.body().string(), true);
                else
                    return new PostResponse(BAD_RESPONSE_MSG, false);
            }
        } catch (IOException e) {
            return new PostResponse(BAD_RESPONSE_MSG, false);
        }

    }

    class PostResponse {
        private String message;
        private boolean isReadable;

        PostResponse(String message, boolean readable) {
            this.message = message;
            this.isReadable = readable;
        }

        public String getMessage() {
            return message;
        }

        boolean isReadable() {
            return isReadable;
        }
    }

}
