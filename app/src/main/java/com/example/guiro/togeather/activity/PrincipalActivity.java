package com.example.guiro.togeather.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.guiro.togeather.R;
import com.example.guiro.togeather.config.ConfiguracaoFirebase;
import com.example.guiro.togeather.helper.Base64Custom;
import com.example.guiro.togeather.helper.Permissao;
import com.example.guiro.togeather.helper.UsuarioFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private String[] permissoesNecessarias = new String[]{

            Manifest.permission.CAMERA

    };
    private ImageView imageButtonCamera;
    private ImageView imagemTeste;
    private StorageReference storageReference;
    private String identificadorUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        // Configurações iniciais
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        // Permissões
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imagemTeste = findViewById(R.id.imageTeste);

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tirarFoto();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Menu Principal");
        setSupportActionBar(toolbar);
    }

    public void btChat(View view){

        startActivity(new Intent(this, ChatActivity.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                abrirTelaLogin();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deslogarUsuario(){

        try {
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void abrirTelaLogin(){
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void tirarFoto(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null){

            startActivityForResult(intent, 1);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK){

            Bundle extras = data.getExtras();
            Bitmap foto = (Bitmap) extras.get("data");

            // Recuperar dados da imagem para o firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            foto.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] dadosImagem = baos.toByteArray();

            // Salvar no firebase
            StorageReference imagemRef = storageReference.
                    child("fotos").
                    child("perfil").
                    child(identificadorUsuario).
                    child("perfil.jpeg");

            UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
