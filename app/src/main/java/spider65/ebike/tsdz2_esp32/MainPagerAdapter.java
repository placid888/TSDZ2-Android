package spider65.ebike.tsdz2_esp32;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import spider65.ebike.tsdz2_esp32.data.TSDZ_Status;
import spider65.ebike.tsdz2_esp32.fragments.FragmentJkBattery; // 新增：電池詳細頁
import spider65.ebike.tsdz2_esp32.fragments.FragmentStatus;
import spider65.ebike.tsdz2_esp32.fragments.MyFragmentListener;

public class MainPagerAdapter extends FragmentStateAdapter {
    // === 修改：總共有 3 個分頁 ===
    private final MyFragmentListener[] fragments = new MyFragmentListener[3];
    private final TSDZ_Status mStatus;

    MainPagerAdapter(FragmentActivity fragmentActivity, TSDZ_Status status) {
        super(fragmentActivity);
        mStatus = status;
    }

    public MyFragmentListener getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return 3; // 改為 3 頁
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment f;
        if (position == 0) {
            f = FragmentStatus.newInstance(mStatus, 0); // 第一頁自訂網格
        } else if (position == 1) {
            f = FragmentStatus.newInstance(mStatus, 1); // 第二頁自訂網格
        } else {
            f = FragmentJkBattery.newInstance(mStatus); // 第三頁：JK BMS 詳細電池資訊
        }
        
        fragments[position] = (MyFragmentListener) f;
        return f;
    }
}