package br.net.easify.tracker.view.fragments

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import br.net.easify.tracker.R
import br.net.easify.tracker.databinding.FragmentHistoryBinding
import br.net.easify.tracker.model.Response
import br.net.easify.tracker.repositories.database.model.SqliteRoute
import br.net.easify.tracker.view.adapters.RouteHistoryAdapter
import br.net.easify.tracker.view.route.RouteActivity
import br.net.easify.tracker.viewmodel.HistoryViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_history.*


class HistoryFragment : Fragment(), RouteHistoryAdapter.OnItemClick,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var viewModel: HistoryViewModel
    private lateinit var dataBinding: FragmentHistoryBinding
    private lateinit var routesAdapter: RouteHistoryAdapter

    private val routesObserver = Observer<List<SqliteRoute>> { list ->
        list?.let {
            routesAdapter.updateRoutes(it)
            viewModel.getTotalDistance()
        }
        swipeRefreshLayout.isRefreshing = false
    }

    private val totalDistanceObserver = Observer<String> {
        dataBinding.total = it
    }

    private val deleteRouteObserver = Observer<Response> {
        if (!it.success) {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
        }

        routesAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_history,
            container,
            false
        )

        viewModel =
            ViewModelProviders.of(this).get(HistoryViewModel::class.java)
        viewModel.routes.observe(
            viewLifecycleOwner,
            routesObserver
        )
        viewModel.totalDistance.observe(
            viewLifecycleOwner,
            totalDistanceObserver
        )
        viewModel.deleteRouteResponse.observe(viewLifecycleOwner, deleteRouteObserver)
        viewModel.refresh()

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        routesAdapter =
            RouteHistoryAdapter(
                requireActivity().application,
                arrayListOf(),
                this,
                requireActivity()
            )
        dataBinding.routesHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = routesAdapter
        }

        dataBinding.swipeRefreshLayout.setOnRefreshListener(this)

        val simpleItemTouchCallback: SimpleCallback =
            object : SimpleCallback(
                0,
                ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    routesAdapter.removeItem(viewHolder.adapterPosition)
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorRed
                            )
                        )
                        .addSwipeRightActionIcon(R.drawable.ic_delete)
                        .addSwipeRightLabel(context!!.getString(R.string.delete))
                        .setSwipeRightLabelColor(R.color.colorWhite)
                        .create()
                        .decorate()

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

        val itemTouchHelper =
            ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(dataBinding.routesHistory)
    }

    override fun onItemClick(route: SqliteRoute) {
        val intent = Intent(requireContext(), RouteActivity::class.java)
        intent.putExtra("routeId", route.user_route_id as Long)
        startActivity(intent)
    }

    override fun onItemDelete(routeId: Long) {
        viewModel.deleteRoute(routeId)
    }

    override fun onRefresh() {
        viewModel.refreshApi()
    }
}
