package com.pavithran.pricely;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ValuationHistoryAdapter extends RecyclerView.Adapter<ValuationHistoryAdapter.ViewHolder> {

    private final List<ValuationItem> items;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault());

    public ValuationHistoryAdapter(List<ValuationItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_valuation_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ValuationItem item = items.get(position);
        holder.tvLocation.setText(item.getLocation());
        holder.tvPrice.setText(item.getFormattedPrice());

        String details = String.format(Locale.getDefault(),
                "%.0f sq. ft. | %d BHK | %d Bath | %d Balcony | %s | %s",
                item.getTotalSqft(), item.getBhk(), item.getBath(), item.getBalcony(),
                item.getAreaType(), item.getAvailability());
        holder.tvDetails.setText(details);

        String dateStr = dateFormat.format(new Date(item.getTimestamp()));
        holder.tvDate.setText(dateStr);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocation;
        TextView tvPrice;
        TextView tvDetails;
        TextView tvDate;

        ViewHolder(View itemView) {
            super(itemView);
            tvLocation = itemView.findViewById(R.id.tvHistoryLocation);
            tvPrice = itemView.findViewById(R.id.tvHistoryPrice);
            tvDetails = itemView.findViewById(R.id.tvHistoryDetails);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
        }
    }
}
