package br.net.easify.tracker.view.adapters

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.repositories.database.model.SqliteRoute
import br.net.easify.tracker.databinding.HolderRouteBinding
import br.net.easify.tracker.repositories.RouteRepository
import javax.inject.Inject

class RouteHistoryAdapter(private var application: Application, private var routes: ArrayList<SqliteRoute>): RecyclerView.Adapter<RouteHistoryAdapter.RouteViewHolder>() {

    @Inject
    lateinit var routeRepository: RouteRepository

    init {
        (application as MainApplication).getAppComponent()?.inject(this)
    }

    fun updateRoutes(newRoutes: List<SqliteRoute>) {
        routes.clear()
        routes.addAll(newRoutes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<HolderRouteBinding>(inflater, R.layout.holder_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {

        var route = routes[position]

        if ( route.sync == 0 && route.in_progress == 0 ) {
            holder.view.refreshButton.visibility = View.VISIBLE

//            holder.view.refreshButton.setOnClickListener(View.OnClickListener {
//                routeRepository.synchronizeTrackingActivity(route)
//            })
        }

        holder.view.route = route
    }

    override fun getItemCount() = routes.size

    class RouteViewHolder(var view: HolderRouteBinding): RecyclerView.ViewHolder(view.root)
}