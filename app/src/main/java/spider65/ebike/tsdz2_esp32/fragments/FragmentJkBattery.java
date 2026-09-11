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
    private GridLayout cellGridLayout;

    public static FragmentJkBattery newInstance(TSDZ_Status status) {
        FragmentJkBattery fragment = new FragmentJkBattery();
        fragment.currentStatus = status;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 建立主 ScrollView 確保資料多時可以上下捲動
        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout rootLayout = new LinearLayout(getContext());
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(24, 24, 24, 24);

        // 1. 上方總覽資訊文字框
        tvSummary = new TextView(getContext());
        tvSummary.setTextSize(16);
        tvSummary.setTextColor(Color.WHITE);
        tvSummary.setPadding(16, 16, 16, 24);
        rootLayout.addView(tvSummary);

        // 2. 下方各串電壓動態網格 (每行排 2 個或 3 個)
        cellGridLayout = new GridLayout(getContext());
        cellGridLayout.setColumnCount(2); // 兩欄式排列
        rootLayout.addView(cellGridLayout);

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

        // 更新上方總覽
        String summary = String.format(Locale.getDefault(),
                "【JK BMS 電池健康總覽】\n" +
                "總電壓: %.2f V   |   總電流: %.2f A\n" +
                "剩餘電量 (SOC): %d %%\n" +
                "最高單串: %d mV   |   最低單串: %d mV\n" +
                "最大壓差 (Delta): %d mV\n" +
                "MOS 溫度: %.1f °C   |   電池溫: %.1f °C\n" +
                "有效串數: %d 串",
                currentStatus.jkVoltage, currentStatus.jkCurrent,
                currentStatus.jkSoc,
                currentStatus.jkCellMaxMv, currentStatus.jkCellMinMv,
                currentStatus.jkCellMaxMv - currentStatus.jkCellMinMv,
                currentStatus.jkTempFet, currentStatus.jkTempBat,
                currentStatus.activeCellCount
        );
        if (tvSummary != null) {
            tvSummary.setText(summary);
        }

        // 動態生成單體電壓格子
        if (cellGridLayout != null && getContext() != null) {
            cellGridLayout.removeAllViews();
            int count = currentStatus.activeCellCount;
            if (count <= 0 || count > 17) count = 13; // 預設防護

            for (int i = 0; i < count; i++) {
                TextView cellView = new TextView(getContext());
                int voltageMv = currentStatus.cellVoltages != null ? currentStatus.cellVoltages[i] : 0;
                
                cellView.setText(String.format(Locale.getDefault(), "Cell %02d\n%.3f V", (i + 1), voltageMv / 1000.0f));
                cellView.setTextSize(14);
                cellView.setTextColor(Color.BLACK);
                cellView.setBackgroundColor(Color.parseColor("#E0E0E0"));
                cellView.setPadding(20, 20, 20, 20);
                
                // 設定格子的寬高與邊距
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(i % 2, 1f); // 均分兩欄
                params.setMargins(8, 8, 8, 8);
                cellView.setLayoutParams(params);

                cellGridLayout.addView(cellView);
            }
        }
    }
}