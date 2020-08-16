package br.net.easify.tracker.helpers

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import br.net.easify.tracker.R


class CustomAlertDialog : AlertDialog {
    constructor(context: Context?) : super(context!!) {}

    companion object {
        var view: View? = null
        var button: Button? = null
        var negativeButton: Button? = null
        var message: TextView? = null
        var title: TextView? = null
        var congratulation_card: CardView? = null
        var dialog: AlertDialog? = null
        fun getViewOneButton(
            context: Context?,
            titulo: String?,
            mensagem: String?,
            botaoPositivo: String?,
            onClick: View.OnClickListener?,
            colorCard: Int
        ): View? {
            view =
                View.inflate(context, R.layout.layout_dialog_msg, null)
            congratulation_card =
                view!!.findViewById(R.id.congratulation_card)
            button =
                view!!.findViewById(R.id.read_btn)
            message =
                view!!.findViewById(R.id.message)
            title =
                view!!.findViewById(R.id.title)
            title!!.setText(titulo)
            message!!.setText(mensagem)
            button!!.setText(botaoPositivo)
            if (colorCard != 0) congratulation_card!!.setCardBackgroundColor(
                colorCard
            )
            if (onClick != null) {
                button!!.setOnClickListener(onClick as View.OnClickListener?)
            } else {
                button!!.setOnClickListener(
                    View.OnClickListener { view: View? -> dialog!!.dismiss() })
            }
            return view
        }

        fun getViewOneButton(
            context: Context?,
            titulo: String?,
            mensagem: String?,
            botaoPositivo: String?,
            onClick: View.OnClickListener?
        ): View? {
            view =
                View.inflate(context, R.layout.layout_dialog_msg, null)
            congratulation_card =
                view!!.findViewById(R.id.congratulation_card)
            button =
                view!!.findViewById(R.id.read_btn)
            message =
                view!!.findViewById(R.id.message)
            title =
                view!!.findViewById(R.id.title)
            title!!.setText(titulo)
            message!!.setText(mensagem)
            button!!.setText(botaoPositivo)
            if (onClick != null) {
                button!!.setOnClickListener(onClick as View.OnClickListener?)
            } else {
                button!!.setOnClickListener(
                    View.OnClickListener { view: View? -> dialog!!.dismiss() })
            }
            return view
        }

        fun getViewOneButton(
            context: Context?,
            @StringRes titulo: Int,
            @StringRes mensagem: Int,
            botaoPositivo: String?,
            onClick: View.OnClickListener?,
            colorCard: Int
        ): View? {
            view =
                View.inflate(context, R.layout.layout_dialog_msg, null)
            congratulation_card =
                view!!.findViewById(R.id.congratulation_card)
            button =
                view!!.findViewById(R.id.read_btn)
            message =
                view!!.findViewById(R.id.message)
            title =
                view!!.findViewById(R.id.title)
            if (colorCard != 0) congratulation_card!!.setCardBackgroundColor(
                colorCard
            )
            title!!.setText(titulo)
            message!!.setText(mensagem)
            button!!.setText(botaoPositivo)
            if (onClick != null) {
                button!!.setOnClickListener(onClick as View.OnClickListener?)
            } else {
                button!!.setOnClickListener(
                    View.OnClickListener { view: View? -> dialog!!.dismiss() })
            }
            return view
        }

        fun getViewTwoButton(
            context: Context?,
            titulo: String?,
            mensagem: String?,
            botaoPositivo: String?,
            onClick: View.OnClickListener?,
            botaoNegativo: String?,
            onClickNega: View.OnClickListener?,
            colorCard: Int
        ): View? {
            view =
                View.inflate(context, R.layout.layout_dialog_msg_two_button, null)
            congratulation_card =
                view!!.findViewById(R.id.congratulation_card)
            button =
                view!!.findViewById(R.id.btnPositivo)
            negativeButton =
                view!!.findViewById(R.id.btnNegativo)
            message =
                view!!.findViewById(R.id.message)
            title =
                view!!.findViewById(R.id.title)
            if (colorCard != 0) congratulation_card!!.setCardBackgroundColor(
                colorCard
            )
            title!!.setText(titulo)
            message!!.setText(mensagem)
            button!!.setText(botaoPositivo)
            negativeButton!!.setText(botaoNegativo)
            button!!.setOnClickListener(onClick)
            negativeButton!!.setOnClickListener(onClickNega)
            return view
        }

        fun show(
            context: Context?,
            title: String?,
            message: String?
        ): AlertDialog? {
            val builder =
                Builder(context!!)
                    .setView(
                        getViewOneButton(
                            context,
                            title,
                            message,
                            "Close",
                            null
                        )
                    )
                    .setCancelable(false)
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            dialog = builder.show()
            dialog!!.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return dialog
        }

        fun show(
            context: Context?,
            @StringRes title: Int,
            @StringRes message: Int
        ): AlertDialog? {
            val builder =
                Builder(context!!)
                    .setView(
                        getViewOneButton(
                            context,
                            title,
                            message,
                            "Close",
                            null,
                            0
                        )
                    )
                    .setCancelable(false)
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            dialog = builder.show()
            dialog!!.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return dialog
        }

        fun show(
            context: Context?,
            @StringRes title: Int,
            @StringRes message: Int,
            colorCard: Int
        ): AlertDialog? {
            val builder =
                Builder(context!!)
                    .setView(
                        getViewOneButton(
                            context,
                            title,
                            message,
                            "Close",
                            null,
                            colorCard
                        )
                    )
                    .setCancelable(false)
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            dialog = builder.show()
            dialog!!.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return dialog
        }

        fun show(
            context: Context?,
            titulo: String?,
            mensagem: String?,
            botaoPositivo: String?,
            onClick: View.OnClickListener?,
            colorCard: Int
        ): AlertDialog? {
            val builder =
                Builder(context!!)
                    .setView(
                        getViewOneButton(
                            context,
                            titulo,
                            mensagem,
                            botaoPositivo,
                            onClick,
                            colorCard
                        )
                    )
                    .setCancelable(false)
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            dialog = builder.show()
            dialog!!.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return dialog
        }

        fun show(
            context: Context?,
            titulo: String?,
            mensagem: String?,
            botaoPositivo: String?,
            onClick: View.OnClickListener?
        ): AlertDialog? {
            val builder =
                Builder(context!!)
                    .setView(
                        getViewOneButton(
                            context,
                            titulo,
                            mensagem,
                            botaoPositivo,
                            onClick
                        )
                    )
                    .setCancelable(false)
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            dialog = builder.show()
            dialog!!.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return dialog
        }

        fun show(
            context: Context?,
            titulo: String?,
            mensagem: String?,
            botaoPCancel: String?,
            onClick: View.OnClickListener?,
            cancelable: Boolean
        ): AlertDialog? {
            val builder =
                Builder(context!!)
                    .setView(
                        getViewOneButton(
                            context,
                            titulo,
                            mensagem,
                            botaoPCancel,
                            onClick
                        )
                    )
                    .setCancelable(cancelable)
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            dialog = builder.show()
            dialog!!.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return dialog
        }

        fun show(
            context: Context?,
            title: String?,
            message: String?,
            positiveButton: String?,
            onPositiveButtonClick: View.OnClickListener?,
            negativeButton: String?,
            onNegativeButtonClick: View.OnClickListener?
        ): AlertDialog? {
            val builder =
                Builder(context!!)
                    .setView(
                        getViewTwoButton(
                            context,
                            title,
                            message,
                            positiveButton,
                            onPositiveButtonClick,
                            negativeButton,
                            onNegativeButtonClick,
                            0
                        )
                    )
                    .setCancelable(false)
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            dialog = builder.show()
            dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return dialog
        }

        fun show(
            context: Context?,
            title: String?,
            message: String?,
            positiveButton: String?,
            onPositiveButtonClick: View.OnClickListener?,
            negativeButton: String?,
            onNegativeButtonClick: View.OnClickListener?,
            colorCard: Int
        ): AlertDialog? {
            val builder =
                Builder(context!!)
                    .setView(
                        getViewTwoButton(
                            context,
                            title,
                            message,
                            positiveButton,
                            onPositiveButtonClick,
                            negativeButton,
                            onNegativeButtonClick,
                            colorCard
                        )
                    )
                    .setCancelable(false)
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            dialog = builder.show()
            dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return dialog
        }
    }
}
