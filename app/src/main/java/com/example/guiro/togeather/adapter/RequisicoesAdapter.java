package com.example.guiro.togeather.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.guiro.togeather.R;
import com.example.guiro.togeather.model.Requisicao;
import com.example.guiro.togeather.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequisicoesAdapter extends RecyclerView.Adapter<RequisicoesAdapter.MyViewHolder> {

    private List<Requisicao> requisicoes;
    private Context context;
    private Usuario acompanhante;

    public RequisicoesAdapter(List<Requisicao> requisicoes, Context context, Usuario acompanhante) {
        this.requisicoes = requisicoes;
        this.context = context;
        this.acompanhante = acompanhante;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_requisicoes, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Requisicao requisicao = requisicoes.get(position);
        Usuario usuario = requisicao.getUsuario();

        holder.nome.setText(usuario.getNome());
        holder.distancia.setText("1 km- aproximadamente");

        if(usuario.getFoto() != null){

            Uri uri = Uri.parse(usuario.getFoto());
            Glide.with(context).load(uri).into(holder.foto);
        }else {

            holder.foto.setImageResource(R.drawable.padrao);
        }

    }

    @Override
    public int getItemCount() {
        return requisicoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome, distancia;

        public MyViewHolder(View itemView){
            super(itemView);

            nome = itemView.findViewById(R.id.textRequisicaoNome);
            distancia = itemView.findViewById(R.id.textRequisicaoDistancia);
            foto = itemView.findViewById(R.id.imageViewFotoContato);
        }
    }
}
