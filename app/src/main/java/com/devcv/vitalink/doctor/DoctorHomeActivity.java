package com.devcv.vitalink.doctor;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.devcv.vitalink.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.Toast; // Temporal para placeholders

public class DoctorHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);

        bottomNavigationView = findViewById(R.id.doctor_bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_doctor_citas) {
                loadFragment(new DoctorCitasFragment());
                return true;
            } else if (itemId == R.id.nav_doctor_pacientes) {
                // TODO: Reemplazar con el fragmento de pacientes
                loadFragment(new DoctorPacientesFragment());
                return true;
            } else if (itemId == R.id.nav_doctor_escaner) {
                // TODO: Reemplazar con el fragmento de escáner
                Toast.makeText(this, "Escáner (próximamente)", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_doctor_perfil) {
                // TODO: Reemplazar con el fragmento de perfil
                loadFragment(new DoctorPerfilFragment());
                return true;
            }
            return false;
        });

        // Cargamos el fragmento de Citas por defecto al iniciar
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_doctor_citas);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.doctor_fragment_container, fragment);
        fragmentTransaction.commit();
    }
}