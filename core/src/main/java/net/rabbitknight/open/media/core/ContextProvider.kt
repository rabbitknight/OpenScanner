package net.rabbitknight.open.media.core

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.core.content.ContentProviderCompat


internal class ContextProvider : ContentProvider() {
    companion object {

        private lateinit var provider: ContentProvider

        /**
         * 获取全局上下文
         */
        @SuppressLint("StaticFieldLeak")
        val context = ContentProviderCompat.requireContext(provider).applicationContext
    }

    override fun onCreate(): Boolean {
        provider = this
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = -1

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = -1
}