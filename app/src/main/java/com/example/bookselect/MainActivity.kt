package com.example.bookselect


import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        //ボタンがクリックされたらAPIを叩く（AsyncTaskクラスを使って、非同期処理を行う）
        //今回は、isbnを検索条件にして検索する本をランダムで選出するため、ランダム化する処理も使う
        button.setOnClickListener {



            //国会図書館
            /*       HitAPITask().execute(http://iss.ndl.go.jp/api/sru?operation=searchRetrieve&query=isbn=9784101006017)*/

        /*  https://api.openbd.jp/v1/coverage*/
            //limit offset
            //googleBooks いける
            HitAPITask().execute("https://www.googleapis.com/books/v1/volumes?q=isbn:9784102003039")
        /*    //openBD ISBNの全件検索ができる
            HitAPITask().execute("https://api.openbd.jp/v1/get?isbn=9784101006017")*/

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

                //json形式から値を取得する
                val jsonText = buffer.toString()
                //jsonオブジェクトに格納
                val parentJsonObj = JSONObject(jsonText)
                println("検証　　　" + parentJsonObj)
                //テキストに表示するオブジェクトをとってくる(上から、タイトル、出版日、あらすじ、著者、画像)
                val itemsArrayJson=parentJsonObj.getJSONArray("items").getJSONObject(0)
                val titleJson: String =
                    itemsArrayJson.getJSONObject("volumeInfo").getString("title")
                val publishedDateJson: String =
                    itemsArrayJson.getJSONObject("volumeInfo").getString("publishedDate")
                   val discriptionJson:String=itemsArrayJson.getJSONObject("volumeInfo").getString("description")
                val authorsJson =
                    itemsArrayJson.getJSONObject("volumeInfo").getJSONArray("authors")
                        .getString(0)
                val imgJson=itemsArrayJson.getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail")






                val text: String =
                    "あなたが読むべき本は？！\n\nタイトル：『$titleJson』\n\n著者：$authorsJson\n出版年：$publishedDateJson\nあらすじ：$discriptionJson"
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

            //処理が１行なので、省略
            if (result == null) return
            textView.text = result
         /*   Picasso.with(imageView.context).load(result).into(imageView)*/

            var matchImage:ImageView=findViewById(R.id.imageView)
            var imgUrl:String="https://books.google.com/books/content?id=nR4mrgEACAAJ&printsec=frontcover&img=1&zoom=5&source=gbs_api"
            Glide.with(application).load(imgUrl).into(matchImage)


        }
    }


    }



/*
{
    "kind": "books#volumes",
    "totalItems": 1,
    "items": [
    {
        "kind": "books#volume",
        "id": "nR4mrgEACAAJ",
        "etag": "CDtKcZGlu/Y",
        "selfLink": "https://www.googleapis.com/books/v1/volumes/nR4mrgEACAAJ",
        "volumeInfo": {
        "title": "ジキルとハイド",
        "authors": [
        "ロバート・L. スティーヴンソン"
        ],
        "publishedDate": "2015-02-01",
        "description": "ロンドンの高名な紳士、ジキル博士の家にある時からハイドという男が出入りし始めた。彼は肌の青白い小男で不愉快な笑みをたたえ、人にかつてない嫌悪、さらには恐怖を抱かせるうえ、ついに殺人事件まで起こしてしまう。しかし、実はジキルが薬物によって邪悪なハイドへと姿を変えていたのだった...。人間の心に潜む善と悪の葛藤を描き、二重人格の代名詞としても名高い怪奇小説。",
        "industryIdentifiers": [
        {
            "type": "ISBN_10",
            "identifier": "4102003037"
        },
        {
            "type": "ISBN_13",
            "identifier": "9784102003039"
        }
        ],
        "readingModes": {
        "text": false,
        "image": false
    },
        "pageCount": 153,
        "printType": "BOOK",
        "maturityRating": "NOT_MATURE",
        "allowAnonLogging": false,
        "contentVersion": "preview-1.0.0",
        "imageLinks": {
        "smallThumbnail": "http://books.google.com/books/content?id=nR4mrgEACAAJ&printsec=frontcover&img=1&zoom=5&source=gbs_api",
        "thumbnail": "http://books.google.com/books/content?id=nR4mrgEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api"
    },
        "language": "ja",
        "previewLink": "http://books.google.co.jp/books?id=nR4mrgEACAAJ&dq=isbn:9784102003039&hl=&cd=1&source=gbs_api",
        "infoLink": "http://books.google.co.jp/books?id=nR4mrgEACAAJ&dq=isbn:9784102003039&hl=&source=gbs_api",
        "canonicalVolumeLink": "https://books.google.com/books/about/%E3%82%B8%E3%82%AD%E3%83%AB%E3%81%A8%E3%83%8F%E3%82%A4%E3%83%89.html?hl=&id=nR4mrgEACAAJ"
    },
        "saleInfo": {
        "country": "JP",
        "saleability": "NOT_FOR_SALE",
        "isEbook": false
    },
        "accessInfo": {
        "country": "JP",
        "viewability": "NO_PAGES",
        "embeddable": false,
        "publicDomain": false,
        "textToSpeechPermission": "ALLOWED",
        "epub": {
        "isAvailable": false
    },
        "pdf": {
        "isAvailable": false
    },
        "webReaderLink": "http://play.google.com/books/reader?id=nR4mrgEACAAJ&hl=&printsec=frontcover&source=gbs_api",
        "accessViewStatus": "NONE",
        "quoteSharingAllowed": false
    },
        "searchInfo": {
        "textSnippet": "ロンドンの高名な紳士、ジキル博士の家にある時からハイドという男が出入りし始めた。彼は肌の青白い小男で不愉快な笑みをたたえ、人にかつてない嫌悪、さらには恐怖を抱か ..."
    }
    }
    ]
}


*/
