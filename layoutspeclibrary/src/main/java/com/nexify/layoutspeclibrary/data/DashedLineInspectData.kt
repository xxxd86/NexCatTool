package com.nexify.layoutspeclibrary.data


data class NTLine(var startX:Float = 0f,var startY:Float = 0f,var stopX:Float = 0f,var stopY:Float = 0f)
data class NTLineMap(var left:NTLine? , var right:NTLine?,var top:NTLine?,var bottom:NTLine?)