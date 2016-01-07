/*
 * Copyright (c) 2013-2015 Shaleen Jain <shaleen.jain95@gmail.com>
 *
 * This file is part of UPES Academics.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.shalzz.attendance.network;

import android.content.res.Resources;
import android.util.Base64;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shalzz.attendance.R;
import com.shalzz.attendance.model.PeriodModel;
import com.shalzz.attendance.model.SubjectModel;
import com.shalzz.attendance.model.UserModel;
import com.shalzz.attendance.wrapper.MyPreferencesManager;
import com.shalzz.attendance.wrapper.MyVolley;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataAPI {

    public static final String TAG = "Gson_Request";
    public static final String USER_TAG = "USER_Gson_Request";

    public static void getUser(Response.Listener<UserModel> successListener,
                               Response.ErrorListener errorListener) {

        String creds = MyPreferencesManager.getUser();
        getUser(successListener, errorListener, creds);
    }

    public static void getUser(Response.Listener<UserModel> successListener,
                               Response.ErrorListener errorListener,
                               final String credentials ) {

        Resources res = MyVolley.getMyResources();
        Map<String, String> headers = new HashMap<String, String>();
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headers.put("Authorization", auth);
        headers.put("UserModel-Agent", res.getString(R.string.UserAgent));

        String mURL = res.getString(R.string.URL_user);
        GsonRequest<UserModel> gsonRequest = new GsonRequest<>(
                mURL,
                UserModel.class,
                headers,
                successListener,
                errorListener);

        gsonRequest.setShouldCache(false);
        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyVolley.getInstance().addToRequestQueue(gsonRequest ,USER_TAG);
    }

    public static void getAttendance(Response.Listener<ArrayList<SubjectModel>> successListener,
                                     Response.ErrorListener errorListener) {

        Resources res = MyVolley.getMyResources();
        final Map<String, String> headers = new HashMap<String, String>();
        String creds = MyPreferencesManager.getUser();
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
        headers.put("Authorization", auth);
        headers.put("UserModel-Agent", res.getString(R.string.UserAgent));

        String mURL = res.getString(R.string.URL_attendance);
        Type collectionType = new TypeToken<ArrayList<SubjectModel>>(){}.getType();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        GsonRequest<ArrayList<SubjectModel>> requestAttendance = new GsonRequest<>(
                mURL,
                null,
                collectionType,
                gson,
                headers,
                successListener,
                errorListener);

        requestAttendance.setShouldCache(false);
        requestAttendance.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyVolley.getInstance().addToRequestQueue(requestAttendance ,TAG);
    }

    public static void getTimeTable(Response.Listener<ArrayList<PeriodModel>> successListener,
                                    Response.ErrorListener errorListener) {

        Resources res = MyVolley.getMyResources();
        Map<String, String> headers = new HashMap<String, String>();
        String creds = MyPreferencesManager.getUser();
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
        headers.put("Authorization", auth);
        headers.put("UserModel-Agent", res.getString(R.string.UserAgent));

        String mURL = res.getString(R.string.URL_timetable);
        Type collectionType = new TypeToken<ArrayList<PeriodModel>>(){}.getType();
        GsonRequest<ArrayList<PeriodModel>> requestTimeTable = new GsonRequest<>(
                mURL,
                collectionType,
                headers,
                successListener,
                errorListener);

        requestTimeTable.setShouldCache(false);
        requestTimeTable.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyVolley.getInstance().addToRequestQueue(requestTimeTable ,TAG);
    }
}