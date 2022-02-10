package com.caldas.jettipapp.util

fun calculateTotalTip(totalBill: Double, tipPercengage: Int): Double {
    return if (totalBill > 1 && totalBill.toString().isNotEmpty()) {
        (totalBill * tipPercengage) / 100
    } else {
        0.0
    }
}

fun calculateTotalPerPerson(totalBill: Double, splitBy: Int, tipPercengage: Int) : Double {
    val bill = calculateTotalTip(totalBill, tipPercengage) + totalBill
    return (bill / splitBy)
}