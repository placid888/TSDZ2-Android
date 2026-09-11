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

    // === 新增：Fragment 專用 UI 刷新節流閥 ===
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 250; // 控制卡片每 250 毫秒才更新一次

    public static FragmentStatus newInstance(TSDZ_Status status) {
        FragmentStatus fragment = new FragmentStatus();
        fragment.currentStatus = status;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        loadGridConfig();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // === 殺手鐧：關閉 RecyclerView 預設的更新動畫，徹底消滅閃爍！ ===
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
            // === 修改：加入時間判斷，阻擋過於頻繁的更新 ===
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
            String savedName = prefs.getString("GRID_CELL_" + i, null);
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
            editor.putString("GRID_CELL_" + i, gridConfig.get(i).name());
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
}