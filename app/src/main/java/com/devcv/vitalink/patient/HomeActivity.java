package com.devcv.vitalink.patient;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.devcv.vitalink.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 1. Enlazamos la barra de navegación del layout
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 2. Configuramos un listener para detectar los clics
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Usamos un if/else para determinar qué ícono se presionó
                if (item.getItemId() == R.id.nav_tratamientos) {
                    loadFragment(new TratamientosFragment());
                    return true;
                } else if (item.getItemId() == R.id.nav_medicos) {
                    // Placeholder para el futuro fragmento
                    loadFragment(new MisMedicosFragment());
                    return true;
                } else if (item.getItemId() == R.id.nav_citas) {
                    loadFragment(new CitasFragment());
                    return true;
                } else if (item.getItemId() == R.id.nav_escaner) {
                    loadFragment(new EscanerFragment());
                    return true;
                } else if (item.getItemId() == R.id.nav_perfil) {
                    loadFragment(new PerfilFragment());
                    return true;
                }
                return false;
            }
        });

        // 3. Cargamos el fragmento inicial por defecto
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_tratamientos);
        }
    }

    // 4. Método de ayuda para cargar los fragmentos en el contenedor
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // R.id.fragment_container es el ID de nuestro FrameLayout en activity_home.xml
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}