package com.example.moneytracker.domain.model

class CategoryModel(
    val id: Int = 0,
    val name: String,
    val imgPath: String? = null
){
    override fun toString() = "CategoryModel<$id>\nname: $name, img: $imgPath"
}