package com.example.guiro.togeather.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.guiro.togeather.config.ConfiguracaoFirebase;
import com.example.guiro.togeather.helper.Permissao;
import com.example.guiro.togeather.helper.UsuarioFirebase;
import com.example.guiro.togeather.model.Destino;
import com.example.guiro.togeather.model.Requisicao;
import com.example.guiro.togeather.model.Usuario;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.guiro.togeather.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Componentes
    private EditText editDestino;
    private LinearLayout linearLayoutDestino;
    private Button buttonChamar;

    private GoogleMap mMap;
    private String[] permissao = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng meuLocal;
    private boolean cancelarChamado = false;
    private DatabaseReference firebaseRef;
    private Requisicao requisicao;
    private FloatingActionButton fabChat;


    Date data = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Encontre uma companhia");
        setSupportActionBar(toolbar);

        MapsInitializer.initialize(this);

        //Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        //Inicializar componentes
        editDestino = findViewById(R.id.editDestino);
        linearLayoutDestino = findViewById(R.id.linearLayoutDestino);
        buttonChamar = findViewById(R.id.buttonChamar);

        //Adicionar listener para status da requisição
        verificaStatusRequisicao();

        //Validar permissões
        Permissao.validarPermissoes(permissao, this, 1);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Adiciona evento de clique no FabChat
        fabChat = findViewById(R.id.fabChat);
        fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MapaActivity.this, ChatActivity.class);
                startActivity(i);
                finish();

            }
        });
    }

    private void verificaStatusRequisicao() {

        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogadoChamado();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        Query requisicaoPesquisa = requisicoes.orderByChild("usuario/email")
                .equalTo(usuarioLogado.getEmail());

        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Requisicao> lista = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    lista.add(ds.getValue(Requisicao.class));

                }

                Collections.reverse(lista);
                if (lista != null && lista.size() > 0) {

                    requisicao = lista.get(0);

                    switch (requisicao.getStatus()) {
                        case Requisicao.STATUS_AGUARDANDO:
                            linearLayoutDestino.setVisibility(View.GONE);
                            buttonChamar.setText("Cancelar Solicitação");
                            buttonChamar.setBackgroundColor(Color.parseColor("#ff0000"));
                            buttonChamar.setTextColor(Color.parseColor("#FFFFFF"));
                            fabChat.setVisibility(View.VISIBLE);
                            cancelarChamado = true;
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void alteraInterfaceStatusRequisicao(String status) {

        cancelarChamado = false;
        switch (status) {
            case Requisicao.STATUS_AGUARDANDO:
                requisicaoAguardando();
                break;
            case Requisicao.STATUS_CANCELADA:
                requisicaoCancelada();
                break;

        }

    }

    private void requisicaoCancelada() {

        linearLayoutDestino.setVisibility(View.VISIBLE);
        buttonChamar.setText("Vamos Juntas");
        buttonChamar.setBackgroundColor(Color.parseColor("#ffa500"));
        buttonChamar.setTextColor(Color.parseColor("#343f4b"));
        fabChat.setVisibility(View.GONE);
        cancelarChamado = false;

    }

    private void requisicaoAguardando() {

        linearLayoutDestino.setVisibility(View.GONE);
        buttonChamar.setText("Cancelar Solicitação");
        cancelarChamado = true;

    }

    private Address recuperarEndereco(String endereco) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> listaEnderecos = geocoder.getFromLocationName(endereco, 1);
            if (listaEnderecos != null && listaEnderecos.size() > 0) {
                Address address = listaEnderecos.get(0);

                return address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void realizarChamado(View view) {

        //false -> Chamado não pode ser cancelado ainda
        //true -> Chamado pode ser cancelado

        if (cancelarChamado) {   //Acompanhante ainda não solicitada

            //Cancelar requisição
            requisicao.setStatus(Requisicao.STATUS_CANCELADA);
            requisicao.atualizarStatus();

            requisicaoCancelada();

        } else {

            String enderecoDestino = editDestino.getText().toString();

            if (!enderecoDestino.equals("") || enderecoDestino != null) {

                Address addressDestino = recuperarEndereco(enderecoDestino);
                if (addressDestino != null) {

                    final Destino destino = new Destino();
                    destino.setCidade(addressDestino.getAdminArea());
                    destino.setCep(addressDestino.getPostalCode());
                    destino.setBairro(addressDestino.getSubLocality());
                    destino.setRua(addressDestino.getThoroughfare());
                    destino.setNumero(addressDestino.getFeatureName());
                    destino.setLatitude(String.valueOf(addressDestino.getLatitude()));
                    destino.setLongitude(String.valueOf(addressDestino.getLongitude()));

                    StringBuilder mensagem = new StringBuilder();
                    mensagem.append("Cidade: " + destino.getCidade());
                    mensagem.append("\nRua: " + destino.getRua());
                    mensagem.append("\nBairro: " + destino.getBairro());
                    mensagem.append("\nNúmero: " + destino.getNumero());
                    mensagem.append("\nCep: " + destino.getCep());

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("O destino está correto?");
                    builder.setMessage(mensagem);
                    builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            salvarRequisicao(destino);
                            //cancelarChamado = true;

                        }
                    });
                    builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }

            } else {
                Toast.makeText(this, "Informe o endereço de destino!",
                        Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void salvarRequisicao(Destino destino) {

        Requisicao requisicao = new Requisicao();
        requisicao.setDestino(destino);

        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioLogado.setLatitude(String.valueOf(meuLocal.latitude));
        usuarioLogado.setLongitude(String.valueOf(meuLocal.longitude));

        requisicao.setUsuario(usuarioLogado);
        requisicao.setStatus(Requisicao.STATUS_AGUARDANDO);
        requisicao.salvar();

        linearLayoutDestino.setVisibility(View.GONE);
        buttonChamar.setText("Cancelar Solicitação");
        buttonChamar.setBackgroundColor(Color.parseColor("#ff0000"));
        buttonChamar.setTextColor(Color.parseColor("#FFFFFF"));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        recuperarLocalizacaoUsuario();
    }

    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                meuLocal = new LatLng(latitude, longitude);

                mMap.clear();
                mMap.addMarker(
                        new MarkerOptions()
                                .position(meuLocal)
                                .title(UsuarioFirebase.getUsuarioAtual().getDisplayName())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.girl))
                );
                mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(meuLocal, 18)
                );

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //Solicitar atualizações de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    100,
                    10,
                    locationListener
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {

            //permission denied (negada)
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                //Alerta
                alertaValidacaoPermissao();
            } else if (permissaoResultado == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            100,
                            10,
                            locationListener
                    );
                }
            }
        }
    }

    private void alertaValidacaoPermissao() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(MapaActivity.this, PrincipalActivity.class);
        finish();
        startActivity(i);

    }
}
