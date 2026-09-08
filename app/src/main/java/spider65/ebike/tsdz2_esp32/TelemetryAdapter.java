package spider65.ebike.tsdz2_esp32.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import spider65.ebike.tsdz2_esp32.R;
import spider65.ebike.tsdz2_esp32.data.TelemetryType;

public class TelemetryAdapter extends RecyclerView.Adapter<TelemetryAdapter.ViewHolder> {

    private List<TelemetryType> items;

    public TelemetryAdapter(List<TelemetryType> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_telemetry, parent, false);
        
        // 動態設定高度為父元件的五分之一，配合 5 列排版
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = parent.getHeight() / 5;
        view.setLayoutParams(layoutParams);
        
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TelemetryType type = items.get(position);
        holder.tvTitle.setText(type.displayName);
        // 預設佔位符，後續步驟加入即時資料綁定邏輯
        holder.tvValue.setText("--");
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvValue;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvValue = itemView.findViewById(R.id.tv_value);
        }
    }
}