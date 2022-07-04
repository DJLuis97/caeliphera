package com.djluis.caeliphera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.djluis.caeliphera.entities.Parroquia;
import com.djluis.caeliphera.entities.Person;
import com.djluis.caeliphera.io.FileHelper;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreRecopiladorActivity extends AppCompatActivity {
	private EditText txt_ci_recopilador, txt_first_name_recopilador, text_last_name_recopilador, txt_birth_store_recopilador;
	private AutoCompleteTextView auto_complete_text_parroquia, auto_complete_text_encargado;
	private       Map<String, String> payload;
	private final int                 REQUEST_CODE = 1000;
	private       ProgressBar         progress_bar_store_recopilador;
	private       Button              btn_store_recopilador, btn_back_store_recopilador;
	private TextView txt_view_status;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_recopilador);
		setUpView();
		ArrayAdapter<Person>    aa_person = new ArrayAdapter<Person>(this, R.layout.list_item, llenarEncargados());
		ArrayAdapter<Parroquia> aap       = new ArrayAdapter<Parroquia>(this, R.layout.list_item, llenar());
		auto_complete_text_parroquia.setAdapter(aap);
		auto_complete_text_encargado.setAdapter(aa_person);
		findLocation();
	}

	private void findLocation () {
		if (finedLocationGranted()) {
			getLocation();
		} else {
			// No location access granted.
			showPermissionLocation();
		}
	}

	private void getLocation () {
		Log.d("getLocation", "SE HA OBTENIDO LA LOCACION");
		progress_bar_store_recopilador.setVisibility(View.VISIBLE);
		try {
			LocationRequest locationRequest = new LocationRequest();
			locationRequest.setInterval(10000);
			locationRequest.setFastestInterval(30000);
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION
			) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_COARSE_LOCATION
			) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			LocationServices.getFusedLocationProviderClient(this)
											.requestLocationUpdates(locationRequest, new LocationCallback() {
												@Override
												public void onLocationResult (@NonNull LocationResult locationResult) {
													super.onLocationResult(locationResult);
													LocationServices.getFusedLocationProviderClient(StoreRecopiladorActivity.this)
																					.removeLocationUpdates(this);
													if (locationResult.getLocations().size() > 0) {
														int    latestLocationIndex = locationResult.getLocations().size() - 1;
														double latitud             = locationResult.getLocations()
																																			 .get(latestLocationIndex)
																																			 .getLatitude();
														double longitude           = locationResult.getLocations()
																																			 .get(latestLocationIndex)
																																			 .getLongitude();
														payload.put("latitude", String.valueOf(latitud));
														payload.put("longitude", String.valueOf(longitude));
														progress_bar_store_recopilador.setVisibility(View.GONE);
													}
												}
											}, Looper.myLooper());
		} catch (Exception exception) {
			progress_bar_store_recopilador.setVisibility(View.GONE);
			Log.d("exception", "exception -> " + exception.getMessage());
		}
	}

	private boolean finedLocationGranted () {
		return ActivityCompat.checkSelfPermission(this,
			Manifest.permission.ACCESS_FINE_LOCATION
		) == PackageManager.PERMISSION_GRANTED;
	}

	private List<Person> llenarEncargados () {
		showLoading();
		List<Person> encargados = new ArrayList<>();
		try {
			String token = FileHelper.getToken(openFileInput("token.txt"));
			if (token == null) throw new RuntimeException("Token nulo.");
			String url = "https://caeliphera-api.herokuapp.com/api/v1/encargados";
			JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse (JSONObject response) {
					hideLoading();
					progress_bar_store_recopilador.setVisibility(View.GONE);
					Log.d("RESPONSE", response.toString());
					try {
						JSONArray ja_person = response.getJSONArray("people");
						for (int i = 0; i < ja_person.length(); i++) {
							Person person = new Person();
							person.setPersonId(ja_person.getJSONObject(i).getInt("id"));
							person.setFullName(ja_person.getJSONObject(i).getString("full_name"));
							encargados.add(person);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse (VolleyError error) {
					hideLoading();
					progress_bar_store_recopilador.setVisibility(View.GONE);
					NetworkResponse response = error.networkResponse;
					if (response != null) {
						if (response.statusCode == 401) {
							Toast.makeText(StoreRecopiladorActivity.this, "No autorizado", Toast.LENGTH_LONG).show();
							startActivity(new Intent(StoreRecopiladorActivity.this, LoginActivity.class));
						} else {
							Toast.makeText(StoreRecopiladorActivity.this, "Error API => " + response.statusCode, Toast.LENGTH_LONG)
									 .show();
						}
					}
				}
			}) {
				@Override
				public Map<String, String> getHeaders () {
					Map<String, String> headers = new HashMap<>();
					headers.put("Accept", "application/json");
					headers.put("Authorization", "Bearer " + token);
					return headers;
				}
			};
			Volley.newRequestQueue(this).add(jor);
		} catch (FileNotFoundException exception) {
			hideLoading();
			Log.e("(╯°□°）╯︵ ┻━┻ |>", this.getClass().toString() + "@llenarEncargados -> " + exception.getMessage());
		}
		return encargados;
	}

	private List<Parroquia> llenar () {
		showLoading();
		List<Parroquia> parroquias = new ArrayList<>();
		try {
			String token = FileHelper.getToken(openFileInput("token.txt"));
			if (token == null) throw new RuntimeException("Token nulo.");
			String url = "https://caeliphera-api.herokuapp.com/api/v1/parroquias";
			JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse (JSONObject response) {
					hideLoading();
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
					hideLoading();
					NetworkResponse response = error.networkResponse;
					if (response != null && response.data != null) {
						if (response.statusCode == 401) {
							Toast.makeText(StoreRecopiladorActivity.this, "No autorizado", Toast.LENGTH_LONG).show();
							startActivity(new Intent(StoreRecopiladorActivity.this, LoginActivity.class));
						} else {
							Toast.makeText(StoreRecopiladorActivity.this,
								"Error de respuesta: " + response.statusCode,
								Toast.LENGTH_LONG
							).show();
						}
					} else {
						Log.e("(╯°□°）╯︵ ┻━┻ |>", "StoreRecopiladorActivity@llenar#onErrorResponse -> " + error.toString());
					}
				}
			}) {
				@Override
				public Map<String, String> getHeaders () {
					Map<String, String> headers = new HashMap<String, String>();
					headers.put("Accept", "application/json");
					headers.put("Authorization", "Bearer " + token);
					return headers;
				}
			};
			Volley.newRequestQueue(this).add(jor);
		} catch (FileNotFoundException exception) {
			hideLoading();
			Log.e("(╯°□°）╯︵ ┻━┻ |>", "StoreRecopiladorActivity@llenar -> " + exception.getMessage());
		}
		return parroquias;
	}

	private void setUpView () {
		progress_bar_store_recopilador = (ProgressBar) findViewById(R.id.progress_bar_store_recopilador);
		text_last_name_recopilador = (EditText) findViewById(R.id.text_last_name_recopilador);
		txt_ci_recopilador = (EditText) findViewById(R.id.txt_ci_recopilador);
		txt_first_name_recopilador = (EditText) findViewById(R.id.txt_first_name_recopilador);
		txt_birth_store_recopilador = (EditText) findViewById(R.id.txt_birth_store_recopilador);
		auto_complete_text_parroquia = (AutoCompleteTextView) findViewById(R.id.auto_complete_text_parroquia);
		auto_complete_text_encargado = (AutoCompleteTextView) findViewById(R.id.auto_complete_text_encargado);
		txt_view_status = (TextView) findViewById(R.id.txt_view_status);
		payload = new HashMap<>();
		btn_back_store_recopilador = ((Button) findViewById(R.id.btn_back_store_recopilador));
		btn_store_recopilador = ((Button) findViewById(R.id.btn_store_recopilador));
		// Listeners
		btn_back_store_recopilador.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				startActivity(new Intent(StoreRecopiladorActivity.this, MainActivity.class));
			}
		});
		btn_store_recopilador.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				store();
			}
		});
		auto_complete_text_parroquia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick (AdapterView<?> adapterView, View view, int i, long l) {
				payload.put("parroquia_id", String.valueOf(((Parroquia) adapterView.getItemAtPosition(i)).getId()));
			}
		});
		auto_complete_text_encargado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick (AdapterView<?> adapterView, View view, int i, long l) {
				payload.put("id_encargado", String.valueOf(((Person) adapterView.getItemAtPosition(i)).getPersonId()));
			}
		});
	}

	private void store () {
		refillPayload();
		findLocation();
		if (finedLocationGranted()) {
			showLoading();
			try {
				String token = FileHelper.getToken(openFileInput("token.txt"));
				if (token == null) throw new RuntimeException("Token nulo");
				Log.d("PAYLOAD", payload.toString());
				String url = "https://caeliphera-api.herokuapp.com/api/v1/recopiladores";
				JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST,
					url,
					new JSONObject(payload),
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse (JSONObject response) {
							hideLoading();
							Toast.makeText(StoreRecopiladorActivity.this, "Recopilador Guardado", Toast.LENGTH_SHORT).show();
							startActivity(new Intent(StoreRecopiladorActivity.this, MainActivity.class));
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse (VolleyError error) {
							hideLoading();
							NetworkResponse response = error.networkResponse;
							if (response != null) {
								Toast.makeText(StoreRecopiladorActivity.this, "Error API => " + response.statusCode, Toast.LENGTH_SHORT)
										 .show();
								if (response.statusCode == 422) {
									txt_view_status.setText("Los datos proporcionados no son válidos.");
								}
							}
						}
					}
				) {
					@Override
					public Map<String, String> getHeaders () {
						Map<String, String> headers = new HashMap<>();
						headers.put("Accept", "application/json");
						headers.put("Authorization", "Bearer " + token);
						headers.put("Content-Type", "application/json");
						return headers;
					}
				};
				Volley.newRequestQueue(this).add(jor);
			} catch (FileNotFoundException exception) {
				hideLoading();
				Log.e("(╯°□°）╯︵ ┻━┻ |>", this.getClass().toString() + "@store -> " + exception.getMessage());
			}
		} else {
			Toast.makeText(StoreRecopiladorActivity.this, "Permiso denegado", Toast.LENGTH_LONG).show();
		}
	}

	private void refillPayload () {
		payload.put("ci", txt_ci_recopilador.getText().toString());
		payload.put("first_name", txt_first_name_recopilador.getText().toString());
		payload.put("last_name", text_last_name_recopilador.getText().toString());
		payload.put("birth", txt_birth_store_recopilador.getText().toString());
	}

	private void showPermissionLocation () {
		ActivityCompat.requestPermissions(StoreRecopiladorActivity.this,
			new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
			REQUEST_CODE
		);
	}

	@Override
	public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		Log.d("", "onRequestPermissionsResult -> permissions" + Arrays.toString(permissions));
		Log.d("", "onRequestPermissionsResult -> grantResults" + Arrays.toString(grantResults));
		Log.d("", "onRequestPermissionsResult -> requestCode" + requestCode);
		if (requestCode == REQUEST_CODE && grantResults.length > 0) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				getLocation();
			} else {
				Toast.makeText(this, "Por favor, permite la ubicacion para poder realizar esta accion", Toast.LENGTH_LONG)
						 .show();
			}
		}
	}

	private void showLoading () {
		txt_view_status.setText("");
		progress_bar_store_recopilador.setVisibility(View.VISIBLE);
		btn_back_store_recopilador.setEnabled(false);
		btn_store_recopilador.setEnabled(false);
		auto_complete_text_encargado.setEnabled(false);
		auto_complete_text_parroquia.setEnabled(false);
	}

	private void hideLoading () {
		progress_bar_store_recopilador.setVisibility(View.GONE);
		btn_back_store_recopilador.setEnabled(true);
		btn_store_recopilador.setEnabled(true);
		auto_complete_text_encargado.setEnabled(true);
		auto_complete_text_parroquia.setEnabled(true);
	}
}