package spider65.ebike.tsdz2_esp32;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import spider65.ebike.tsdz2_esp32.data.TSDZ_Status;
// 注意：我們已經不需要匯入 FragmentDebug 了
import spider65.ebike.tsdz2_esp32.fragments.FragmentStatus;
import spider65.ebike.tsdz2_esp32.fragments.MyFragmentListener;

public class MainPagerAdapter extends FragmentStateAdapter {
    private final MyFragmentListener[] fragments = new MyFragmentListener[2];
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
        return 2;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // === 核心修改：不管第一頁(0)還是第二頁(1)，我們都載入 FragmentStatus ===
        // 並將 position (頁碼) 傳遞進去，讓它可以區分儲存空間
        Fragment f = FragmentStatus.newInstance(mStatus, position);
        fragments[position] = (MyFragmentListener)f;
        return f;
    }
}