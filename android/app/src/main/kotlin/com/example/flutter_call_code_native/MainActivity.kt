package com.example.flutter_call_code_native

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "samples.flutter.dev/battery"
    private val Tag_Battery = "Battery";

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        val mChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
        mChannel.setMethodCallHandler { call, result ->
            if (call.method == "getBatteryLevel") {
                Log.d(Tag_Battery, "call get battery level")
                val batteryLevel = getBatterLevel()

                if (batteryLevel != -1) {
                    result.success(batteryLevel);
                } else {
                    result.error("ERROR", "Battery level not available", null)
                }
            } else {
                result.notImplemented();
            }
        }
    }

    private fun getBatterLevel(): Int {
        val batteryLevel: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager;
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

            Log.d(Tag_Battery, "1:$batteryLevel");
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            Log.d(Tag_Battery, "2: $batteryLevel");
        }

        return batteryLevel
    }
}
