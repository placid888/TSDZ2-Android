package spider65.ebike.tsdz2_esp32.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import spider65.ebike.tsdz2_esp32.data.TSDZ_Status;

public class FragmentJkBattery extends Fragment implements MyFragmentListener {

    private TSDZ_Status currentStatus;
    private TextView tvInfo;

    public static FragmentJkBattery newInstance(TSDZ_Status status) {
        FragmentJkBattery fragment = new FragmentJkBattery();
        fragment.currentStatus = status;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 暫時用純文字介面，確保編譯通過，後續我們再擴充成 13S/17S 列表網格
        tvInfo = new TextView(getContext());
        tvInfo.setText("JK BMS 詳細資訊頁面建置中...");
        tvInfo.setTextSize(18);
        tvInfo.setPadding(32, 32, 32, 32);
        return tvInfo;
    }

    @Override
    public void refreshView(TSDZ_Status newStatus) {
        currentStatus = newStatus;
        if (tvInfo != null && currentStatus != null) {
            tvInfo.setText("JK BMS 總電壓: " + currentStatus.jkVoltage + " V\n" +
                         "SOC: " + currentStatus.jkSoc + " %\n" +
                         "電流: " + currentStatus.jkCurrent + " A");
        }
    }
}