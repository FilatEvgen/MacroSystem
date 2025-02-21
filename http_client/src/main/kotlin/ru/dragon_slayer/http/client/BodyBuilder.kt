package ru.dragon_slayer.http.client

interface IBodyBuilder {
    fun bodyContent(): ByteArray
    fun contentType(): ContentType
}

class BodyBuilderUrlEncodedForm : IBodyBuilder {
    companion object {
        fun build(params: List<Pair<String, String>>): BodyBuilderUrlEncodedForm {
            return BodyBuilderUrlEncodedForm().apply { params(params) }
        }
    }

    private var urlParamBuilder: IUrlParamBuilder = UrlParamBuilder()
    fun params(params: List<Pair<String, String>>) {
        params.forEach { setOf(urlParamBuilder.param(it.first, it.second)) }
    }

    override fun contentType() = ContentTypes.APPLICATION.X_WWW_FORM_URLENCODED

    override fun bodyContent(): ByteArray {
        return urlParamBuilder.build().toByteArray(Charsets.UTF_8)
    }
}
