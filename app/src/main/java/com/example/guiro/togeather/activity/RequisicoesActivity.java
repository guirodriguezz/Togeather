package com.example.guiro.togeather.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.guiro.togeather.R;
import com.example.guiro.togeather.adapter.RequisicoesAdapter;
import com.example.guiro.togeather.config.ConfiguracaoFirebase;
import com.example.guiro.togeather.helper.UsuarioFirebase;
import com.example.guiro.togeather.model.Requisicao;
import com.example.guiro.togeather.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequisicoesActivity extends AppCompatActivity {

    //Componentes
    private RecyclerView recyclerRequisicoes;
    private TextView textResultado;

    private DatabaseReference firebaseRef;
    private List<Requisicao> listaRequisicoes = new ArrayList<>();
    private RequisicoesAdapter adapter;
    private Usuario acompanhante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisicoes);

        //Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        acompanhante = UsuarioFirebase.getDadosUsuarioLogado();


        //Configurar componentes
        recyclerRequisicoes = findViewById(R.id.recyclerRequisicoes);
        textResultado = findViewById(R.id.textResultado);

        //Configurar RecyclerView
        adapter = new RequisicoesAdapter(listaRequisicoes, getApplicationContext(), acompanhante);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerRequisicoes.setLayoutManager(layoutManager);
        recyclerRequisicoes.setHasFixedSize(true);
        recyclerRequisicoes.setAdapter(adapter);

        recuperarRequisicoes();
    }

    private void recuperarRequisicoes(){

        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        Query requisicaoPesquisa = requisicoes.orderByChild("status")
                .equalTo(Requisicao.STATUS_AGUARDANDO);

        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    textResultado.setVisibility(View.GONE);
                    recyclerRequisicoes.setVisibility(View.VISIBLE);
                }else{
                    textResultado.setVisibility(View.VISIBLE);
                    recyclerRequisicoes.setVisibility(View.GONE);
                }

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Requisicao requisicao = ds.getValue(Requisicao.class);
                    listaRequisicoes.add(requisicao);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
