package com.bhjbestkalyangame.realapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;
import com.bhjbestkalyangame.realapplication.Listener.IRecyclerClickListener;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder>{

    AppCompatActivity appCompatActivity;
    List<SkuDetails> skuDetailsList;
    BillingClient billingClient;
    String type;

    public ProductAdapter(AppCompatActivity appCompatActivity, List<SkuDetails> skuDetailsList, BillingClient billingClient, String type) {
        this.appCompatActivity = appCompatActivity;
        this.skuDetailsList = skuDetailsList;
        this.billingClient = billingClient;
        this.type = type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(type.equals("product")){
        return new MyViewHolder(LayoutInflater.from(appCompatActivity.getBaseContext())
                .inflate(R.layout.layout_product_display_for_adapter, parent, false));
        }else{
            return new MyViewHolder(LayoutInflater.from(appCompatActivity.getBaseContext())
                    .inflate(R.layout.layout_subscription_display_for_adapter, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.Title.setText(skuDetailsList.get(position).getTitle());
        holder.Price.setText(skuDetailsList.get(position).getPrice());
        holder.Description.setText(skuDetailsList.get(position).getDescription());

        holder.setmListener(new IRecyclerClickListener() {
            @Override
            public void onClick(View view, int position) {
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList.get(position))
                        .build();

                int response = billingClient.launchBillingFlow(appCompatActivity, billingFlowParams)
                        .getResponseCode();

                switch (response){
                    case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                        Toast.makeText(appCompatActivity, "Billing Unavailable", Toast.LENGTH_SHORT).show();
                        break;

                    case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                        Toast.makeText(appCompatActivity, "Developers Error", Toast.LENGTH_SHORT).show();
                        break;

                    case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                        Toast.makeText(appCompatActivity, "Feature Not Supported", Toast.LENGTH_SHORT).show();
                        break;

                    case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                        Toast.makeText(appCompatActivity, "Items Already Owned", Toast.LENGTH_SHORT).show();
                        break;

                    case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                        Toast.makeText(appCompatActivity, "Service Disconnected", Toast.LENGTH_SHORT).show();
                        break;

                    case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                        Toast.makeText(appCompatActivity, "Service Timeout", Toast.LENGTH_SHORT).show();
                        break;

                    case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                        Toast.makeText(appCompatActivity, "Item Unavailable", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return skuDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Title, Price, Description;

        IRecyclerClickListener mListener;

        public void setmListener(IRecyclerClickListener mListener) {
            this.mListener = mListener;
        }

        public MyViewHolder(@NonNull View itemView, IRecyclerClickListener mListener) {
            super(itemView);
            this.mListener = mListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.title);
            Price = itemView.findViewById(R.id.price);
            Description = itemView.findViewById(R.id.description);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }


}
