package br.net.easify.tracker.utils

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
        var mView: View? = null
        var button: Button? = null
        var btnNegativo: Button? = null
        var message: TextView? = null
        var title: TextView? = null
        var congratulation_card: CardView? = null
        var mAlertDialog: AlertDialog? = null
        fun getViewOneButton(
            context: Context?,
            titulo: String?,
            mensagem: String?,
            botaoPositivo: String?,
            onClick: View.OnClickListener?,
            colorCard: Int
        ): View? {
            mView =
                View.inflate(context, R.layout.layout_dialog_msg, null)
            congratulation_card =
                mView!!.findViewById(R.id.congratulation_card)
            button =
                mView!!.findViewById(R.id.read_btn)
            message =
                mView!!.findViewById(R.id.message)
            title =
                mView!!.findViewById(R.id.title)
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
                    View.OnClickListener { view: View? -> mAlertDialog!!.dismiss() })
            }
            return mView
        }

        fun getViewOneButton(
            context: Context?,
            titulo: String?,
            mensagem: String?,
            botaoPositivo: String?,
            onClick: View.OnClickListener?
        ): View? {
            mView =
                View.inflate(context, R.layout.layout_dialog_msg, null)
            congratulation_card =
                mView!!.findViewById(R.id.congratulation_card)
            button =
                mView!!.findViewById(R.id.read_btn)
            message =
                mView!!.findViewById(R.id.message)
            title =
                mView!!.findViewById(R.id.title)
            title!!.setText(titulo)
            message!!.setText(mensagem)
            button!!.setText(botaoPositivo)
            if (onClick != null) {
                button!!.setOnClickListener(onClick as View.OnClickListener?)
            } else {
                button!!.setOnClickListener(
                    View.OnClickListener { view: View? -> mAlertDialog!!.dismiss() })
            }
            return mView
        }

        fun getViewOneButton(
            context: Context?,
            @StringRes titulo: Int,
            @StringRes mensagem: Int,
            botaoPositivo: String?,
            onClick: View.OnClickListener?,
            colorCard: Int
        ): View? {
            mView =
                View.inflate(context, R.layout.layout_dialog_msg, null)
            congratulation_card =
                mView!!.findViewById(R.id.congratulation_card)
            button =
                mView!!.findViewById(R.id.read_btn)
            message =
                mView!!.findViewById(R.id.message)
            title =
                mView!!.findViewById(R.id.title)
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
                    View.OnClickListener { view: View? -> mAlertDialog!!.dismiss() })
            }
            return mView
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
            mView =
                View.inflate(context, R.layout.layout_dialog_msg_two_button, null)
            congratulation_card =
                mView!!.findViewById(R.id.congratulation_card)
            button =
                mView!!.findViewById(R.id.btnPositivo)
            btnNegativo =
                mView!!.findViewById(R.id.btnNegativo)
            message =
                mView!!.findViewById(R.id.message)
            title =
                mView!!.findViewById(R.id.title)
            if (colorCard != 0) congratulation_card!!.setCardBackgroundColor(
                colorCard
            )
            title!!.setText(titulo)
            message!!.setText(mensagem)
            button!!.setText(botaoPositivo)
            btnNegativo!!.setText(botaoNegativo)
            button!!.setOnClickListener(onClick)
            btnNegativo!!.setOnClickListener(onClickNega)
            return mView
        }

        /**
         * Show simples
         *
         * @param context
         * @param titulo
         * @param mensagem
         */
        fun show(
            context: Context?,
            titulo: String?,
            mensagem: String?
        ): AlertDialog? {
            val builder =
                Builder(context!!)
                    .setView(
                        getViewOneButton(
                            context,
                            titulo,
                            mensagem,
                            "Fechar",
                            null
                        )
                    )
                    .setCancelable(false)
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
            }
            mAlertDialog = builder.show()
            mAlertDialog!!.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return mAlertDialog
        }

        /**
         * Show simples
         *
         * @param context
         * @param titulo
         * @param mensagem
         */
        fun show(
            context: Context?,
            @StringRes titulo: Int,
            @StringRes mensagem: Int
        ): AlertDialog? {
            val builder =
                Builder(context!!)
                    .setView(
                        getViewOneButton(
                            context,
                            titulo,
                            mensagem,
                            "Fechar",
                            null,
                            0
                        )
                    )
                    .setCancelable(false)
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
            }
            mAlertDialog = builder.show()
            mAlertDialog!!.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return mAlertDialog
        }

        /**
         * Show simples
         *
         * @param context
         * @param titulo
         * @param mensagem
         */
        fun show(
            context: Context?,
            @StringRes titulo: Int,
            @StringRes mensagem: Int,
            colorCard: Int
        ): AlertDialog? {
            val builder =
                Builder(context!!)
                    .setView(
                        getViewOneButton(
                            context,
                            titulo,
                            mensagem,
                            "Fechar",
                            null,
                            colorCard
                        )
                    )
                    .setCancelable(false)
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
            }
            mAlertDialog = builder.show()
            mAlertDialog!!.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return mAlertDialog
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
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
            }
            mAlertDialog = builder.show()
            mAlertDialog!!.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return mAlertDialog
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
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
            }
            mAlertDialog = builder.show()
            mAlertDialog!!.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return mAlertDialog
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
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
            }
            mAlertDialog = builder.show()
            mAlertDialog!!.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return mAlertDialog
        }

        /**
         * Show com opção
         *
         * @param context
         * @param titulo
         * @param mensagem
         * @param botaoPositivo
         * @param onClickPosi
         * @param botaoNegativo
         * @param onClickNega
         * @return
         */
        fun show(
            context: Context?,
            titulo: String?,
            mensagem: String?,
            botaoPositivo: String?,
            onClickPosi: View.OnClickListener?,
            botaoNegativo: String?,
            onClickNega: View.OnClickListener?
        ): AlertDialog? {
            val builder =
                Builder(context!!)
                    .setView(
                        getViewTwoButton(
                            context,
                            titulo,
                            mensagem,
                            botaoPositivo,
                            onClickPosi,
                            botaoNegativo,
                            onClickNega,
                            0
                        )
                    )
                    .setCancelable(false)
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
            }
            mAlertDialog = builder.show()
            mAlertDialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return mAlertDialog
        }

        /**
         * Show com opção
         *
         * @param context
         * @param titulo
         * @param mensagem
         * @param botaoPositivo
         * @param onClickPosi
         * @param botaoNegativo
         * @param onClickNega
         * @return
         */
        fun show(
            context: Context?,
            titulo: String?,
            mensagem: String?,
            botaoPositivo: String?,
            onClickPosi: View.OnClickListener?,
            botaoNegativo: String?,
            onClickNega: View.OnClickListener?,
            colorCard: Int
        ): AlertDialog? {
            val builder =
                Builder(context!!)
                    .setView(
                        getViewTwoButton(
                            context,
                            titulo,
                            mensagem,
                            botaoPositivo,
                            onClickPosi,
                            botaoNegativo,
                            onClickNega,
                            colorCard
                        )
                    )
                    .setCancelable(false)
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
            }
            mAlertDialog = builder.show()
            mAlertDialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            return mAlertDialog
        }
    }
}
