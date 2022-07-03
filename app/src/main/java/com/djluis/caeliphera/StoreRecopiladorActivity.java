package com.djluis.caeliphera;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.djluis.caeliphera.entities.Parroquia;
import com.djluis.caeliphera.io.FileHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreRecopiladorActivity extends AppCompatActivity {
	private EditText txt_ci_recopilador, txt_first_name_recopilador, text_last_name_recopilador;
	private AutoCompleteTextView auto_complete_text_parroquia;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_recopilador);
		setUpView();
		ArrayAdapter<Parroquia> aap = new ArrayAdapter<Parroquia>(this, R.layout.list_item, llenar());
		auto_complete_text_parroquia.setAdapter(aap);
	}

	private List<Parroquia> llenar () {
		List<Parroquia> parroquias = new ArrayList<>();
		try {
			String token = FileHelper.getToken(openFileInput("token.txt"));
			if (token == null) throw new RuntimeException("Token nulo.");
			String url = "https://caeliphera-api.herokuapp.com/api/v1/parroquias";
			JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse (JSONObject response) {
					Log.d("data => ", response.toString());
					try {
						JSONArray ja = response.getJSONArray("parroquias");
						for (int i = 0; i < ja.length(); i++) {
							Parroquia p = new Parroquia();
							p.setId(ja.getJSONObject(i).getInt("id"));
							p.setName(ja.getJSONObject(i).getString("name"));
							parroquias.add(p);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse (VolleyError error) {
					NetworkResponse response = error.networkResponse;
					if (response != null && response.data != null) {
						Toast.makeText(StoreRecopiladorActivity.this,
								"Error de respuesta: " + response.statusCode,
								Toast.LENGTH_LONG
						).show();
					} else {
						Log.e("(╯°□°）╯︵ ┻━┻ |>", "StoreRecopiladorActivity@llenar#onErrorResponse -> " + error.toString());
					}
				}
			}) {
				@Override
				public Map<String, String> getHeaders () throws AuthFailureError {
					Map<String, String> headers = new HashMap<String, String>();
					headers.put("Accept", "application/json");
					headers.put("Authorization", "Bearer " + token);
					return headers;
				}
			};
			Volley.newRequestQueue(this).add(jor);
		} catch (FileNotFoundException exception) {
			Log.e("(╯°□°）╯︵ ┻━┻ |>", "StoreRecopiladorActivity@llenar -> " + exception.getMessage());
		}
		return parroquias;
	}

	private void setUpView () {
		text_last_name_recopilador = (EditText) findViewById(R.id.text_last_name_recopilador);
		txt_ci_recopilador = (EditText) findViewById(R.id.txt_ci_recopilador);
		txt_first_name_recopilador = (EditText) findViewById(R.id.txt_first_name_recopilador);
		auto_complete_text_parroquia = (AutoCompleteTextView) findViewById(R.id.auto_complete_text_parroquia);
		((Button) findViewById(R.id.btn_back_store_recopilador)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				startActivity(new Intent(StoreRecopiladorActivity.this, MainActivity.class));
			}
		});
		((Button) findViewById(R.id.btn_store_recopilador)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				Toast.makeText(StoreRecopiladorActivity.this, "Recopilador Guardado", Toast.LENGTH_SHORT).show();
			}
		});
		auto_complete_text_parroquia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick (AdapterView<?> adapterView, View view, int i, long l) {
				String item = ((Parroquia) adapterView.getItemAtPosition(i)).getName();
				Toast.makeText(getApplicationContext(), "Item => " + item, Toast.LENGTH_SHORT).show();
			}
		});
	}
}