package com.luthfirr.tokoapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.luthfirr.tokoapp.ui.store.visit.StoreVisitActivity

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == StoreVisitActivity.ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            if (geofencingEvent.hasError()) {
                GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.errorCode)
                return
            }

            // Test that the reported transition was of interest.
            when (geofencingEvent.geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> {
                    val transitionIntent = Intent(StoreVisitActivity.ACTION_GEOFENCE_TRANSITION)
                    val bundle =
                        bundleOf(StoreVisitActivity.TRANSITION to Geofence.GEOFENCE_TRANSITION_ENTER)
                    transitionIntent.putExtras(bundle)
                    context.sendBroadcast(transitionIntent)
                }
                Geofence.GEOFENCE_TRANSITION_EXIT -> {
                    val transitionIntent = Intent(StoreVisitActivity.ACTION_GEOFENCE_TRANSITION)
                    val bundle =
                        bundleOf(StoreVisitActivity.TRANSITION to Geofence.GEOFENCE_TRANSITION_EXIT)
                    transitionIntent.putExtras(bundle)
                    context.sendBroadcast(transitionIntent)
                }
            }
        }
    }
}