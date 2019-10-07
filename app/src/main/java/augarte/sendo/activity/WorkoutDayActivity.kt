package augarte.sendo.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import augarte.sendo.R
import augarte.sendo.adapter.WorkoutPagerAdapter
import augarte.sendo.dataModel.Day
import augarte.sendo.dataModel.Exercise
import augarte.sendo.dataModel.ExerciseDay
import augarte.sendo.fragment.AddProgressFragment
import augarte.sendo.fragment.DayPagerExercisesFragment
import augarte.sendo.fragment.DayPagerProgressFragment
import augarte.sendo.fragment.DayPagerTimerFragment
import augarte.sendo.view.BottomSheet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_add_progress.*
import kotlinx.android.synthetic.main.app_bar_main.*

class WorkoutDayActivity: AppCompatActivity() {

    private lateinit var day: Day

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.postponeEnterTransition(this)
        setContentView(R.layout.activity_add_progress)

        day = if (savedInstanceState == null) {
            intent.extras?.getParcelable("day")!!
        } else {
            savedInstanceState.getParcelable("day") as Day
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val pageAdapter = WorkoutPagerAdapter(supportFragmentManager)

        val listenerSerie = object : OnAddProgress{
            override fun onSerieAdded() {
                bottomsheet.setState(BottomSheetBehavior.STATE_HIDDEN)
            }
        }

        val listener = object : OnExerciseDayClickListener{
            override fun onItemClick(exerciseDay: ExerciseDay) {
                bottomsheet.fullScreen = false
                bottomsheet.setFragment(AddProgressFragment(exerciseDay, listenerSerie))
            }
        }

        pageAdapter.addFragment(DayPagerExercisesFragment(day.exerciseDayList, listener))
        pageAdapter.addFragment(DayPagerProgressFragment(day))
        pageAdapter.addFragment(DayPagerTimerFragment())
        viewPager.adapter = pageAdapter

        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                bottomNavigation.menu.getItem(position).isChecked = true
            }

        })

        bottomNavigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item->
            when(item.itemId){
                R.id.workout ->{
                    viewPager.currentItem = 0
                    return@OnNavigationItemSelectedListener true
                }
                R.id.progress ->{
                    viewPager.currentItem = 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.timer ->{
                    viewPager.currentItem = 2
                    return@OnNavigationItemSelectedListener true
                } else -> viewPager.currentItem = 1
            }
            false
        })
        bottomNavigation.selectedItemId = R.id.workout
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    interface OnAddProgress {
        fun onSerieAdded()
    }

    interface OnExerciseDayClickListener {
        fun onItemClick(exerciseDay: ExerciseDay)
    }
}