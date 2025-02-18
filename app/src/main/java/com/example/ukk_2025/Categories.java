package com.example.ukk_2025;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Categories extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CategoriesAdapter adapter;
    private List<CategoriesModel> kategoriList;
    private String userId;
    private static final String URL_VIEW = "http://172.16.0.93//ukk/kategori.php";
    private static final String URL_ADD = "http://172.16.0.93/ukk/tambahKat.php";
    private static final String URL_EDIT = "http://172.16.0.93/ukk/editKat.php";
    private static final String URL_DELETE = "http://172.16.0.93/ukk/hapusKat.php";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        SharedPreferences loginPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = loginPrefs.getString("idL", null);

        if (userId == null) {
            SharedPreferences regisPrefs = getSharedPreferences("RegisPrefs", MODE_PRIVATE);
            userId = regisPrefs.getString("idR", null);
        }

        recyclerView = findViewById(R.id. recyclerViewkategori);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        kategoriList = new ArrayList<>();
        adapter = new CategoriesAdapter(this, kategoriList, new CategoriesAdapter.OnKategoriClickListener() {
            @Override
            public void onEditClick(CategoriesModel kategori) {
                showEditDialog(kategori);
            }

            @Override
            public void onDeleteClick(String id) {
                deleteKategori(id);
            }
        });
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadData);
        loadData();
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        String url = URL_VIEW + "?user_id=" + userId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    kategoriList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            kategoriList.add(new CategoriesModel(obj.getString("id"), obj.getString("category")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                },
                error -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void showEditDialog(CategoriesModel kategori) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_categories);
        EditText editNama = dialog.findViewById(R.id.editNama);
        Button btnSimpan = dialog.findViewById(R.id.btnSimpan);
        editNama.setText(kategori.getKategori());

        btnSimpan.setOnClickListener(v -> {
            String newKategori = editNama.getText().toString().trim();
            if (!newKategori.isEmpty()) {
                editKategori(kategori.getId(), newKategori);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void editKategori(String id, String kategoriBaru) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_EDIT,
                response -> {
                    Toast.makeText(this, "Kategori berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    loadData();
                },
                error -> Toast.makeText(this, "Gagal memperbarui kategori", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("category_id", id);
                params.put("new_name", kategoriBaru);
                params.put("user_id", userId);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void deleteKategori(String id) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_DELETE,
                response -> loadData(),
                error -> Toast.makeText(this, "Gagal menghapus kategori", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("category_id", id);
                params.put("user_id", userId);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}