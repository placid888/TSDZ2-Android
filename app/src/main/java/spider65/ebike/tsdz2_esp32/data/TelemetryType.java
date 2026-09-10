package spider65.ebike.tsdz2_esp32.data;

public enum TelemetryType {
    SPEED("Speed", "km/h"),
    CADENCE("Cadence", "rpm"),
    MOTOR_POWER("Motor Power", "W"),
    PEDAL_POWER("Pedal Power", "W"),
    BATTERY_CURRENT("Battery Curr", "A"),
    BATTERY_VOLTAGE("Battery Volt", "V"),
    MOTOR_TEMP("Motor Temp", "C"),
    PCB_TEMP("PCB Temp", "C"),
    WATT_HOUR("Energy", "Wh"),
    SOC("SOC", "%"),
    DUTY_CYCLE("Duty Cycle", ""),
    FOC_ANGLE("FOC Angle", ""),
    PEDAL_TORQUE("Pedal Torque", "Nm"),
    TORQUE_ADC("Torque ADC", ""),
    MOTOR_ERPS("Motor ERPS", ""),
    FW_OFFSET("FW Offset", ""),
    TORQUE_SMOOTH_PCT("Torque Smooth", "%");

    public final String displayName;
    public final String unit;

    TelemetryType(String displayName, String unit) {
        this.displayName = displayName;
        this.unit = unit;
    }
}