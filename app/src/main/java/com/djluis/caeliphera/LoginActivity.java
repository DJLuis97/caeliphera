package com.djluis.caeliphera;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
  private EditText text_username_login, text_password_login;
  private Button btn_login_login;
  TextView helper_username_login, helper_password_login;

  @Override protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    text_password_login = findViewById(R.id.text_password_login);
    text_username_login = findViewById(R.id.text_username_login);
    btn_login_login = findViewById(R.id.btn_login_login);
    helper_username_login = findViewById(R.id.welcome_main);
    helper_password_login = findViewById(R.id.helper_password_login);
    btn_login_login.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick (View view) {
        loginAction(
            text_username_login.getText().toString(), text_password_login.getText().toString());
      }
    });
  }

  private void loginAction (String email, String password) {
    cleanHelpers();
    String              url    = "https://caeliphera-api.herokuapp.com/api/auth/login";
    Map<String, String> params = new HashMap<>();
    params.put("email", email);
    params.put("password", password);
    params.put("device_name", Build.MODEL);
    JsonObjectRequest jor = new JsonObjectRequest(
        Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
      @Override public void onResponse (JSONObject response) {
        SQLite sqLite = new SQLite(
            LoginActivity.this, "caeliphera", null, 1);
        SQLiteDatabase writable_db = sqLite.getWritableDatabase();
        try {
          String email = response.getJSONObject("user").getString("email");
          String full_name = response.getJSONObject("user").getJSONObject("person")
                                     .getString("full_name");
          String        id    = response.getJSONObject("user").getString("id");
          String        token = response.getString("access_token");
          ContentValues cv    = new ContentValues();
          cv.put("id", id);
          cv.put("email", email);
          cv.put("full_name", full_name);
          cv.put("token", token);
          writable_db.insert("users", null, cv);
          writable_db.close();
          startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } catch (JSONException e) {
          Log.e(
              "exception", "(╯°□°）╯︵ ┻━┻ |> " + e.getMessage());
          Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
        }
      }
    }, new Response.ErrorListener() {
      @Override public void onErrorResponse (VolleyError error) {
        String          json     = null;
        NetworkResponse response = error.networkResponse;
        if (response != null && response.data != null) {
          json = new String(response.data);
          String message = getJson(json, "message");
          if (message != null) {
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
          }
          switch (response.statusCode) {
            case 422:
              try {
                String errors = getJson(json, "errors");
                JSONArray error_email = getJsonArray(
                    errors, "email"), error_password = getJsonArray(errors, "password");
                helper_username_login.setText(error_email.getString(0));
                helper_password_login.setText(error_password.getString(0));
              } catch (JSONException e) {
                e.printStackTrace();
              } catch (Exception e) {
                e.printStackTrace();
              }
              break;
            default:
              Toast.makeText(LoginActivity.this, "Unknown Server Error", Toast.LENGTH_SHORT).show();
          }
        } else {
          Log.e("loginAction", "(╯°□°）╯︵ ┻━┻ |> " + error.toString());
        }
      }
    })
    {
      public Map<String, String> getHeaders () {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        return headers;
      }
    };
    Volley.newRequestQueue(this).add(jor);
  }

  private String getJson (String json, String key) {
    try {
      JSONObject jo = new JSONObject(json);
      return jo.getString(key);
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }

  private JSONArray getJsonArray (String json, String key) {
    try {
      JSONObject jo = new JSONObject(json);
      return jo.getJSONArray(key);
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }

  private void cleanHelpers () {
    helper_username_login.setText("");
  }
}