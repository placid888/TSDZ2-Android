package spider65.ebike.tsdz2_esp32.data;

import androidx.annotation.StringRes;
import spider65.ebike.tsdz2_esp32.R;

public enum TelemetryType {
    SPEED(R.string.telemetry_speed, "km/h"),
    CADENCE(R.string.telemetry_cadence, "rpm"),
    MOTOR_POWER(R.string.telemetry_motor_power, "W"),
    PEDAL_POWER(R.string.telemetry_pedal_power, "W"),
    BATTERY_CURRENT(R.string.telemetry_battery_current, "A"),
    BATTERY_VOLTAGE(R.string.telemetry_battery_voltage, "V"),
    MOTOR_TEMP(R.string.telemetry_motor_temp, "°C"),
    PCB_TEMP(R.string.telemetry_pcb_temp, "°C"),
    WATT_HOUR(R.string.telemetry_watt_hour, "Wh"),
    SOC(R.string.telemetry_soc, "%"),
    DUTY_CYCLE(R.string.telemetry_duty_cycle, ""),
    FOC_ANGLE(R.string.telemetry_foc_angle, "°"),
    PEDAL_TORQUE(R.string.telemetry_pedal_torque, "Nm"),
    TORQUE_ADC(R.string.telemetry_torque_adc, ""),
    MOTOR_ERPS(R.string.telemetry_motor_erps, "erps"),
    FW_OFFSET(R.string.telemetry_fw_offset, ""),
    TORQUE_SMOOTH_PCT(R.string.telemetry_torque_smooth_pct, "%");

    @StringRes
    public final int nameResId;
    public final String unit;

    TelemetryType(@StringRes int nameResId, String unit) {
        this.nameResId = nameResId;
        this.unit = unit;
    }
}