package edu.ub.pis2425.projecte7owls.presentation.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.domain.entities.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private List<Product> productList;
    private OnProductClickListener listener;
    private Set<Product> selectedProducts = new HashSet<>(); // ← NUEVO

    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    public boolean isProductSelected(Product product) {
        return selectedProducts.contains(product);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, listener, selectedProducts.contains(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public void clearSelection() {
        selectedProducts.clear();
        notifyDataSetChanged();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewProduct;
        private TextView textViewName;
        private TextView textViewDescription;
        private TextView textViewPrice;
        private Button buttonAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
        }

        public void bind(final Product product, final OnProductClickListener listener, boolean isSelected) {
            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .into(imageViewProduct);

            textViewName.setText(product.getName());
            textViewDescription.setText(product.getDescription());
            textViewPrice.setText(String.format("%d points", product.getPrice()));

            updateButtonAppearance(isSelected);

            buttonAddToCart.setOnClickListener(v -> {
                boolean added;
                if (selectedProducts.contains(product)) {
                    selectedProducts.remove(product);
                    added = false;
                } else {
                    selectedProducts.add(product);
                    added = true;
                }

                updateButtonAppearance(added);
                listener.onProductClick(product);
            });
        }

        private void updateButtonAppearance(boolean isSelected) {
            if (isSelected) {
                buttonAddToCart.setText("Remove");
                buttonAddToCart.setBackgroundColor(Color.GRAY);
            } else {
                buttonAddToCart.setText("Add to Cart");
                buttonAddToCart.setBackgroundColor(itemView.getResources().getColor(R.color.blue_dark));
            }
        }
    }
}
