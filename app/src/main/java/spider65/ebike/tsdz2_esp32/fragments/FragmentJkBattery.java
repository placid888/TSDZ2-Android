package spider65.ebike.tsdz2_esp32.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.widget.ScrollView;
import android.widget.LinearLayout;

import java.util.Locale;

import spider65.ebike.tsdz2_esp32.data.TSDZ_Status;

public class FragmentJkBattery extends Fragment implements MyFragmentListener {

    private TSDZ_Status currentStatus;
    private TextView tvSummary;
    private GridLayout cellVoltageLayout;
    private GridLayout cellResistanceLayout;

    public static FragmentJkBattery newInstance(TSDZ_Status status) {
        FragmentJkBattery fragment = new FragmentJkBattery();
        fragment.currentStatus = status;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout rootLayout = new LinearLayout(getContext());
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(24, 24, 24, 24);

        // 1. 上方完整總覽資訊 (改為深色字體，避免在淺色背景下消失)
        tvSummary = new TextView(getContext());
        tvSummary.setTextSize(15);
        tvSummary.setTextColor(Color.parseColor("#333333"));
        tvSummary.setPadding(8, 8, 8, 20);
        rootLayout.addView(tvSummary);

        // 標題：單體電壓
        TextView tvTitleVolt = new TextView(getContext());
        tvTitleVolt.setText("【單體電壓 (V)】");
        tvTitleVolt.setTextColor(Color.parseColor("#2E7D32"));
        tvTitleVolt.setTextSize(16);
        tvTitleVolt.setPadding(0, 16, 0, 8);
        rootLayout.addView(tvTitleVolt);

        // 2. 中間：單體電壓動態網格 (兩欄)
        cellVoltageLayout = new GridLayout(getContext());
        cellVoltageLayout.setColumnCount(2);
        rootLayout.addView(cellVoltageLayout);

        // 標題：均衡線電阻
        TextView tvTitleRes = new TextView(getContext());
        tvTitleRes.setText("【均衡線電阻 (Ω)】");
        tvTitleRes.setTextColor(Color.parseColor("#2E7D32"));
        tvTitleRes.setTextSize(16);
        tvTitleRes.setPadding(0, 24, 0, 8);
        rootLayout.addView(tvTitleRes);

        // 3. 下方：均衡線電阻動態網格 (兩欄)
        cellResistanceLayout = new GridLayout(getContext());
        cellResistanceLayout.setColumnCount(2);
        rootLayout.addView(cellResistanceLayout);

        scrollView.addView(rootLayout);
        updateUI();
        return scrollView;
    }

    @Override
    public void refreshView(TSDZ_Status newStatus) {
        currentStatus = newStatus;
        updateUI();
    }

    private void updateUI() {
        if (currentStatus == null) return;

        // 更新上方豐富的總覽數據
        String summary = String.format(Locale.getDefault(),
                "電池功率: %.1f W   |   單體平均: %.3f V\n" +
                "電池容量: %.1f Ah   |   最大壓差: %d mV\n" +
                "剩餘容量: %.1f Ah   |   均衡電流: %.3f A\n" +
                "功率溫度: %.1f °C   |   循環次數: %d 次\n" +
                "電池溫1: %.1f °C   |   循環容量: %.1f Ah\n" +
                "電池溫2: %.1f °C   |   均衡狀態: %s\n" +
                "電池總壓: %.2f V   |   電池電流: %.2f A\n" +
                "有效串數: %d 串",
                currentStatus.jkPower, currentStatus.jkCellAvgMv / 1000.0f,
                currentStatus.jkTotalCap, currentStatus.jkDeltaMv / 1000.0f,
                currentStatus.jkRemainCap, currentStatus.jkBalCurrent,
                currentStatus.jkTempFet, currentStatus.jkCycleCount,
                currentStatus.jkTempBat, currentStatus.jkCycleCap,
                currentStatus.jkTempBat2, currentStatus.jkBalancingOn ? "開啟" : "關閉",
                currentStatus.jkVoltage, currentStatus.jkCurrent,
                currentStatus.activeCellCount
        );
        if (tvSummary != null) {
            tvSummary.setText(summary);
        }

        int count = currentStatus.activeCellCount;
        if (count <= 0 || count > 17) count = 13;

        // 渲染單體電壓格子
        if (cellVoltageLayout != null && getContext() != null) {
            cellVoltageLayout.removeAllViews();
            for (int i = 0; i < count; i++) {
                TextView cellView = new TextView(getContext());
                int voltageMv = currentStatus.cellVoltages != null ? currentStatus.cellVoltages[i] : 0;
                
                cellView.setText(String.format(Locale.getDefault(), "Cell %02d\n%.3f V", (i + 1), voltageMv / 1000.0f));
                cellView.setTextSize(13);
                cellView.setTextColor(Color.parseColor("#222222"));
                cellView.setBackgroundColor(Color.parseColor("#E0E0E0"));
                cellView.setPadding(16, 16, 16, 16);
                
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(i % 2, 1f);
                params.setMargins(6, 6, 6, 6);
                cellView.setLayoutParams(params);

                cellVoltageLayout.addView(cellView);
            }
        }

        // 渲染均衡線電阻格子
        if (cellResistanceLayout != null && getContext() != null) {
            cellResistanceLayout.removeAllViews();
            for (int i = 0; i < count; i++) {
                TextView resView = new TextView(getContext());
                float resistance = (currentStatus.cellResistances != null && i < currentStatus.cellResistances.length) 
                        ? currentStatus.cellResistances[i] : 0.0f;
                
                resView.setText(String.format(Locale.getDefault(), "Cell %02d\n%.3f Ω", (i + 1), resistance));
                resView.setTextSize(13);
                resView.setTextColor(Color.parseColor("#222222"));
                resView.setBackgroundColor(Color.parseColor("#E0E0E0"));
                resView.setPadding(16, 16, 16, 16);
                
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(i % 2, 1f);
                params.setMargins(6, 6, 6, 6);
                resView.setLayoutParams(params);

                cellResistanceLayout.addView(resView);
            }
        }
    }
}