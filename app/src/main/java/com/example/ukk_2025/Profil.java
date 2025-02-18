package com.example.ukk_2025;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Profil extends AppCompatActivity {

    private TextView nameTextView, emailTextView, phoneTextView; // TextView untuk menampilkan data pengguna
    private Button logoutButton; // Tombol logout
    private String userId;
    private static final String API_URL = "https://yourapi.com/users/"; // Ganti dengan URL API Anda

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // Inisialisasi UI
        nameTextView = findViewById(R.id.Nama);
        emailTextView = findViewById(R.id.Email);
        logoutButton = findViewById(R.id.Logoutbutton);

        // Ambil id dari SharedPreferences, coba ambil dari LoginPrefs dulu, jika kosong ambil dari RegisPrefs
        SharedPreferences loginPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = loginPrefs.getString("idL", null);

        if (userId == null) {
            SharedPreferences regisPrefs = getSharedPreferences("RegisPrefs", MODE_PRIVATE);
            userId = regisPrefs.getString("idR", null);
        }

        if (userId != null) {
            fetchUserData(userId); // Ambil data pengguna berdasarkan ID
        }

        // Set aksi tombol logout
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(); // Panggil metode logout
            }
        });
    }

    private void fetchUserData(String id) {
        String url = API_URL + id; // Buat URL untuk request API

        // Menggunakan Volley untuk request data
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Ambil data dari response JSON
                            String name = response.getString("name");
                            String email = response.getString("email");
                            String phone = response.getString("phone");

                            // Set data ke TextView
                            nameTextView.setText(name);
                            emailTextView.setText(email);
                            phoneTextView.setText(phone);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(Profil.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        // Menambahkan request ke queue
        queue.add(request);
    }

    // Metode untuk logout
    private void logout() {
        // Hapus data di SharedPreferences
        SharedPreferences loginPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences regisPrefs = getSharedPreferences("RegisPrefs", MODE_PRIVATE);

        SharedPreferences.Editor loginEditor = loginPrefs.edit();
        SharedPreferences.Editor regisEditor = regisPrefs.edit();

        loginEditor.clear(); // Hapus data di LoginPrefs
        regisEditor.clear(); // Hapus data di RegisPrefs

        loginEditor.apply();
        regisEditor.apply();

        // Arahkan pengguna kembali ke halaman login
        Intent intent = new Intent(Profil.this, Login.class); // Ganti Login.class sesuai dengan activity login Anda
        startActivity(intent);
        finish(); // Menutup Profil activity agar tidak bisa kembali ke halaman profil
    }
}