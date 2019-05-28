package com.example.guiro.togeather.helper;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.guiro.togeather.config.ConfiguracaoFirebase;
import com.example.guiro.togeather.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {

    public static String getIdentificadorUsuario() {

        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String email = usuario.getCurrentUser().getEmail();
        String identificadorUsuario = Base64Custom.codificarBase64(email);

        return identificadorUsuario;
    }

    public static FirebaseUser getUsuarioAtual() {

        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();

        return usuario.getCurrentUser();
    }

    public static Usuario getDadosUsuarioLogadoChamado(){
        FirebaseUser firebaseUser = getUsuarioAtual();
         Usuario usuario = new Usuario();

         usuario.setId(firebaseUser.getUid());
         usuario.setEmail(firebaseUser.getEmail());
         usuario.setNome(firebaseUser.getDisplayName());

         return usuario;
    }

    public static boolean atualizarFotoUsuario(Uri url) {

        try {

            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d("Perfil", "Erro ao atualizar foto de perfil.");
                    }
                }
            });

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }

    public static boolean atualizarNomeUsuario(String nome) {

        try {

            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d("Perfil", "Erro ao atualizar nome de perfil.");
                    }
                }
            });

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }

    public static Usuario getDadosUsuarioLogado(){

        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());

        if (firebaseUser.getPhotoUrl() == null){

            usuario.setFoto("");

        } else {

            usuario.setFoto(firebaseUser.getPhotoUrl().toString());
        }


        return usuario;
    }

}
