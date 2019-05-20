package com.example.guiro.togeather.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guiro.togeather.R;
import com.example.guiro.togeather.config.ConfiguracaoFirebase;
import com.example.guiro.togeather.helper.Permissao;
import com.example.guiro.togeather.helper.UsuarioFirebase;
import com.example.guiro.togeather.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private TextView nomeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        nomeUser = findViewById(R.id.nomeUser);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Recuperar dados do usuário
        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        nomeUser.setText(usuario.getDisplayName());
    }

    public void btFoto(View view) {

        startActivity(new Intent(this, ConfiguracoesActivity.class));

    }

    public void deslogarUsuario(View view){

        try {
            autenticacao.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
