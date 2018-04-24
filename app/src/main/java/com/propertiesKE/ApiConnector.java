package com.propertiesKE;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

class ApiConnector {
    //used to get the user's specific details from database
    JSONArray GetUserDetails(String email) {
        String url = Constants.BASE_URL2 + "passport/getUser.php?email=" + email;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                jsonArray = new JSONArray(entityResponse);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    //used to get all the ads and all their details from the server
    JSONArray GetAds() {
        String url = Constants.BASE_URL + "home/getAllAds.php";
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                jsonArray = new JSONArray(entityResponse);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    //used to get ad specific information from the database
    JSONArray GetAdDetails(int ItemID) {
        String url = Constants.BASE_URL + "home/getAdDetails.php?ItemID=" + ItemID;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                jsonArray = new JSONArray(entityResponse);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    //used to get ads specific to a particular tab
    JSONArray GetTabAds(int Pid) {
        String url = Constants.BASE_URL + "home/getAllTabAds.php?Pid=" + Pid;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                jsonArray = new JSONArray(entityResponse);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    //used to get all the current user's ads
    JSONArray GetMyAds(String Email, String Status) {
        String url = Constants.BASE_URL + "home/getAllMyAds.php?email=" + Email + "&status=" + Status;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                jsonArray = new JSONArray(entityResponse);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    //used to get all the pictures of a specific ad
    JSONArray GetAllPics(String random) {
        String url = Constants.BASE_URL2 + "uploader/getPics.php?random=" + random;

        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = null;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                //Log.e("JSON String : ", entityResponse);
                jsonArray = new JSONArray(entityResponse);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    //used to get ads when the user filters them
    JSONArray GetFilterAds(int Pid, String price, String county) {
        String url = Constants.BASE_URL + "home/getAllFilterAds.php?Pid=" + Pid + "&price=" + price + "&county=" + county;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                jsonArray = new JSONArray(entityResponse);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    //used to check whether email already exists in database
    String CheckEmail(String Email) {
        String url = Constants.BASE_URL + "login/checkEmail.php?email=" + Email;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = null;

        if (httpEntity != null) {
            try {
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    //used to check whether mobile number already exists in database
    String CheckMobile(String Mobile) {
        String url = Constants.BASE_URL + "login/checkMobile.php?mobile=" + Mobile;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = null;

        if (httpEntity != null) {
            try {
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    //used to register a new user into the database
    String RegisterUser(String FirstName, String LastName, String Email, String Mobile, String Password) {
        String url = Constants.BASE_URL + "login/RegisterUser.php?firstname=" + FirstName + "&lastname=" + LastName + "&email=" + Email + "&mobile=" + Mobile + "&password=" + Password;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = null;

        if (httpEntity != null) {
            try {
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    //used to update a user's new profile picture
    String UploadProfilePicture(String Email, String Image) {
        String url = Constants.BASE_URL2 + "passport/upload.php";

        HttpClient httpclient;
        HttpPost httppost;
        ArrayList<NameValuePair> postParameters;
        httpclient = new DefaultHttpClient();
        httppost = new HttpPost(url);

        postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("image", Image));
        postParameters.add(new BasicNameValuePair("email", Email));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity httpEntity = null;
        httpEntity = response.getEntity();

        String SResponse = null;
        try {
            SResponse = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.valueOf(SResponse);
    }

    //used to login existing user into the application
    String LoginUser(String Email, String Password) {
        String url = Constants.BASE_URL + "login/login.php?email=" + Email + "&password=" + Password;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = null;

        if (httpEntity != null) {
            try {
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    //used to upload main ad picture to server
    String UploadAdPicture(String Image, String Random) {
        String url = Constants.BASE_URL2 + "uploader/upload_main_pic.php";

        HttpClient httpclient;
        HttpPost httppost;
        ArrayList<NameValuePair> postParameters;
        httpclient = new DefaultHttpClient();
        httppost = new HttpPost(url);

        postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("random_no", Random));
        postParameters.add(new BasicNameValuePair("image", Image));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity httpEntity = null;
        httpEntity = response.getEntity();

        String SResponse = null;
        try {
            SResponse = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.valueOf(SResponse);
    }

    //used to upload remaining ad pictures to server
    String UploadAdPictures(String Random, String Image) {
        String url = Constants.BASE_URL2 + "uploader/upload_Pics.php";

        HttpClient httpclient;
        HttpPost httppost;
        ArrayList<NameValuePair> postParameters;
        httpclient = new DefaultHttpClient();
        httppost = new HttpPost(url);

        postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("image", Image));
        postParameters.add(new BasicNameValuePair("random_no", Random));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity httpEntity = null;
        assert response != null;
        httpEntity = response.getEntity();

        String SResponse = null;
        try {
            SResponse = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.valueOf(SResponse);
    }

    //used to get all ads searched by the user
    JSONArray GetSearchAds(String search) {
        String url = Constants.BASE_URL + "home/getAllSearchAds.php?title=" + search;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                jsonArray = new JSONArray(entityResponse);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    //used to check the number of hot category ads available
    String CheckNumber(int Pid) {
        String url = Constants.BASE_URL + "login/checkNumber.php?pid=" + Pid;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = null;

        if (httpEntity != null) {
            try {
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    //used to send verify code to the user to reset password
    String SendVerifyCode(String Mobile) {
        String url = Constants.BASE_URL + "login/sendVerifyCode.php?mobile=" + Mobile;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = null;

        if (httpEntity != null) {
            try {
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    //used to verify SMS code sent to user for changing password
    String CheckVerifyCode(String Mobile, String VCode) {
        String url = Constants.BASE_URL + "login/checkVerificationCode.php?mobile=" + Mobile + "&verification_code=" + VCode;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = null;

        if (httpEntity != null) {
            try {
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    //used to update the user's password when password is reset
    String UpdatePassword(String Mobile, String Password) {
        String url = Constants.BASE_URL + "login/UpdatePassword.php?mobile=" + Mobile + "&new_password=" + Password;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = null;

        if (httpEntity != null) {
            try {
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    //used to check the number of ads the logged in user has
    String CheckNumberMyAds(String Email, String Status) {
        String url = Constants.BASE_URL + "login/checkNumberMyAds.php?email=" + Email + "&status=" + Status;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = null;

        if (httpEntity != null) {
            try {
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    //used to get all the chats the current user has
    JSONArray GetChats(String Email) {
        String url = Constants.BASE_URL + "messagingAPI/getChats.php?email=" + Email;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                jsonArray = new JSONArray(entityResponse);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    //used to check for new messages the current user might have
    String CheckNewMessage(String Email) {
        String url = Constants.BASE_URL + "messagingAPI/checkNewMessage.php?email=" + Email;
        HttpEntity httpEntity = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = null;

        if (httpEntity != null) {
            try {
                response = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }
}
