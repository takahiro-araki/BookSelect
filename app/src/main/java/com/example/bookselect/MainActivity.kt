package com.example.bookselect


import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bookselect.R.id.imageView
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ボタンがクリックされたらAPIを叩く（AsyncTaskクラスを使って、非同期処理を行う）
        button.setOnClickListener {
            var apiUrl = StringBuilder()
            apiUrl.append("https://api.openbd.jp/v1/get?isbn=")
            var isbn = this.doRandom()
            apiUrl.append(isbn)
            HitAPITask().execute(apiUrl.toString())
            /*    //openBD ISBNの全件検索ができる*/
            /*  https://api.openbd.jp/v1/coverage*/
        }
    }
    inner class HitAPITask : AsyncTask<String, String, String>() {
        //APIを叩く
        override fun doInBackground(vararg params: String?): String? {

            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null
            val buffer: StringBuffer
            try {
                val url = URL(params[0])
                connection = url.openConnection() as HttpURLConnection

                //リクエストメソッドを指定してみる
                connection.requestMethod = "GET"
                connection.connect() //指定したAPIを叩く
                /*      //レスポンスを受け取る
            var response=connection.responseCode as HttpURLConnection*/
                //返ってきたデータを使えるようにする
                val stream = connection.inputStream
                reader = BufferedReader(InputStreamReader(stream,"UTF-8"))
                buffer = StringBuffer()
                var line: String?
                while (true) {

                    line = reader.readLine()
                    if (line == null) {
                        break
                    }
                    buffer.append(line)
                }
                //json形式から値を取得する
                val preJsonText:String = buffer.toString()
                println("ジェイソンテキスト検証　"+preJsonText)
                //でたらめなisbnによって書籍情報が取れなかった場合に、もう一度呼び出す
                if (preJsonText == "[null]") {
                   return null
                }
                val jsonText=preJsonText.substring(1,(preJsonText.length-1))
                //jsonオブジェクトに格納
                val parentJsonObj = JSONObject(jsonText)

                //テキストに表示するオブジェクトをとってくる(上から、タイトル、出版日、あらすじ、著者、画像)
                val titleJson: String =
                    parentJsonObj.getJSONObject("summary").getString("title")
                val publishedDateJson: String =
                    parentJsonObj.getJSONObject("summary").getString("pubdate")
                val authorsJson =
                    parentJsonObj.getJSONObject("summary").getString("author")
                val discriptionJson =
                    parentJsonObj.getJSONObject("onix").getJSONObject("CollateralDetail").getJSONArray("TextContent")
                        .getJSONObject(0).getString("Text")
                var imgJson =
                    parentJsonObj.getJSONObject("summary").getString("cover")

                //取得した値を配列に格納しリターン
                val text: String? =
                    "\n\nタイトル：『$titleJson』\n\n著者：$authorsJson\n出版年：$publishedDateJson\nあらすじ：$discriptionJson$imgJson"
                return text
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }/*catch (e:RuntimeException){
                e.printStackTrace()
            }*/
            finally {
                connection?.disconnect()
                try {
                    reader?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return null
        }

        //結果をテキストビューに格納する.
        override fun onPostExecute(result: String?) {

            super.onPostExecute(result)

            println("リゾルト検証" + result)
            //リゾルトがnullなら、ボタンを押してリターン
            if (result == null) {
                button.callOnClick()
                return
            }

            //引数resultの末尾に入っている画像イメージurlを抜き出す
            var url: String? = "http"
            var urlPoint: Int = result.toString().indexOf(url.toString())
            var imgUrl:String?=null
            var text:String?=null

            //画像が入っていなかったらnoImage画像を、入っていたらその画像を入れる
            if(urlPoint==-1){
                text = result.toString()
                textView.text = result
                imageView.setImageResource(R.drawable.noimage)
            }else{
             imgUrl=result.substring(urlPoint)
             text=result.substring(0,result.toString().indexOf(url.toString()))
            //テキストビューに注入
            textView.text = text
            /*   Picasso.with(imageView.context).load(result).into(imageView)*/
            //イメージビューに注入
            var matchImage: ImageView = findViewById(R.id.imageView)
            Glide.with(application).load(imgUrl).into(matchImage)}

        }
    }

    //ISBNをランダムで出す
    fun doRandom(): String {
        //ランダムな12桁の数を生成
        var numMap: MutableMap<Int, Int> = mutableMapOf(13 to 9, 12 to 7, 11 to 8, 10 to 4)
        val random = Random
        val preArray = arrayOf(2, 3, 4, 5, 6, 7, 8, 9)
        for (i in preArray) {
            val randomNum: Int = random.nextInt(10)
            numMap.put(i, randomNum)
        }
        //マップを回して、チェックデジットを算出する
        val array = arrayOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)
        var totalNum: Int = 0
        for (j in array) {
            if (j % 2 == 0) {
                var evenNum = (numMap.get(j) as Int) * 3 as Int
                totalNum += evenNum
            } else {
                var oddNum = numMap.get(j) as Int as Int
                totalNum += oddNum
            }
        }
        //足し合わせた数字の1の位を取得し、チェックデジットを算出
        var stringTotalNum = totalNum.toString()
        var lastNum = stringTotalNum.substring(stringTotalNum.length - 1)
        var checkNum = 10 - lastNum.toInt()
        numMap.put(1, checkNum)

        //マップを回して、StringのISBNを取得
        var isbn = StringBuilder()
        val numArray = arrayOf(13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1)
        for (k in numArray) {
            isbn.append(numMap.get(k).toString())
        }
        println("ＩＳＢＮの検証　　" + isbn.toString())

        return isbn.toString()

    }
}

