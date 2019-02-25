package com.sango.lunchi.util

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.sango.core.util.GlideApp
import com.sango.core.util.px
import com.sango.lunchi.R

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("imageUrlRoundedCorners")
    fun ImageView.download(url: String?) {
        GlideApp.with(this)
            .load(getLogoUrl())
            .diskCacheStrategy(DiskCacheStrategy.ALL) // for cache
            .placeholder(R.drawable.ic_lunchi)
            .fallback(R.drawable.ic_lunchi)
            .transforms(CenterCrop(), RoundedCorners(8.px()))
            .into(this)
    }

    private fun getLogoUrl(): String {
        val logoList = listOf(
            "https://logopond.com/logos/ccd39cc0bdc163d99a6811105942b500.png",
            "https://1.bp.blogspot.com/-Bzy6IbyZSOc/Vw0jFsbPabI/AAAAAAAAQw8/fFM31Tmwgfs6mebfMGuAQM1iJ460_LsCwCLcB/s1600/restaruantes%252B%2B%25282%2529.png",
            "https://logopond.com/logos/7b9a839bad4002d45604d2e9cde8ad86.png",
            "https://logopond.com/logos/54e153e5a7416a2d87a587ff39c1f586.png",
            "https://logopond.com/logos/350f713c4327e0cb41897b34478e5d29.png",
            "https://images-platform.99static.com/vYC8TVkBgXWDULgzOjcfBMY3dxM=/fit-in/900x675/99designs-contests-attachments/51/51270/attachment_51270458",
            "https://cdn.dribbble.com/users/1205252/screenshots/2998945/hugo.png",
            "https://seeklogo.com/images/C/casa-sua-restaurante-logo-0A623207BE-seeklogo.com.jpg",
            "http://www.alcuza.es/wp-content/uploads/2017/12/logo-retina.jpg",
            "http://jorgelessin.com/wp-content/uploads/2013/10/logo18.jpg",
            "http://www.ofifacil.com/ideas-ejemplos/ideas-ejemplos-logos/ideas-logos-restaurantes/10-TriBaker.jpg",
            "http://jorgelessin.com/wp-content/uploads/2013/10/logo23.jpg",
            "https://us.123rf.com/450wm/elenapodolny/elenapodolny1711/elenapodolny171100007/89403092-dise%C3%B1o-de-concepto-de-logotipo-creativo-abstracto-para-restaurantes-.jpg?ver=6",
            "https://media-cdn.tripadvisor.com/media/photo-s/0e/b1/46/99/logotipo-restaurante.jpg",
            "http://duchessrestaurants.com/images/Duchess%20new%20logo%20color.jpg",
            "https://assets3.domestika.org/project-items/001/326/303/diner_g-big.jpg?1434203276",
            "https://d1jgln4w9al398.cloudfront.net/imagens/ce/wl/www.sindelantal.mx/logo-snippet.png",
            "http://www.brandemia.org/sites/default/files/inline/images/logo_fosters_despues.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTJgOEOxCl0wfd_-W2nLyUEjpmSPTjbNg4lyIO727PK6_tvAqGS",
            "https://i.pinimg.com/originals/a6/3a/f4/a63af446d01262d9447f4ef772a62404.jpg"
        )
        return logoList[(0..19).shuffled().first()]
    }
}