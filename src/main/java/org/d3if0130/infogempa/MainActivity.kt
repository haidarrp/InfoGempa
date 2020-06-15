package org.d3if0130.infogempa

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.d3if0130.infogempa.adapter.GempaAdapter
import org.d3if0130.infogempa.api.ApiInstance
import org.d3if0130.infogempa.model.DataGempa
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    @SuppressLint("CheckResult")
    lateinit var  mTTS:TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTTS= TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
            status ->
            if (status != TextToSpeech.ERROR){
                mTTS.language = Locale.UK
            }
        })
        val getData = ApiInstance.create().getData()
        getData.enqueue(object : Callback<DataGempa.Infogempa> {

            override fun onFailure(call: Call<DataGempa.Infogempa>, t: Throwable) {
                onError(t)
            }

            override fun onResponse(
                    call: Call<DataGempa.Infogempa>,
                    response: Response<DataGempa.Infogempa>
            ) {
                val layoutManager = LinearLayoutManager(this@MainActivity)
                val offside = ItemOffsetDecoration(20)
                val adapter = GempaAdapter(response.body()!!.gempa!!, itemClick)
                list_gempa.apply {
                    setLayoutManager(layoutManager)
                    addItemDecoration(offside)
                    setAdapter(adapter)
                }
                progress_loader.visibility = View.GONE
            }
        })

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.website -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://www.bmkg.go.id/")
                }
                startActivity(intent)
            }
        }
    }

    private val itemClick = object : ItemClick {
        override fun OnItemClickRecycler(gempa: DataGempa.Gempa) {
            val snackbar = Snackbar.make(
                    main_layout,
                    "Gempa dirasakan pada kedalaman ${gempa.kedalaman}, dengan kekuatan ${gempa.magnitude} SR",
                    Snackbar.LENGTH_INDEFINITE
            )
            val snackView = snackbar.view
            val textMsg = snackView.findViewById<TextView>(R.id.snackbar_text)
            textMsg.maxLines = 20
            snackbar.setAction("Tutup") {
                snackbar.dismiss()
            }
            snackbar.show()

            val toSpeak = "At ${gempa.tanggal}, An earthquake was felt at a depth of ${gempa.kedalaman}, with ${gempa.magnitude} of magnitude"
            mTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null)

        }
    }

    private fun onError(it: Throwable?) {
        Log.d("Gagal mendapatkan data!", it!!.message)
    }

//    private fun isDarkModeOn(): Boolean {
//        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
//    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        return when (item.itemId) {
//            R.id.theme -> {
//                if (isDarkModeOn()) {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                    startActivity(Intent(requireContext(), MainActivity::class.java).apply {
//                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    })
//                } else {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                    startActivity(Intent(requireContext(), MainActivity::class.java).apply {
//                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    })
//                }
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }


}
