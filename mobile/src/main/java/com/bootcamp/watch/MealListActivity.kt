package com.bootcamp.watch

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import io.github.maylcf.shared.Meal
import kotlinx.android.synthetic.main.activity_main.*

class MealListActivity : AppCompatActivity(), MealListAdapter.Callback, GoogleApiClient.ConnectionCallbacks {

    private var adapter: MealListAdapter? = null
    private var connectedNode: List<Node>? = null
    lateinit var client: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        setupGoogleClient()
    }

    // MealListAdapter.Callback

    override fun mealClicked(meal: Meal) {
        val gson = Gson()

        connectedNode?.forEach { node ->
            val bytes = gson.toJson(meal).toByteArray()
            Wearable.MessageApi.sendMessage(client, node.id, "/meal", bytes)
        }
    }

    // GoogleApiClient.ConnectionCallbacks

    override fun onConnected(bundle: Bundle?) {
        Wearable.NodeApi.getConnectedNodes(client).setResultCallback {
            connectedNode = it.nodes
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        connectedNode = null
    }

    private fun setupRecyclerView() {
        val meals = MealStore.fetchMeals(this)
        adapter = MealListAdapter(meals, this)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)
    }

    private fun setupGoogleClient() {
        client = GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build()

        client.connect()
    }
}
