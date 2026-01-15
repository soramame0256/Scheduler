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
        return Weekday.fromValue(i)
    }
}
