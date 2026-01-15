package com.github.soramame0256.scheduler.repository.converter

import android.util.Log
import androidx.room.TypeConverter
import com.github.soramame0256.scheduler.BuildConfig
import com.github.soramame0256.scheduler.model.Weekday

class WeekdayConverter {

    @TypeConverter
    fun toDbValue(weekday: Weekday): Int = weekday.value

    /*
    基本的にエラーが起きた場合はDEBUGビルドの場合クラッシュ、リリースの場合回避値によるクラッシュ回避としています。
     */
    @TypeConverter
    fun fromDbValue(i: Int): Weekday {
        val weekday = Weekday.fromValue(i)
        return if (weekday == Weekday.ERROR) {
            // weekdayが見つからなかった場合の処理
            if (BuildConfig.DEBUG) {
                throw IllegalArgumentException("データベースに無効な値が設定されています。: Weekday = $i")
            } else {
                Log.e("WeekdayConverter", "データベースに無効な値が設定されています。: Weekday = $i")
                Weekday.ERROR
            }
        } else {
            weekday
        }
    }
}
