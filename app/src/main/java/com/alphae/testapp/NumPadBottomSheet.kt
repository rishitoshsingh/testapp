package com.alphae.testapp

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by rishi on 2/5/19.
 */
class NumPadBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(): NumPadBottomSheet {
            return NumPadBottomSheet()
        }
    }

    override fun getTheme(): Int = R.style.NumPadBottomBar

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(context, theme)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.numpad_bottomsheet, container, false)
    }



}