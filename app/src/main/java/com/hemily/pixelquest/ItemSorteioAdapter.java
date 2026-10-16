package com.hemily.pixelquest;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hemily.pixelquest.databinding.ItemSorteioBinding;

import java.util.List;

// O Adapter fornece as views dos itens; o ViewHolder as guarda para reutilização
public class ItemSorteioAdapter extends RecyclerView.Adapter<ItemSorteioAdapter.ItemViewHolder> {

    public interface OnItemLongClickListener {
        void onItemLongClick(int posicao);
    }

    private final List<ItemSorteio> itens;
    private final OnItemLongClickListener listener;

    public ItemSorteioAdapter(List<ItemSorteio> itens, OnItemLongClickListener listener) {
        this.itens = itens;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSorteioBinding itemBinding = ItemSorteioBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemSorteio item = itens.get(position);
        holder.binding.tvPalavra.setText(item.getPalavra());
        holder.binding.tvCategoria.setText(item.getCategoria());

        // Clique longo: avisa a Activity qual posição excluir
        holder.itemView.setOnLongClickListener(v -> {
            int posicaoAtual = holder.getBindingAdapterPosition();
            if (posicaoAtual != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(posicaoAtual);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final ItemSorteioBinding binding;

        ItemViewHolder(ItemSorteioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}