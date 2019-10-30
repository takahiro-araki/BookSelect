package com.example.bookselect

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ボタンがクリックされたらAPIを叩く（AsyncTaskクラスを使って、非同期処理を行う）
        //今回は、isbnを検索条件にして検索する本をランダムで選出するため、ランダム化する処理も使う
        button.setOnClickListener {


            //国会図書館
            /*       HitAPITask().execute(http://iss.ndl.go.jp/api/sru?operation=searchRetrieve&query=isbn=9784101006017)*/

            //googleBooks いける
            HitAPITask().execute("https://www.googleapis.com/books/v1/volumes?q=isbn:9784101006017")


        }


        //インナークラス（本当は別クラスに作る）
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
                    reader = BufferedReader(InputStreamReader(stream))
                    buffer = StringBuffer()
                    var line: String?
                    while (true) {

                        line = reader.readLine()
                        if (line == null) {
                            break
                        }
                        buffer.append(line)
                    }


                    //この後、ストリングバッファー型のレスポンスパラメーターをどうやってパースするか
                    //json形式のgoogleの方を先にやる
                    val jsonText = buffer.toString()


                    //jsonオブジェクトに格納
                    val parentJsonObj = JSONObject(jsonText)
                    println("検証　　　" + parentJsonObj)
                    //テキストに表示するオブジェクトをとってくる
                    val itemsJson = parentJsonObj.getJSONArray("items")
                    val itemsArrayJson = itemsJson.getJSONObject(0)
                    val titleJson: String =
                        itemsArrayJson.getJSONObject("volumeInfo").getString("title")
                    val publishedDateJson: String =
                        itemsArrayJson.getJSONObject("volumeInfo").getString("publishedDate")
                    /*   val discriptionJson:String=itemsArrayJson.getJSONObject("volumeInfo").getString("description")*/
                    val authorsJson =
                        itemsArrayJson.getJSONObject("volumeInfo").getJSONArray("authors")
                            .getString(0)

                    val text: String =
                        "あなたが読むべき本は？！\n\nタイトル：『$titleJson』\n著者：$authorsJson\n出版年：$publishedDateJson\n"


                    return text


                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } finally {
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

                //処理が１行なので、省略

                if (result == null) return
                textView.text = result


            }

        }


    }
}

