package com.lagradost.quicknovel.providers

import com.lagradost.quicknovel.*
import com.lagradost.quicknovel.MainActivity.Companion.app
import org.jsoup.Jsoup

class IndoWebNovelProvider : MainApi() {
    override val name = "IndoWebNovel"
    override val lang = "id"
    override val mainUrl = "https://indowebnovel.id/id"
    override val iconId = R.drawable.ic_indowebnovel
    override val mainCategories: List<Pair<String, String>> = listOf()
    override val orderBys: List<Pair<String, String>> = listOf()
    override val tags: List<Pair<String, String>> = listOf()
    
    override suspend fun loadMainPage(
        page: Int = 1,
        mainCategory: String?,
        orderBy: String?,
        tag: String?,
    ): HeadMainPageResponse {
        val url = "$mainUrl/page/$page/?s"
        val doc = app.get(url)
          .document
          .select("flexbox2-item")
        return getResponse(this.apiName, doc)
    }

    open suspend fun search(query: String): List<SearchResponse>? {
        doc = app.get("$mainUrl/?s=$query")
          .document
          .select(".flexbox2-item")
        return getResponse(this.apiName, doc)
    }

    open suspend fun load(url: String): LoadResponse? {
        doc = app.get(url).document.selectFirst("series-flex")
        return LoadResponse(
          url: String = url
          name: String = doc.selectFirst("series-title span").text
          author: String? = doc.select("series-infolist span").next().text
          posterUrl: String? = doc.selectFirst(".series-thumb > img").attr("src")
          rating: Int? = doc.attr("[itemProp=ratingValue]").text.toRate()
          synopsis: String? = doc.selectFirst("series-synops > p").text.clean()
          status: Int? = doc.selectFirst(".status").text.toStatus()
          // 0 = null - implemented but not found, 1 = Ongoing, 2 = Complete, 3 = Pause/HIATUS, 4 = Dropped
          apiName: String = this.apiName
          // related : List<SearchResponse>?
        )
    }

    open suspend fun loadHtml(url: String): String? {
        return app.get(url)
          .document
          .selectFirst(".readersss")
          .html()
    }
}

fun getResponse (api, doc) {
  return doc.mapOrNull {
    SearchResponse (
      apiName = api
      name = doc.selectFirst("flexbox2-title").text
      url = doc.selectFirst("a").attr("href")
      posterUrl = selectFirst("flexbox2-thumb img").attr("src")
    )
  }
}