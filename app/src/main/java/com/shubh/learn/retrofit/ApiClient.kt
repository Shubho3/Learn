package com.shubh.learn.retrofit

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import okhttp3.*
import okhttp3.CipherSuite.Companion.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
import okhttp3.CipherSuite.Companion.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256
import okhttp3.CipherSuite.Companion.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class ApiClient {


    companion object {
        var BASE_URL: String = "https://sebateks.pl/sebateks/webservice/"

        //  var BASE_URL: String = "https://myasp-app.com/vibras/webservice/"
        private var retrofit: Retrofit? = null
        private val httpClient = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        fun getClient(context: Context): Retrofit? {
            val spec: ConnectionSpec =
                ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                        TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
                    ).build()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            /*val sslContext: SSLContext =
                SslUtils.getSslContextForCertificateFile(context, "BPClass2RootCA-sha2.cer")
*/
            val client = httpClient
                .addInterceptor(interceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .certificatePinner(CertificatePinner.DEFAULT)
                //.socketFactory(SSLContext.getDefault().socketFactory)
                .connectionSpecs(Collections.singletonList(spec))
                .build()

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }

        fun hasNetwork(context: Context): Boolean? {
            var isConnected: Boolean? = false // Initial Value
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected)
                isConnected = true
            return isConnected
        }

        private fun getUnsafeOkHttpClient(mContext: Context) :
                OkHttpClient.Builder? {
            var mCertificateFactory : CertificateFactory =
                CertificateFactory.getInstance("X.509")
            var mInputStream = mContext.resources.openRawResource(com.shubh.learn.R.raw.success)
            var mCertificate : Certificate = mCertificateFactory.generateCertificate(mInputStream)
            mInputStream.close()
            val mKeyStoreType = KeyStore.getDefaultType()
            val mKeyStore = KeyStore.getInstance(mKeyStoreType)
            mKeyStore.load(null, null)
            mKeyStore.setCertificateEntry("ca", mCertificate)
            val mTmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val mTrustManagerFactory = TrustManagerFactory.getInstance(mTmfAlgorithm)
            mTrustManagerFactory.init(mKeyStore)
            val mTrustManagers = mTrustManagerFactory.trustManagers
            val mSslContext = SSLContext.getInstance("SSL")
            mSslContext.init(null, mTrustManagers, null)
            val mSslSocketFactory = mSslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(mSslSocketFactory, mTrustManagers[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
            return builder
        }

    }


}