package br.net.easify.tracker.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import br.net.easify.tracker.R
import br.net.easify.tracker.repositories.database.model.DbRoute
import br.net.easify.tracker.databinding.HolderRouteBinding

class RouteHistoryAdapter(private var routes: ArrayList<DbRoute>): RecyclerView.Adapter<RouteHistoryAdapter.RouteViewHolder>() {

    fun updateRoutes(newRoutes: List<DbRoute>) {
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
        holder.view.route = routes[position]
    }

    override fun getItemCount() = routes.size

    class RouteViewHolder(var view: HolderRouteBinding): RecyclerView.ViewHolder(view.root)
}