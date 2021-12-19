package dev.hmh.nanopol.data.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.hmh.nanopol.ui.main.price.CoinFragment
import dev.hmh.nanopol.ui.main.pool.PoolFragment
import dev.hmh.nanopol.ui.main.worker.WorkersFragment
import dev.hmh.nanopol.ui.main.dashboard.DashboardFragment
import dev.hmh.nanopol.ui.main.payment.PaymentFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return DashboardFragment()
            1 -> return WorkersFragment()
            2 -> return PoolFragment()
            3 -> return PaymentFragment()
            4 -> return CoinFragment()
        }
        return DashboardFragment()
    }
}
