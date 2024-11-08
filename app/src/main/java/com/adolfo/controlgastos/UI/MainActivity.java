package com.adolfo.controlgastos.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.adolfo.controlgastos.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    Button btnCerraSesion, btnEliminarCuenta;
    Button btnAcerca, btnMainActivity2, btnPoliticasDe;
    private TextView userNombre, userEmail, userID;
    private CircleImageView userImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Forzar el tema claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        // Vincular vistas del XML
        userNombre = findViewById(R.id.userNombre);
        userEmail = findViewById(R.id.userEmail);
        userID = findViewById(R.id.userId);
        userImg = findViewById(R.id.userImagen);
        btnCerraSesion = findViewById(R.id.btnLogout);
        btnEliminarCuenta = findViewById(R.id.btnEliminarCta);
        btnAcerca = findViewById(R.id.btnAcerca);
        btnMainActivity2 = findViewById(R.id.btnApps);
        btnPoliticasDe = findViewById(R.id.btnPoliticas);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Mostrar datos del usuario
        if (currentUser != null) {
            Log.d("MainActivity", "Usuario ID: " + currentUser.getUid());
            Log.d("MainActivity", "Usuario Nombre: " + currentUser.getDisplayName());
            Log.d("MainActivity", "Usuario Email: " + currentUser.getEmail());

            userID.setText(currentUser.getUid());
            userNombre.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Sin nombre");
            userEmail.setText(currentUser.getEmail());
            Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .into(userImg);
        }

        // Configurar botón "Acerca de"
        btnAcerca.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AcercaDeActivity.class);
            startActivity(intent);
        });

        // Configurar botón para otra actividad
        btnMainActivity2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        });

        // Configurar botón para "Políticas de Privacidad"
        btnPoliticasDe.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PoliticasDeActivity.class);
            startActivity(intent);
        });

        // Configurar opciones de Google Sign-In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Configurar botón de cerrar sesión
        btnCerraSesion.setOnClickListener(v -> signOut());

        // Configurar botón de eliminar cuenta
        btnEliminarCuenta.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

                if (signInAccount != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

                    if (credential != null) {
                        user.reauthenticate(credential).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                deleteUser(user);
                            } else {
                                Log.e("MainActivity", "Error al re-autenticar al usuario.", task.getException());
                                Toast.makeText(getApplicationContext(), "Re-autenticación fallida. Intenta cerrar sesión e iniciar de nuevo.",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Log.e("MainActivity", "Error: No se pudo obtener las credenciales.");
                    }
                } else {
                    Log.d("MainActivity", "Error: cuenta de usuario no encontrada.");
                    Toast.makeText(getApplicationContext(), "No se encontró cuenta de Google vinculada.", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d("MainActivity", "Error: usuario no autenticado.");
                Toast.makeText(getApplicationContext(), "No hay usuario autenticado.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Método para eliminar usuario
    private void deleteUser(FirebaseUser user) {
        user.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("MainActivity", "Usuario eliminado exitosamente.");
                signOut();
            } else {
                Log.e("MainActivity", "Error al eliminar el usuario.", task.getException());
                Toast.makeText(getApplicationContext(), "Error al eliminar la cuenta. Inténtalo de nuevo.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Método para cerrar sesión
    private void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginActivity);
            MainActivity.this.finish();
        });
    }
}
