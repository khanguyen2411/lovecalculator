package com.hola360.crushlovecalculator.ui.event.myevent

import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.event.EventModel
import com.hola360.crushlovecalculator.dialog.MessageAlertDialog
import com.hola360.crushlovecalculator.ui.event.EventFragmentDirections
import com.hola360.crushlovecalculator.ui.event.adapter.ActionAdapter
import com.hola360.crushlovecalculator.ui.event.adapter.EventListAdapter
import com.hola360.crushlovecalculator.ui.event.base.BaseEventFragment
import com.hola360.crushlovecalculator.ui.event.eventdialog.EventDiaLog
import com.hola360.crushlovecalculator.ui.event.popup.EventActionModel
import com.hola360.crushlovecalculator.ui.event.popup.ListActionPopup
import com.hola360.crushlovecalculator.utils.ToastUtils
import com.hola360.crushlovecalculator.utils.clickWithDebounce

class MyEventFragment : BaseEventFragment<MyEventViewModel>(),
    EventListAdapter.OnMyEventItemClickListener, EventDiaLog.OnDoneClick {

    private var eventModel = EventModel(0, "", "", "", "", "", 0L, 2, 0L)
    private var isActionClicked = false
    private val actionPopup by lazy {
        ListActionPopup(mainActivity)
    }

    private val eventDialog by lazy {
        EventDiaLog.create(isAdd = true, "Add event", eventModel)
    }

    val actions = mutableListOf(
        EventActionModel(R.drawable.ic_action_edit, "Edit event"),
        EventActionModel(R.drawable.ic_action_delete, "Delete event")
    )

    override fun initView() {
        super.initView()
        mAdapter.onMyEventClickListener = this
        binding!!.tvRetryOrAdd.clickWithDebounce {
            if (!eventDialog.isAdded) {
                eventDialog.setOnDoneClick(this@MyEventFragment)
                eventDialog.show(requireActivity().supportFragmentManager, "addEvent")
            }
        }
    }

    override fun initViewModel() {
        val factory = MyEventViewModel.Factory(mainActivity.application)
        viewModel = ViewModelProvider(this, factory)[MyEventViewModel::class.java]
        initObserveData()

        viewModel.isDeleted.observe(this) {
            it?.let {
                if (it) {
                    fetchData()
                    ToastUtils.getInstance(mainActivity).showToast("Delete Event Successfully")
                }
            }
        }
    }

    override fun onItemClick(eventModel: EventModel) {
        if (!isActionClicked) {
            findNavController().navigate(
                EventFragmentDirections.actionEventFragmentToEventDetailFragment(
                    eventModel
                )
            )
        }
        isActionClicked = false
    }

    fun fetchData() {
        viewModel.fetchData()
    }

    override fun onMoreIconClick(view: View, eventModel: EventModel) {
        actionPopup.setup(view, actions, object : ActionAdapter.OnActionListener {
            override fun onItemClickListener(position: Int) {
                isActionClicked = true
                when (position) {
                    0 -> {
                        val eventDialog = EventDiaLog.create(
                            isAdd = false,
                            title = "Edit event",
                            eventModel = eventModel
                        )
                        if (!eventDialog.isAdded) {
                            eventDialog.setOnDoneClick(this@MyEventFragment)
                            if (isActionClicked) {
                                eventDialog.show(mainActivity.supportFragmentManager, "editDialog")
                            }
                        }
                    }
                    1 -> {
                        //show dialog confirm delete
                        showMessageDialogDeleteEvent(eventModel.eventId!!)
                    }
                }
            }
        })
    }

    override fun onDoneClick(eventModel: EventModel) {
        findNavController().navigate(
            EventFragmentDirections.actionEventFragmentToEventDetailFragment(
                eventModel
            )
        )
        fetchData()
    }


    fun showMessageDialogDeleteEvent(id: Int?) {
        val message = getString(R.string.string_event_delete)
        val title = getString(R.string.string_event_title_delete)
        val messageAlertDialog = MessageAlertDialog.create(title, message)
        messageAlertDialog.onAlertMessageDialogClickListener =
            object : MessageAlertDialog.OnAlertMessageDialogClickListener {
                override fun onPositive() {
                    viewModel.deleteEventById(id!!)
                }

                override fun onNegative() {
                    messageAlertDialog.dismiss()
                }
            }
        messageAlertDialog.show(parentFragmentManager, "DeletePhotos")
    }

}