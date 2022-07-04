package com.djluis.caeliphera;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.djluis.caeliphera.adapters.ListRecopiladorAdapter;
import com.djluis.caeliphera.entities.Parroquia;
import com.djluis.caeliphera.entities.PersonRecopilador;
import com.djluis.caeliphera.io.FileHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListRecopilador extends AppCompatActivity {
	private Button       btn_home_list_recopilador;
	private RecyclerView list_recopiladores;
	private ProgressBar  progress_bar_list_recopilador;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_recopilador);
		setupView();
		llenarRecopiladores();
	}

	private void llenarRecopiladores () {
		showLoading();
		ArrayList<PersonRecopilador> recopiladores = new ArrayList<>();
		String                       url           = "https://caeliphera-api.herokuapp.com/api/v1/recopiladores", token;
		try {
			token = FileHelper.getToken(openFileInput("token.txt"));
			if (token == null) throw new RuntimeException("Token nulo");
			JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse (JSONObject response) {
					hideLoading();
					try {
						JSONArray recopiladores_api = response.getJSONArray("recopiladores");
						if (recopiladores_api.length() <= 0) {
							Toast.makeText(ListRecopilador.this, "No hay recopiladores aún", Toast.LENGTH_LONG).show();
						} else {
							for (int i = 0; i < recopiladores_api.length(); i++) {
								PersonRecopilador pr              = new PersonRecopilador();
								JSONObject        jso_recopilador = recopiladores_api.getJSONObject(i);
								pr.setIdRecopilador(jso_recopilador.getInt("id"));
								//
								if (!jso_recopilador.isNull("parroquia")) {
									JSONObject jso_parroquia = jso_recopilador.getJSONObject("parroquia");
									Parroquia  parroquia     = new Parroquia();
									parroquia.setId(jso_parroquia.getInt("id"));
									parroquia.setName(jso_parroquia.getString("name"));
									pr.setParroquia(parroquia);
								}
								// Person
								JSONObject jso_person = jso_recopilador.getJSONObject("person");
								pr.setFirst_name(jso_person.getString("first_name"));
								pr.setLastName(jso_person.getString("last_name"));
								pr.setCi(jso_person.getString("ci"));
								recopiladores.add(pr);
							}
							list_recopiladores.setAdapter(new ListRecopiladorAdapter(recopiladores));
						}
					} catch (JSONException exception) {
						Log.e("(╯°□°）╯︵ ┻━┻ |>", "ListRecopilador@llenarRecopiladores#onResponse -> " + exception.getMessage());
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse (VolleyError error) {
					hideLoading();
					NetworkResponse response = error.networkResponse;
					if (response != null) {
						Toast.makeText(ListRecopilador.this, "Error API -> " + response.statusCode, Toast.LENGTH_LONG).show();
						if (response.statusCode == 401) {
							Toast.makeText(ListRecopilador.this, "No autorizado", Toast.LENGTH_LONG).show();
							startActivity(new Intent(ListRecopilador.this, LoginActivity.class));
						}
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
			Volley.newRequestQueue(ListRecopilador.this).add(jor);
		} catch (FileNotFoundException exception) {
			Log.e("(╯°□°）╯︵ ┻━┻ |>", "MainActivity@logout -> " + exception.getMessage());
			Toast.makeText(this, "Error al obtener recopiladores", Toast.LENGTH_LONG).show();
			hideLoading();
		}
	}

	private void setupView () {
		btn_home_list_recopilador = (Button) findViewById(R.id.btn_home_list_recopilador);
		list_recopiladores = (RecyclerView) findViewById(R.id.list_recopiladores);
		list_recopiladores.setLayoutManager(new LinearLayoutManager(ListRecopilador.this));
		progress_bar_list_recopilador = (ProgressBar) findViewById(R.id.progress_bar_list_recopilador);
		// Listeners
		btn_home_list_recopilador.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				startActivity(new Intent(ListRecopilador.this, MainActivity.class));
			}
		});
	}

	private void showLoading () {
		progress_bar_list_recopilador.setVisibility(View.VISIBLE);
		btn_home_list_recopilador.setEnabled(false);
	}

	private void hideLoading () {
		progress_bar_list_recopilador.setVisibility(View.GONE);
		btn_home_list_recopilador.setEnabled(true);
	}
}