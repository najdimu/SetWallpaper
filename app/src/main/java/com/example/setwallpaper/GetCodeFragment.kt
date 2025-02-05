package com.example.setwallpaper

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.setwallpaper.databinding.FragmentGetCodeBinding


class GetCodeFragment : Fragment(R.layout.fragment_get_code) {

    private lateinit var binding: FragmentGetCodeBinding
    private val tariffName = listOf("Вы выбрали: 1 неделя", "Вы выбрали: 1 месяц",
        "Вы выбрали: 3 месяца", "Вы выбрали: 6 месяцев", "Вы выбрали: 1 год")
    private val tariffAmount = listOf("Стоимость: 29 рублей", "Стоимость: 99 рублей",
        "Стоимость: 249 рублей", "Стоимость: 429 рублей", "Стоимость: 799 рублей")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGetCodeBinding.bind(view)

        val sharedPreferences = requireContext().getSharedPreferences("remove_tariff", MODE_PRIVATE)
        val tariffID = sharedPreferences.getInt("tariff_ID", -1)
        val tariffStatus = sharedPreferences.getBoolean("tariff_status", false)

        if (tariffStatus){
            binding.chosenTariffCard.visibility = View.VISIBLE
            binding.chosenTariffTitle.text = tariffName[tariffID]
            binding.chosenTariffPrice.text = tariffAmount[tariffID]
        }else{
            binding.chosenTariffCard.visibility = View.GONE
        }

        binding.backBtnGetCodeFg.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnGetCode.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFg, ChooseTariffAdsFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnCheckingPayment.setOnClickListener {
            //TODO link to check payment
        }


    }


}