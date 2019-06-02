package com.example.guiro.togeather.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.guiro.togeather.R;
import com.example.guiro.togeather.adapter.ContatosAdapter;
import com.example.guiro.togeather.adapter.RequisicoesAdapter;
import com.example.guiro.togeather.config.ConfiguracaoFirebase;
import com.example.guiro.togeather.helper.RecyclerItemClickListener;
import com.example.guiro.togeather.helper.UsuarioFirebase;
import com.example.guiro.togeather.model.Requisicao;
import com.example.guiro.togeather.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    //Componentes
    private RecyclerView recyclerViewListaContatos;
    private TextView textResultado;

    private DatabaseReference usuariosRef;
    private DatabaseReference firebaseRef;
    private ArrayList<Usuario> listaContatos = new ArrayList<>();
    private List<Requisicao> listaRequisicoes = new ArrayList<>();
    private ContatosAdapter adapter;
    private RequisicoesAdapter adapter1;

    private ValueEventListener valueEventListenerContatos;
    private FirebaseUser usuarioAtual;
    private Usuario acompanhante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Configurações iniciais
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();
        acompanhante = UsuarioFirebase.getDadosUsuarioLogado();

        //Configurar componentes
        recyclerViewListaContatos = findViewById(R.id.recyclerViewListaContatos);
        textResultado = findViewById(R.id.textResultado);

        // Configurar adapter
        adapter = new ContatosAdapter(listaContatos, getApplicationContext());
        adapter1 = new RequisicoesAdapter(listaRequisicoes, getApplicationContext(), acompanhante);

        // Configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewListaContatos.setLayoutManager(layoutManager);
        recyclerViewListaContatos.setHasFixedSize(true);
        recyclerViewListaContatos.setAdapter(adapter1);

        // Configurar evento de clique no recyclerview
        recyclerViewListaContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewListaContatos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Usuario usuarioSelecionado = listaContatos.get(position);
                                Intent i = new Intent(ChatActivity.this, ChatConversaActivity.class);
                                i.putExtra("chatContato", usuarioSelecionado);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        //recuperarRequisicoes();
        recuperarContatos();

    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarRequisicoes();

    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerContatos);
    }

    public void recuperarContatos() {

        listaContatos.clear();

        valueEventListenerContatos = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dados : dataSnapshot.getChildren()) {

                    Usuario usuario = dados.getValue(Usuario.class);

                    String emailUsuarioAtual = usuarioAtual.getEmail();
                    if (!emailUsuarioAtual.equals(usuario.getEmail())) {

                        listaContatos.add(usuario);

                    }
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                    recyclerViewListaContatos.setVisibility(View.VISIBLE);
                }else{
                    textResultado.setVisibility(View.VISIBLE);
                    recyclerViewListaContatos.setVisibility(View.GONE);
                }

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Requisicao requisicao = ds.getValue(Requisicao.class);
                    listaRequisicoes.add(requisicao);
                }

                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent i=new Intent(ChatActivity.this, PrincipalActivity.class);
        finish();
        startActivity(i);

    }

}
