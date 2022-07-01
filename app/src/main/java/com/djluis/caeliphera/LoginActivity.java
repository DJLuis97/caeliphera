package com.djluis.caeliphera;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
  private EditText text_username_login, text_password_login;
  private Button btn_login_login;

  @Override protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    text_password_login = findViewById(R.id.text_password_login);
    text_username_login = findViewById(R.id.text_username_login);
    btn_login_login = findViewById(R.id.btn_login_login);
    btn_login_login.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick (View view) {
        loginAction();
      }
    });
  }

  private void loginAction () {
    String url = "https://caeliphera-api.herokuapp.com/api/auth/login";
    StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
      @Override public void onResponse (String response) {
        Toast.makeText(LoginActivity.this, "response", Toast.LENGTH_SHORT).show();
        try {
          JSONObject json = new JSONObject(response);
          Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }, new Response.ErrorListener() {
      @Override public void onErrorResponse (VolleyError error) {
        Toast.makeText(LoginActivity.this, "error response", Toast.LENGTH_SHORT).show();
        Log.e("error loginAction", error.getMessage());
      }
    })
    {
      protected Map<String, String> getParams () {
        Map<String, String> params = new HashMap<>();
        params.put("email", text_username_login.getText().toString());
        params.put("password", text_password_login.getText().toString());

        return params;
      }
    };
    Volley.newRequestQueue(this).add(sr);
  }
}