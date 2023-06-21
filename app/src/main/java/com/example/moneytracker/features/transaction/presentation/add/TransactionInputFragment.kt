package com.example.moneytracker.features.transaction.presentation.add

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moneytracker.R
import com.example.moneytracker.features.transaction.domain.model.TransactionType
import com.example.moneytracker.features.transaction.domain.model.getTitle
import io.ghyeok.stickyswitch.widget.StickySwitch
import io.ghyeok.stickyswitch.widget.StickySwitch.OnSelectedChangeListener
import studio.carbonylgroup.textfieldboxes.ExtendedEditText

class TransactionInputFragment : Fragment() {
    interface TransactionInputArgs {
        var transactionType: TransactionType
        fun onSwitchChanged(transactionType: TransactionType)
    }

    lateinit var args: TransactionInputArgs
    fun setTransactionInputArgs(args: TransactionInputArgs) {
        this.args = args
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<StickySwitch>(R.id.sw_tran).apply {
            setA(object : OnSelectedChangeListener {
                override fun onSelectedChange(direction: StickySwitch.Direction, text: String) {
                    Log.d("Debug", "$text - $direction")
                    args.onSwitchChanged(TransactionType.EXPENSE)
                }
            })
        }
        view.findViewById<ExtendedEditText>(R.id.tfe_tran_title).apply {
            hint = getString(R.string.transaction_name, args.transactionType.getTitle())
        }
    }

}