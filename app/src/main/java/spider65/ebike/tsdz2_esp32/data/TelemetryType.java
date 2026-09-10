package spider65.ebike.tsdz2_esp32.data;

public enum TelemetryType {
    SPEED("速度", "km/h"),
    CADENCE("踏頻", "rpm"),
    MOTOR_POWER("馬達功率", "W"),
    PEDAL_POWER("踏板功率", "W"),
    BATTERY_CURRENT("電池電流", "A"),
    BATTERY_VOLTAGE("電池電壓", "V"),
    MOTOR_TEMP("馬達溫度", "°C"),
    PCB_TEMP("控制器溫度", "°C"),
    WATT_HOUR("消耗能量", "Wh"),
    SOC("SOC (%)", "%"),
    DUTY_CYCLE("佔空比", ""),
    FOC_ANGLE("FOC 角度", "°"),
    PEDAL_TORQUE("踏板扭力", "Nm"),
    TORQUE_ADC("扭力 ADC", ""),
    MOTOR_ERPS("馬達電氣轉速", "erps"),
    FW_OFFSET("弱磁偏移量", ""),
    TORQUE_SMOOTH_PCT("扭力平滑度", "%");

    public final String displayName;
    public final String unit;

    TelemetryType(String displayName, String unit) {
        this.displayName = displayName;
        this.unit = unit;
    }
}