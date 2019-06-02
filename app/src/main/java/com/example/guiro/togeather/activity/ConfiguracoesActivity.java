package com.example.guiro.togeather.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.guiro.togeather.R;
import com.example.guiro.togeather.config.ConfiguracaoFirebase;
import com.example.guiro.togeather.helper.Permissao;
import com.example.guiro.togeather.helper.UsuarioFirebase;
import com.example.guiro.togeather.model.Mensagem;
import com.example.guiro.togeather.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    private String[] permissoesNecessarias = new String[]{

            Manifest.permission.CAMERA

    };

    private ImageButton imageButtonCamera;
    private CircleImageView circleImageViewFoto;
    private EditText editPerfilNome;
    private Button imageAtualizarNome;
    private StorageReference storageReference;
    private String identificadorUsuario;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        // Configurações iniciais
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        // Permissões
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        circleImageViewFoto = findViewById(R.id.circleImageViewFoto);
        editPerfilNome = findViewById(R.id.editPerfilNome);
        imageAtualizarNome = findViewById(R.id.imageAtualizarNome);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        //toolbar.setTitle("Foto");
        setSupportActionBar(toolbar);

        // Botão voltar
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recuperar dados do usuário
        final FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();

        if (url != null) {

            Glide.with(ConfiguracoesActivity.this)
                    .load(url)
                    .into(circleImageViewFoto);

        } else {

            circleImageViewFoto.setImageResource(R.drawable.padrao);
        }

        editPerfilNome.setText(usuario.getDisplayName());

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tirarFoto();
            }
        });

        imageAtualizarNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome = editPerfilNome.getText().toString();
                boolean retorno = UsuarioFirebase.atualizarNomeUsuario(nome);

                ACProgressFlower dialog = new ACProgressFlower.Builder(ConfiguracoesActivity.this)
                        .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                        .themeColor(Color.WHITE)
                        .text("Salvando foto...")
                        .fadeColor(Color.DKGRAY).build();
                dialog.show();

                if (retorno) {

                    usuarioLogado.setNome(nome);
                    usuarioLogado.atualizar();

                    Toast.makeText(ConfiguracoesActivity.this,
                            "Selfie tirada com sucesso!",
                            Toast.LENGTH_SHORT).show();
                }

                avancarParaChat();
            }
        });

    }

    public void avancarParaChat() {

        startActivity(new Intent(this, MapaActivity.class));
        finish();

    }

    public void tirarFoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(intent, 1);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imagem = (Bitmap) extras.get("data");

            circleImageViewFoto.setImageBitmap(imagem);

            // Recuperar dados da imagem para o firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] dadosImagem = baos.toByteArray();

            // Salvar no firebase
            StorageReference imagemRef = storageReference
                    .child("fotos")
                    .child("perfil")
                    .child(identificadorUsuario + ".jpeg");

            UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(ConfiguracoesActivity.this,
                            "Erro ao fazer upload da imagem",
                            Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener
                            (new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    atualizaFotoUsuario(uri);
                                }
                            });
                }
            });

        }
    }

    public void atualizaFotoUsuario(Uri url) {

        boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);

        if (retorno) {

            usuarioLogado.setFoto(url.toString());
            usuarioLogado.atualizar();
        }


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent i=new Intent(ConfiguracoesActivity.this, PrincipalActivity.class);
        finish();
        startActivity(i);

    }
}
