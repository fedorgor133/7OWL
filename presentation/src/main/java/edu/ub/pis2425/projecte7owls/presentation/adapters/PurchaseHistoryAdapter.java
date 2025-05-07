package edu.ub.pis2425.projecte7owls.presentation.adapters;

import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.domain.entities.Compra;

public class PurchaseHistoryAdapter extends RecyclerView.Adapter<PurchaseHistoryAdapter.ViewHolder> {

    private List<Compra> compras;

    public PurchaseHistoryAdapter(List<Compra> compras) {
        this.compras = compras;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre, textPrecio, textFecha;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textNombre = itemView.findViewById(R.id.textNombre);
            textPrecio = itemView.findViewById(R.id.textPrecio);
            textFecha = itemView.findViewById(R.id.textFecha);
            imageView = itemView.findViewById(R.id.imageViewProducto);
        }
    }

    @NonNull
    @Override
    public PurchaseHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial_compra, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseHistoryAdapter.ViewHolder holder, int position) {
        Compra compra = compras.get(position);
        holder.textNombre.setText(compra.getNombre());
        holder.textPrecio.setText(compra.getPrecio() + " points");
        holder.textFecha.setText("Fecha: " + compra.getFechaCompra());

        Glide.with(holder.itemView.getContext())
                .load(compra.getImagen())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return compras.size();
    }
}

