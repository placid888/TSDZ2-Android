package spider65.ebike.tsdz2_esp32.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import spider65.ebike.tsdz2_esp32.MyApp;
import spider65.ebike.tsdz2_esp32.R;
import spider65.ebike.tsdz2_esp32.data.TSDZ_Status;
import spider65.ebike.tsdz2_esp32.data.TelemetryType;

public class FragmentStatus extends Fragment implements MyFragmentListener {

    private TelemetryAdapter adapter;
    private TSDZ_Status currentStatus;
    private final List<TelemetryType> gridConfig = new ArrayList<>();

    // 紀錄目前的頁碼 (0 為第一頁, 1 為第二頁)
    private int pageIndex = 0;

    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 250;

    // === 修改：新增 pageIndex 參數，讓 Fragment 知道自己是哪一頁 ===
    public static FragmentStatus newInstance(TSDZ_Status status, int pageIndex) {
        FragmentStatus fragment = new FragmentStatus();
        fragment.currentStatus = status;
        Bundle args = new Bundle();
        args.putInt("PAGE_INDEX", pageIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        
        // 讀取傳遞進來的頁碼
        if (getArguments() != null) {
            pageIndex = getArguments().getInt("PAGE_INDEX", 0);
        }
        
        loadGridConfig();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setItemAnimator(null);

        adapter = new TelemetryAdapter(gridConfig, currentStatus);
        adapter.setOnItemLongClickListener(position -> showSelectionDialog(position));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void refreshView(TSDZ_Status newStatus) {
        currentStatus = newStatus;
        if (adapter != null && isVisible()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime > UPDATE_INTERVAL) {
                adapter.updateStatus(newStatus);
                lastUpdateTime = currentTime;
            }
        }
    }

    private void loadGridConfig() {
        SharedPreferences prefs = MyApp.getPreferences();
        gridConfig.clear();
        for (int i = 0; i < 10; i++) {
            // === 修改：儲存的 Key 加上頁碼 (例如：GRID_CELL_PAGE_0_1) ===
            String savedName = prefs.getString("GRID_CELL_PAGE_" + pageIndex + "_" + i, null);
            if (savedName != null) {
                try {
                    gridConfig.add(TelemetryType.valueOf(savedName));
                } catch (IllegalArgumentException e) {
                    gridConfig.add(getDefaultType(i));
                }
            } else {
                gridConfig.add(getDefaultType(i));
            }
        }
    }

    private void saveGridConfig() {
        SharedPreferences.Editor editor = MyApp.getPreferences().edit();
        for (int i = 0; i < gridConfig.size(); i++) {
            // === 修改：儲存的 Key 加上頁碼 ===
            editor.putString("GRID_CELL_PAGE_" + pageIndex + "_" + i, gridConfig.get(i).name());
        }
        editor.apply();
    }

    private void showSelectionDialog(final int position) {
        if (getContext() == null) return;

        final TelemetryType[] allTypes = TelemetryType.values();
        String[] displayNames = new String[allTypes.length];
        for (int i = 0; i < allTypes.length; i++) {
            displayNames[i] = getString(allTypes[i].nameResId);
        }

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_select_telemetry)
                .setItems(displayNames, (dialog, which) -> {
                    gridConfig.set(position, allTypes[which]);
                    adapter.notifyItemChanged(position);
                    saveGridConfig();
                })
                .show();
    }

    private TelemetryType getDefaultType(int index) {
        // 第一頁的預設欄位 (狀態資料)
        if (pageIndex == 0) {
            switch (index) {
                case 0: return TelemetryType.SPEED;
                case 1: return TelemetryType.CADENCE;
                case 2: return TelemetryType.MOTOR_POWER;
                case 3: return TelemetryType.PEDAL_POWER;
                case 4: return TelemetryType.BATTERY_CURRENT;
                case 5: return TelemetryType.BATTERY_VOLTAGE;
                case 6: return TelemetryType.MOTOR_TEMP;
                case 7: return TelemetryType.WATT_HOUR;
                case 8: return TelemetryType.SOC;
                case 9: return TelemetryType.DUTY_CYCLE;
                default: return TelemetryType.SPEED;
            }
        } 
        // 第二頁的預設欄位 (除錯資料)
        else {
            switch (index) {
                case 0: return TelemetryType.FOC_ANGLE;
                case 1: return TelemetryType.MOTOR_ERPS;
                case 2: return TelemetryType.TORQUE_ADC;
                case 3: return TelemetryType.PEDAL_TORQUE;
                case 4: return TelemetryType.FW_OFFSET;
                case 5: return TelemetryType.TORQUE_SMOOTH_PCT;
                case 6: return TelemetryType.PCB_TEMP;
                case 7: return TelemetryType.BATTERY_VOLTAGE; // 可以重複放一些常用的
                case 8: return TelemetryType.BATTERY_CURRENT;
                case 9: return TelemetryType.SPEED;
                default: return TelemetryType.FOC_ANGLE;
            }
        }
    }
}