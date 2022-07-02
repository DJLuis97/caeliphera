package com.djluis.caeliphera;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.djluis.caeliphera.io.FileHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
	private TextView welcome_main;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		welcome_main = (TextView) findViewById(R.id.welcome_main);
		((Button) findViewById(R.id.btn_go_store_recopilador)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				startActivity(new Intent(MainActivity.this, StoreRecopiladorActivity.class));
			}
		});
		((Button) findViewById(R.id.btn_logout)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				logout();
			}
		});
		if (existsToken()) {
			welcome_main.setText("Bienvenido de nuevo.");
		} else {
			startActivity(new Intent(this, LoginActivity.class));
		}
	}

	private void logout () {
		String url = "https://caeliphera-api.herokuapp.com/api/auth/logout", token;
		try {
			token = FileHelper.getToken(openFileInput("token.txt"));
			if (token == null) throw new RuntimeException("Token nulo");
			StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
				@Override
				public void onResponse (String response) {
					File token_file = new File(getFilesDir(), "token.txt");
					if (token_file.delete()) {
						Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
						startActivity(new Intent(MainActivity.this, LoginActivity.class));
					} else {
						Toast.makeText(MainActivity.this, "Algo paso al cerrar tu sesión :c", Toast.LENGTH_LONG).show();
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse (VolleyError error) {
					Toast.makeText(MainActivity.this, "Error API al cerrar sesión :(", Toast.LENGTH_LONG).show();
					Log.e("(╯°□°）╯︵ ┻━┻ |>", "MainActivity@logout#onErrorResponse -> " + error.toString());
				}
			}) {
				public Map<String, String> getHeaders () {
					Map<String, String> headers = new HashMap<String, String>();
					headers.put("Accept", "application/json");
					headers.put("Content-Type", "application/json");
					headers.put("Authorization", "Bearer " + token);
					return headers;
				}
			};
			Volley.newRequestQueue(MainActivity.this).add(sr);
		} catch (FileNotFoundException exception) {
			Log.e("(╯°□°）╯︵ ┻━┻ |>", "MainActivity@logout -> " + exception.getMessage());
			Toast.makeText(this, "Error al intentar cerrar sesión", Toast.LENGTH_LONG).show();
		}
	}

	private boolean existsToken () {
		try {
			String token = FileHelper.getToken(openFileInput("token.txt"));
			return token != null;
		} catch (FileNotFoundException exception) {
			Log.e("(╯°□°）╯︵ ┻━┻ |>", "MainActivity@existsToken -> " + exception.getMessage());
			welcome_main.setText("");
			return false;
		}
	}
}