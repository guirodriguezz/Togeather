package com.example.guiro.togeather.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    private static FirebaseAuth autenticacao;
    private static DatabaseReference database;
    private static StorageReference storage;

    // Retorna a instancia do Firebasedatabase
    public static DatabaseReference getFirebaseDatabase(){

        if (database == null){

            database = FirebaseDatabase.getInstance().getReference();
        }

        return database;
    }

    // Retorna a instancia do FirebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao() {

        if (autenticacao == null){

            autenticacao = FirebaseAuth.getInstance();

        }

        return autenticacao;
    }

    public static StorageReference getFirebaseStorage(){

        if (storage == null){

            storage = FirebaseStorage.getInstance().getReference();

        }

        return storage;

    }
}
