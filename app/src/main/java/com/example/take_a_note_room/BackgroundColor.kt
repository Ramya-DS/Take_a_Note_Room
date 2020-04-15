package com.example.take_a_note_room

enum class BackgroundColor(val color: Int) {
    COLOR_1(R.color.Color_1),
    COLOR_2(R.color.Color_2),
    COLOR_3(R.color.Color_3),
    COLOR_4(R.color.Color_4),
    COLOR_5(R.color.Color_5),
    COLOR_6(R.color.Color_6),
    COLOR_7(R.color.Color_7),
    COLOR_8(R.color.Color_8),
    COLOR_9(R.color.Color_9),
    COLOR_10(R.color.Color_10),
    COLOR_11(R.color.Color_11),
    COLOR_12(R.color.Color_12),
    COLOR_13(R.color.Color_13),
    COLOR_14(R.color.Color_14),
    COLOR_15(R.color.Color_15),
    COLOR_16(R.color.Color_16),
    COLOR_17(R.color.Color_17),
    COLOR_18(R.color.Color_18);

    companion object {
        fun random(): Int {
            val values = values()
            val randomIndex = (0..17).random()
            return values[randomIndex].color
        }
    }


}