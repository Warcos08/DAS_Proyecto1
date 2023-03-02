package com.example.das_proyecto1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class activity_inicio extends AppCompatActivity {

    // Codigos para gestionar los intents de las diferentes actividades
    private static int resultCodeAjustes = 3;
    private static String idiomaAct = "English";


    // Metodos del ciclo de vida de la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Cargo la pagina en el idioma elegido
        if (savedInstanceState != null) {
            String idioma = savedInstanceState.getString("idioma");

            System.out.println("#");
            System.out.println("#");
            System.out.println("#");
            System.out.println(idioma);
            System.out.println(idiomaAct);

            idiomaAct = idioma;
            Locale nuevaloc;

            if (idioma.equals("English")) {
                nuevaloc = new Locale("en");
            } else {
                nuevaloc = new Locale("es");
            }

            Locale.setDefault(nuevaloc);
            Configuration configuration = getBaseContext().getResources().getConfiguration();
            configuration.setLocale(nuevaloc);
            configuration.setLayoutDirection(nuevaloc);

            Context context = getBaseContext().createConfigurationContext(configuration);
            getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

            finish();
            startActivity(getIntent());
        }
    }


    // Metodos onClick

    public void onClickJugar(View v) {
        // El usuario se puede definir como parte de las preferencias
        // En caso de no estar logueado, deberia saltar un dialogo que de opcion de hacer login o register
        // notificacion cuando te logueas/registras con exito??

        // Cojo el nombre del usuario logueado
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.contains("username")) {
            // Ir a la pantalla de juego
            System.out.println("CON USERNAME");
        } else {
            // Opcion de loguearse
            System.out.println("SIN USERNAME");
            showDialogoLogin();
        }


    }

    public void onClickAjustes(View v) {
        Intent intent = new Intent(activity_inicio.this, actividad_ajustes.class);
        startActivityIntent.launch(intent);
    }

    public void onClickSalir(View v) {
        DialogFragment dialogo_salir = new dialogo_salir();
        dialogo_salir.show(getSupportFragmentManager(), "dialogo_salir");
    }


    // Funcion que crea el dialogo de login
    void showDialogoLogin() {
        // Creo el dialogo de login con el layout
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogo_login);
        dialog.setCancelable(true);

        // Funcion del boton de login
        Button btn_login = (Button) dialog.findViewById(R.id.login_btn);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtengo el nombre de usuario introducido
                EditText input_user = dialog.findViewById(R.id.login_input_username);
                String user = input_user.getText().toString();
                // Obtengo la contraseña
                EditText input_password = dialog.findViewById(R.id.login_input_password);
                String password = input_password.getText().toString();

                String msg1;
                String msg2;
                String msg3;
                // Obtengo el idioma del dispositivo
                Locale locale = getResources().getConfiguration().getLocales().get(0);
                String idioma = locale.getDisplayLanguage();

                if (idioma.equals("English")) {
                    msg1 = "Write down your username and password";
                    msg2 = "Wrong username or password";
                    msg3 = "Logging in";
                } else {
                    msg1 = "Introduce un usuario y contraseña";
                    msg2 = "Usuario o contraseña incorrectos";
                    msg3 = "Logueado correctamente";
                }

                if (user.equals("") || password.equals("")) {
                    // En caso de que no se haya rellenado el formulario mando un mensaje
                    Toast.makeText(activity_inicio.this, msg1, Toast.LENGTH_SHORT).show();

                } else {
                    // Obtengo la BD
                    BD_login gestorBD = new BD_login(activity_inicio.this, "miBD", null, 1);
                    SQLiteDatabase bd = gestorBD.getWritableDatabase();

                    // Compruebo que el usuario se encuentre en la BD
                    Cursor c = bd.rawQuery("SELECT * FROM Usuarios " +
                            "WHERE Username = '" + user + "' " + "AND Password = '" + password + "'", null);

                    // Si hay next es que existe ese usuario con esa contraseña
                    if (c.moveToNext()) {
                        Toast.makeText(activity_inicio.this, msg3, Toast.LENGTH_SHORT).show();
                        // Jugar

                    } else {
                        Toast.makeText(activity_inicio.this, msg2, Toast.LENGTH_SHORT).show();
                    }


                }
            }


        });

        // Funcion del texto de registrarse
        TextView text_registro = (TextView) dialog.findViewById(R.id.login_registro);
        text_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        dialog.show();
    }


    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Locale locale = getResources().getConfiguration().getLocales().get(0);
            String idioma = locale.getDisplayLanguage();
            idiomaAct = idioma;

            finish();
            startActivity(getIntent());

        }
    });


    // Para guardar la info cuando se rote la pantalla
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Guardo el idioma del dispositivo
        savedInstanceState.putString("idioma", idiomaAct);
    }
}