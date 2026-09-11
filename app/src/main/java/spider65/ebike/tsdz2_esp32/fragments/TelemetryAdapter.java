package spider65.ebike.tsdz2_esp32.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

import spider65.ebike.tsdz2_esp32.R;
import spider65.ebike.tsdz2_esp32.data.TSDZ_Status;
import spider65.ebike.tsdz2_esp32.data.TelemetryType;

public class TelemetryAdapter extends RecyclerView.Adapter<TelemetryAdapter.ViewHolder> {

    private final List<TelemetryType> items;
    private TSDZ_Status currentStatus;
    private OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public TelemetryAdapter(List<TelemetryType> items, TSDZ_Status initialStatus) {
        this.items = items;
        this.currentStatus = initialStatus;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    // === 核心修改 1：捨棄暴力的 notifyDataSetChanged，改用帶 Payload 的局部更新 ===
    public void updateStatus(TSDZ_Status newStatus) {
        this.currentStatus = newStatus;
        if (items != null && !items.isEmpty()) {
            // 傳遞 newStatus 作為 payload，告訴系統「只要更新數值」
            notifyItemRangeChanged(0, items.size(), newStatus); 
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_telemetry, parent, false);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        view.post(() -> {
            layoutParams.height = parent.getHeight() / 5;
            view.setLayoutParams(layoutParams);
        });

        return new ViewHolder(view);
    }

    // === 核心修改 2：新增處理 Payload 的 onBindViewHolder，只更新 TextView 的數字 ===
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.get(0) instanceof TSDZ_Status) {
            // 如果收到了局部更新要求，我們就只更新數值 (tvValue)，絕對不去動標題和排版
            TelemetryType type = items.get(position);
            TSDZ_Status updatedStatus = (TSDZ_Status) payloads.get(0);
            holder.tvValue.setText(formatValue(type, updatedStatus));
        } else {
            // 如果是第一次建立卡片，才去執行完整的 UI 綁定
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    // 這是原本完整綁定 UI 的函數，保持不變，只在卡片第一次出現或切換模式時呼叫
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TelemetryType type = items.get(position);
        
        // 透過 Resource ID 自動顯示對應語系的字串
        holder.tvTitle.setText(type.nameResId);

        if (currentStatus != null) {
            holder.tvValue.setText(formatValue(type, currentStatus));
        } else {
            holder.tvValue.setText("--");
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    longClickListener.onItemLongClick(adapterPos);
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    private String formatValue(TelemetryType type, TSDZ_Status status) {
        switch (type) {
            case SPEED: return String.format(Locale.getDefault(), "%.1f %s", status.speed, type.unit);
            case CADENCE: return String.format(Locale.getDefault(), "%d %s", status.cadence, type.unit);
            case MOTOR_POWER: return String.format(Locale.getDefault(), "%d %s", (int)(status.volts * status.amperes), type.unit);
            case PEDAL_POWER: return String.format(Locale.getDefault(), "%d %s", status.pPower, type.unit);
            case BATTERY_CURRENT: return String.format(Locale.getDefault(), "%.1f %s", status.amperes, type.unit);
            case BATTERY_VOLTAGE: return String.format(Locale.getDefault(), "%.1f %s", status.volts, type.unit);
            case MOTOR_TEMP: return String.format(Locale.getDefault(), "%.1f %s", status.motorTemperature, type.unit);
            case PCB_TEMP: return String.format(Locale.getDefault(), "%.1f %s", status.pcbTemperature, type.unit);
            case WATT_HOUR: return String.format(Locale.getDefault(), "%d %s", status.wattHour, type.unit);
            case SOC: return String.format(Locale.getDefault(), "%d %s", status.soc, type.unit);
            case DUTY_CYCLE: return String.valueOf(status.dutyCycle);
            case FOC_ANGLE: return String.valueOf(status.focAngle);
            case PEDAL_TORQUE: return String.format(Locale.getDefault(), "%.1f %s", status.pTorque, type.unit);
            case TORQUE_ADC: return String.valueOf(status.torqueADCValue);
            case MOTOR_ERPS: return String.valueOf(status.motorERPS);
            case FW_OFFSET: return String.valueOf(status.fwOffset);
            case TORQUE_SMOOTH_PCT: return String.format(Locale.getDefault(), "%d %s", status.torqueSmoothPct, type.unit);
            default: return "--";
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvValue = itemView.findViewById(R.id.tv_value);
        }
    }
}