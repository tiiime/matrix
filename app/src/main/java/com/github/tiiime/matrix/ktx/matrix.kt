package com.github.tiiime.matrix.ktx

import android.graphics.Matrix

operator fun Matrix.get(index: Int) = FloatArray(9).apply(::getValues)[index]
