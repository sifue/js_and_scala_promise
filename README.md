JavaScriptにもScalaにもPromiseという機能がありますが、全く内容が違って、しかもScalaのFutureがむしろJavaScriptのPromiseに似た機能なのでそれを整理します。

基本的には、[言語によってちょっと違うFuture/Promiseをまとめてみた](http://qiita.com/reki2000/items/6acf94a07dee8d26a744)のシリーズの内容をはしょったものです。

まずはJavaScriptのPromiseは、非同期処理自体を抽象化したものとなっております。しかも、thenというPromiseを更に関数を適用して得たでPromiseでPromiseChainを書くことも可能です。

詳しくは、Azuさんの[Promiseの本](http://azu.github.io/promises-book/)が詳しく書かれていますが、簡単な実装例で言うと、

```js
function doubleUp(value) {
    return value * 2;
}
function increment(value) {
    return value + 1;
}
function output(value) {
    console.log(value);
}
var promise = new Promise(function (resolve) {
        resolve(1);
});
promise
    .then(increment)
    .then(doubleUp)
    .then(output) // 4が出力される
    .catch(function(error){
        console.error(error);
    });
```
このようなPromiseをチェーンして書いていく方法が書けます。thenが関数を適用すると適用した非同期処理に対するPromiseを返してくれるのでこのようにチェーンして書くことができます。また例外情報を保有しているので、これらの何処かで発生した例外に対して例外処理を書くことができます。

これと似た処理をScalaで実装する場合、Futureを使うことになります。

```scala
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

object MainFuture extends App {
  def double(value: Int): Int = value * 2
  def increment(value: Int): Int = value + 1
  def output(value: Int): Unit  = Console.println(value)

  val future = Future {1}
  (for {
    i1 <- future
    i2 <- Future {increment(i1)}
    i3 <- Future {double(i2)}
  } yield output(i3)) // 4が出力される
    .onFailure { case t: Throwable =>
      Console.println(t.getMessage)
    }

  Thread.sleep(1000)
}
```

同じことが書けます。わざと、`val future = Future {1}`と書いてJavaScriptにフォーマットを似せて書きました。ほぼJavaScriptの実装例と同様の動きをします。Futureは非同期処理というよりは、非同期処理によって得られる未来を抽象化したオブジェクトです。本来ScalaのFutureは、宣言した瞬間に別スレッドでその処理を実行しますが、今回のforの中で宣言しているため逐次処理されます。例外処理も、どこかのFutureで例外が発生すれば、その途中で発生した例外に対してonFailureが呼ばれます。

つまり、JavaScriptのPromiseとScalaのFutureは似ています。

複数のPromise/Futureで最初に完了したものを取得する、のようなお便利メソッド(Promise.race/Future.firstCompleteOf)も両言語とも持ち合わせています。

では、ScalaのPromiseはなんでしょうか。これは、一度だけ結果を代入可能なFutureです。使い方は、

```scala
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

object MainPromise extends App {
  def output(value: Int): Unit  = Console.println(value)

  val promise = Promise[Int]
  Future {
    promise.trySuccess(0)
  }
  Future {
    Thread.sleep(100)
    promise.trySuccess(1)
  }
  promise.future.map(output) // 先に終了する0が出力される

  Thread.sleep(1000)
}
```

以上の例のように利用します。上記の例では、複数のFutureを合成して先に結果が出た方をFutureとして定義しています。さらに複数のPromiseを利用して複雑なFutureな処理(例えば、カウントダウンラッチのようは並行部品)を実現することも可能です。

というわけで、JavaScriptとScalaのPromise、全然違いますね。
皆さん、気をつけましょう。

