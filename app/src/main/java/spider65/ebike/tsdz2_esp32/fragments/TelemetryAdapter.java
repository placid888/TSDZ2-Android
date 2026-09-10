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

    public void updateStatus(TSDZ_Status newStatus) {
        this.currentStatus = newStatus;
        notifyDataSetChanged();
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
        holder.tvTitle.setText(type.displayName);

        if (currentStatus != null) {
            holder.tvValue.setText(formatValue(type, currentStatus));
        } else {
            holder.tvValue.setText("--");
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(position);
                return true;
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

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvValue = itemView.findViewById(R.id.tv_value);
        }
    }
}