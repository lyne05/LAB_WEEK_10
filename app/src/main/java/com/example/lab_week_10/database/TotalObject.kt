package com.example.lab_week_10.database

import androidx.room.ColumnInfo

data class TotalObject(
    @ColumnInfo(name = "value") val value: Int,
    @ColumnInfo(name = "date") val date: String
)
