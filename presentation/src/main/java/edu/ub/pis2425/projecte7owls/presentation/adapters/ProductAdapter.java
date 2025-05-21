package edu.ub.pis2425.projecte7owls.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ub.pis2425.projecte7owls.R;
import edu.ub.pis2425.projecte7owls.domain.entities.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product, int quantity);
    }

    private List<Product> productList;
    private OnProductClickListener listener;
    private Map<Product, Integer> selectedProducts = new HashMap<>();

    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    public Map<Product, Integer> getSelectedProducts() {
        return selectedProducts;
    }

    public void clearSelection() {
        selectedProducts.clear();
        notifyDataSetChanged();
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
        int quantity = selectedProducts.getOrDefault(product, 0);
        holder.bind(product, quantity);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewProduct;
        private TextView textViewName;
        private TextView textViewDescription;
        private TextView textViewPrice;
        private Button buttonIncrease;
        private Button buttonDecrease;
        private TextView textQuantity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
            textQuantity = itemView.findViewById(R.id.textQuantity);
        }

        public void bind(final Product product, int quantity) {
            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .into(imageViewProduct);

            textViewName.setText(product.getName());
            textViewDescription.setText(product.getDescription());
            textViewPrice.setText(String.format("%d points", product.getPrice()));
            textQuantity.setText(String.valueOf(quantity));
            buttonDecrease.setEnabled(quantity > 0);

            buttonIncrease.setOnClickListener(v -> {
                int newQuantity = selectedProducts.getOrDefault(product, 0) + 1;
                selectedProducts.put(product, newQuantity);
                textQuantity.setText(String.valueOf(newQuantity));
                buttonDecrease.setEnabled(true);
                listener.onProductClick(product, newQuantity);
            });

            buttonDecrease.setOnClickListener(v -> {
                int currentQuantity = selectedProducts.getOrDefault(product, 0);
                if (currentQuantity > 0) {
                    int newQuantity = currentQuantity - 1;
                    if (newQuantity == 0) {
                        selectedProducts.remove(product);
                    } else {
                        selectedProducts.put(product, newQuantity);
                    }
                    textQuantity.setText(String.valueOf(newQuantity));
                    buttonDecrease.setEnabled(newQuantity > 0);
                    listener.onProductClick(product, newQuantity);
                }
            });
        }
    }
}
