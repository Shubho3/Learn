package com.shubh.learn.ui.companyHome.friend

//import com.github.barteksc.pdfviewer.PDFView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.shubh.learn.R
import com.shubh.learn.databinding.FragmentCompenyPriceListBinding
import com.shubh.learn.utills.SharedPref
import com.shubh.learn.utills.errorSnack

class CompenyPriceListFragment : Fragment() {
    private lateinit var binding: FragmentCompenyPriceListBinding
    lateinit var sharedPref: SharedPref
    var pdfUrl = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_compeny_price_list,
            container,
            false
        )
        sharedPref = SharedPref(requireContext())
        binding.header.tvLogo.text = requireContext().getString(R.string.price_list)
        binding.header.imgHeader.setOnClickListener { requireActivity().onBackPressed() }
        if (arguments != null) {
            val result = requireArguments().getString("Result")
            if (result != null) {
                pdfUrl = result

                if (pdfUrl.contains(".pdf")) {
                    binding.webView.visibility = View.VISIBLE
                    //  RetrievePDFFromURL(binding.idPDFView).execute(pdfUrl)
                    binding.webView.webViewClient = WebViewClient()
                    binding.webView.settings.setSupportZoom(true)
                    binding.webView.settings.javaScriptEnabled = true
                    binding.webView.settings.builtInZoomControls = true;
                    binding.webView.settings.displayZoomControls = false;
                    val url = pdfUrl
                    binding.webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$url")
                    Log.e(
                        "TAG",
                        "onCreateView: ++++bcfhdsb  " + "https://docs.google.com/gview?embedded=true&url=$url"
                    )
                } else if (pdfUrl.contains(".png") or pdfUrl.contains(".jpg")) {
                    binding.idimageView.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(pdfUrl).placeholder(R.drawable.loading)
                        .into(binding.idimageView)
                } else {
                    binding.root.errorSnack(getString(R.string.invalid_doc))
                }
            }
        }

        return binding.root
    }

    /* class RetrievePDFFromURL(pdfView: PDFView) :
         AsyncTask<String, Void, InputStream>() {
         val mypdfView: PDFView = pdfView

         @Deprecated("Deprecated in Java")
         override fun doInBackground(vararg params: String?): InputStream? {
             var inputStream: InputStream? = null
             try {
                 val url = URL(params.get(0))
                 val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
                 if (urlConnection.responseCode == 200) {
                     inputStream = BufferedInputStream(urlConnection.inputStream)
                 }
             } catch (e: Exception) {
                 e.printStackTrace()
                 return null; }
             return inputStream; }

         @Deprecated("Deprecated in Java", ReplaceWith("my pdfView.fromStream(result).load()"))
         override fun onPostExecute(result: InputStream?) {
             mypdfView.fromStream(result).load()
         }
     }*/
}