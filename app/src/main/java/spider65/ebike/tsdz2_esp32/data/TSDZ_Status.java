package spider65.ebike.tsdz2_esp32.data;

import android.util.Log;

import static spider65.ebike.tsdz2_esp32.TSDZConst.STATUS_ADV_SIZE;

public class TSDZ_Status {
    private static final String TAG = "TSDZ_Status";

    public RidingMode ridingMode;
    public boolean streetMode;
    public short assistLevel;
    public float speed;
    public short cadence;
    public float motorTemperature;
     // === 極空 BMS 變數 ===
         // === 極空 BMS 完整詳細數據 ===
    public float jkVoltage = 0;          // 總電壓
    public float jkCurrent = 0;          // 總電流
    public int jkSoc = 0;                // 剩餘電量 %
    public float jkTempFet = 0;          // MOS 溫度
    public float jkTempBat = 0;          // 電池溫度
    public int jkCellMaxMv = 0;          // 最高單串電壓 (mV)
    public int jkCellMinMv = 0;          // 最低單串電壓 (mV)
    public int jkDeltaMv = 0;            // 最大壓差 (mV)
    public int activeCellCount = 13;     // 目前有效串數 (預設 13，可隨 BMS 狀態動態調整)
    public int[] cellVoltages = new int[17]; // 支援最高 17 串單體電壓 (單位: mV)
    public float jkVoltage = 0;          // BMS 總電壓
    public float jkCurrent = 0;          // BMS 總電流
    public int jkSoc = 0;                // 剩餘電量 %
    public float jkTempFet = 0;          // 功率管溫度
    public float jkTempBat = 0;          // 電池組溫度
    public int jkCellMaxMv = 0;          // 最高單體電壓 (mV)
    public int jkCellMinMv = 0;          // 最低單體電壓 (mV)
    public boolean jkConnected = false; 
    public int pPower;
    public float volts;
    public float amperes;
    public short status;
    public boolean brake;
    public boolean controllerFromESP32ReceiveError;
    public boolean esp32FromControllerReceiveError;
    public boolean esp32FromLDCReceiveError;
    public int wattHour;
    public short rxcErrors;
    public short rxlErrors;
    public short torqueSmoothPct;
    public short torqueSmoothAvg;
    public short torqueSmoothMin;
    public short torqueSmoothMax;
    public short dutyCycle;
    public int motorERPS;
    public short focAngle;
    public int torqueADCValue;
    public short adcThrottle;
    public short throttle;
    public float pTorque;
    public short fwOffset;
    public float pcbTemperature;
    public boolean timeDebug;
    public boolean hallDebug;
    public short debug1;
    public short debug2;
    public short debug3;
    public short debug4;
    public short debug5;
    public short debug6;
    public short soc;

    public enum RidingMode {
        OFF_MODE(0),
        POWER_ASSIST_MODE(1),
        TORQUE_ASSIST_MODE(2),
        CADENCE_ASSIST_MODE(3),
        eMTB_ASSIST_MODE(4),
        WALK_ASSIST_MODE(5),
        CRUISE_MODE(6);

        public final int value;

        RidingMode(int value) {
            this.value = value;
        }

        public static RidingMode valueOf(int val) {
            for (RidingMode e : values())
                if (e.value == val) return e;
            return null;
        }
    }

    public boolean setData(byte[] data) {
        if (data.length != STATUS_ADV_SIZE) {
            Log.e(TAG, "Wrong Status BT message size!");
            return false;
        }

        ridingMode = RidingMode.valueOf(data[0] & 0x7f);
        streetMode = (data[0] & 0x80) != 0;
        assistLevel = (short)(data[1] & 255);
        status = (short)(data[2] & 0x0f);
        brake = (data[2] & 0x20) != 0;
        controllerFromESP32ReceiveError = (data[2] & 0x10) != 0;
        esp32FromControllerReceiveError = (data[2] & 0x80) != 0;
        esp32FromLDCReceiveError = (data[2] & 0x40) != 0;

        speed = (float)(((data[4] & 255) << 8) + (data[3] & 255)) / 10;
        cadence = (short)(data[5] & 255);

        long l = ((data[7] & 255) << 8) + (data[6] & 255);
        pTorque = (float)l / 100;
        pPower = (int)((l * cadence / 96) + 5) / 10;

        short t = (short)(((data[9] & 0xff) << 8) | (data[8] & 0xff));
        motorTemperature = (float)(t) / 10;
        t = (short)(((data[11] & 0xff) << 8) | (data[10] & 0xff));
        pcbTemperature = (float)(t) / 10;

        volts = (float)(((data[13] & 255) << 8) + (data[12] & 255)) / 1000;
        amperes = (float)(data[14] & 255) / 10;
        wattHour = ((data[16] & 255) << 8) + ((data[15] & 255));
        adcThrottle = (short)(data[17] & 255);
        throttle = (short)(data[18] & 255);
        torqueADCValue = ((data[20] & 255) << 8) + (data[19] & 255);
        dutyCycle = (short)(data[21] & 255);
        motorERPS = ((data[23] & 255) << 8) + (data[22] & 255);
        focAngle = (short)(data[24] & 255);
        fwOffset = (short)(data[25] & 255);
        torqueSmoothPct = (short)(data[26] & 255);
        torqueSmoothAvg = (short)(data[27] & 255);
        torqueSmoothMin = (short)(data[28] & 255);
        torqueSmoothMax = (short)(data[29] & 255);
        rxcErrors = (short)(data[30] & 255);
        rxlErrors = (short)(data[31] & 255);
        timeDebug = (data[32] & 255) == 0x20;
        hallDebug = (data[32] & 255) == 0x40;
        debug1 = (short)(data[33] & 255);
        debug2 = (short)(data[34] & 255);
        debug3 = (short)(data[35] & 255);
        debug4 = (short)(data[36] & 255);
        debug5 = (short)(data[37] & 255);
        debug6 = (short)(data[38] & 255);
        soc = (short)(data[38] & 255); 
        
        return true;
    }
}