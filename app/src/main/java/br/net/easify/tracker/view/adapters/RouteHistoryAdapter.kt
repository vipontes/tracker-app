package br.net.easify.tracker.view.adapters

import android.app.Activity
import android.app.Application
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.databinding.HolderRouteBinding
import br.net.easify.tracker.repositories.RouteRepository
import br.net.easify.tracker.repositories.database.model.SqliteRoute
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class RouteHistoryAdapter(
    application: Application,
    private var routes: ArrayList<SqliteRoute>,
    private var listener: OnItemClick,
    private var parentActivity: Activity
) : RecyclerView.Adapter<RouteHistoryAdapter.RouteViewHolder>() {

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

    interface OnItemClick {
        fun onItemClick(route: SqliteRoute)
        fun onItemDelete(routeId: Long)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RouteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<HolderRouteBinding>(
            inflater,
            R.layout.holder_route,
            parent,
            false
        )
        return RouteViewHolder(view)
    }

    fun removeItem(position: Int) {
        Snackbar.make(
            parentActivity.findViewById(R.id.frame_layout),
            parentActivity.getString(R.string.are_you_sure),
            2500
        )
            .setAction(parentActivity.getString(R.string.delete)) {
                val route = routes[position]
                listener.onItemDelete(route.user_route_id!!)
            }.addCallback(object : BaseCallback<Snackbar?>() {
                override fun onDismissed(
                    transientBottomBar: Snackbar?,
                    event: Int
                ) {
                    super.onDismissed(transientBottomBar, event)
                    notifyItemChanged(position)
                }
            }).show()
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {

        val route = routes[position]

        holder.itemView.setOnClickListener {
            listener.onItemClick(route)
        }

        holder.view.route = route
    }

    override fun getItemCount() = routes.size

    class RouteViewHolder(var view: HolderRouteBinding) :
        RecyclerView.ViewHolder(view.root)
}