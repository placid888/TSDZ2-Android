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
    
    // === 新增：用來記住目前的 RecyclerView 實體 ===
    private RecyclerView mRecyclerView;

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

    // === 攔截 RecyclerView 的綁定狀態 ===
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    // === 終極解法：繞過 RecyclerView 的更新機制，直接替換文字 ===
    public void updateStatus(TSDZ_Status newStatus) {
        this.currentStatus = newStatus;
        
        if (mRecyclerView != null && items != null) {
            for (int i = 0; i < items.size(); i++) {
                // 直接去畫面上抓那張卡片
                ViewHolder holder = (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    // 只偷偷把數字換掉，完全不觸發版面重繪與動畫！
                    holder.tvValue.setText(formatValue(items.get(i), newStatus));
                }
            }
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TelemetryType type = items.get(position);
        
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